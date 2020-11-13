package kan.eclipsetrader.charts.indicators.volptax;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.graphics.RGB;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.charts.internal.VerticalScaleValue;
import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.DateTimeUtils;
import net.sourceforge.eclipsetrader.core.Functions;
import net.sourceforge.eclipsetrader.core.IHistoryFeed;
import net.sourceforge.eclipsetrader.core.TimeInterval;
import net.sourceforge.eclipsetrader.core.TrydConfig;
import net.sourceforge.eclipsetrader.core.TrydConfigKey;
import net.sourceforge.eclipsetrader.core.db.Bar;
import net.sourceforge.eclipsetrader.core.db.BarData;
import net.sourceforge.eclipsetrader.core.db.History;
import net.sourceforge.eclipsetrader.core.db.PlotLineType;
import net.sourceforge.eclipsetrader.core.db.Security;

public class VolPtaxIndicator extends IndicatorPlugin implements IHistoryFeed.IHistoryFeedMonitor
{
    static final RGB DEFAULT_UPPER_LINE_COLLOR;
    static final RGB DEFAULT_LOWER_LINE_COLLOR;

    static final VerticalScaleValue DEFAULT_V_SCALE_VALUE;
    
    private PlotLineType lineTypeUpperBoundary;
    private int lineThicknessUpperBoundary;
    private RGB lineColorUpperBoundary;
    
    private PlotLineType lineTypeLowerBoundary;
    private int lineThicknessLowerBoundary;
    private RGB lineColorLowerBoundary;

    private Object textSide;
    private VerticalScaleValue verticalScaleValue;

	private DateTimeUtils dateTimeUtils = DateTimeUtils.getInstance();
	
	private boolean updatingHistory = false;
	
//	private boolean isReplayMode;
	
//	private boolean historyDataNotAvailable;

	private Security ptaxPartialsSecurity;
	
	private Double ptaxVolMax;
	
	private Double ptaxVolMin;
	
	private Double ptaxVol;
	
	private Date dateRefPtaxVol;
	
//	private long timeOpenMarket10hMillis;
	
    static {
    	DEFAULT_UPPER_LINE_COLLOR = new RGB(0, 255, 0);
    	DEFAULT_LOWER_LINE_COLLOR = new RGB(255, 0, 0);
        (DEFAULT_V_SCALE_VALUE = VerticalScaleValue.createDefault()).setShowValue(true);
        VolPtaxIndicator.DEFAULT_V_SCALE_VALUE.setFractionDigits(2);
    }
    
    public VolPtaxIndicator() {
    	this.lineTypeUpperBoundary = VolPtaxIndicator.DEFAULT_PLOT_LINE_TYPE;
        this.lineThicknessUpperBoundary = VolPtaxIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorUpperBoundary = VolPtaxIndicator.DEFAULT_UPPER_LINE_COLLOR;

        this.lineTypeLowerBoundary = VolPtaxIndicator.DEFAULT_PLOT_LINE_TYPE;
        this.lineThicknessLowerBoundary = VolPtaxIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorLowerBoundary = VolPtaxIndicator.DEFAULT_LOWER_LINE_COLLOR;

        this.textSide = VolPtaxIndicator.DEFAULT_TEXT_SIDE;
        this.verticalScaleValue = DEFAULT_V_SCALE_VALUE;
        
        final String ptaxSecuritiesConfig = TrydConfig.getInstance().getString(TrydConfigKey.PTAX__SECURITIES, "");
        final String[] splitConfig = ptaxSecuritiesConfig.split(";");
        String securityCode = "PTXUSDV";

        if (splitConfig.length > 0 && !splitConfig[0].trim().isEmpty()) {
            securityCode = splitConfig[0].trim();
        }

        this.ptaxPartialsSecurity = Functions.getSecurity(securityCode);
//        this.isReplayMode = CorePlugin.getDefault().isReplayMode();
    }
    
    @Override
    protected void doCalculate() {

		if (this.getBarData().size() == 0) {
			return;
		}

		if (this.updatingHistory) {
			return;
		}

//		if (this.historyDataNotAvailable) {
//			return;
//		}
		
		Date graphEndDate = getBarData().getEnd();
		Date truncGraphEndDate = truncDateByTimeInterval(graphEndDate, TimeInterval.DAILY);
		
		if (dateRefPtaxVol == null || !dateTimeUtils.isSameDay(dateRefPtaxVol, graphEndDate)) {
			History history = ptaxPartialsSecurity.getHistory(TimeInterval.DAILY);
			if (history.size() == 0) {
				Functions.logMessage("Empty PTAX history data");
				loadHistory(ptaxPartialsSecurity, TimeInterval.DAILY);
				return;
			}
			
			BarData ptaxHistoryBarData = new BarData(history.getList(), adjusted);
			
			if (ptaxHistoryBarData != null && ptaxHistoryBarData.size() > 1) {
				Calendar dayBefore = Calendar.getInstance();
				dayBefore.setTime(graphEndDate);
				dayBefore.add(Calendar.DATE, -1);
				
				// filtra o historico ate o pregao anterior para garantir que a ultima barra corresponda ao pregao anterior
				ptaxHistoryBarData = ptaxHistoryBarData.getPeriod(ptaxHistoryBarData.getBegin(), truncDateByTimeInterval(dayBefore.getTime(), TimeInterval.DAILY), adjusted);
				
				Bar previousPtaxBarData = ptaxHistoryBarData.get(ptaxHistoryBarData.size()-1);
				this.ptaxVol = (previousPtaxBarData.getHigh(adjusted) - previousPtaxBarData.getLow(adjusted)) * 1000;
				this.dateRefPtaxVol = graphEndDate;
				Calendar timeOpenMarket10h = Calendar.getInstance();
				timeOpenMarket10h.setTime(truncGraphEndDate);
				timeOpenMarket10h.set(Calendar.HOUR_OF_DAY, 10);
//				this.timeOpenMarket10hMillis = timeOpenMarket10h.getTimeInMillis();
			} else {
				return;
			}
		}
		
		final PlotLine plotLineUpperBoundary;

		(plotLineUpperBoundary = new PlotLine()).setType(this.lineTypeUpperBoundary);
		plotLineUpperBoundary.setLineWidth(this.lineThicknessUpperBoundary);
		plotLineUpperBoundary.setColor(this.lineColorUpperBoundary);
		plotLineUpperBoundary.setTextSide(IndicatorPlugin.DEFAULT_TEXT_SIDE.getClass().cast(this.textSide));
		
		String label = "Vol Ptax max";
		
		plotLineUpperBoundary.setLabel(label);
		plotLineUpperBoundary.setText(label);

    	final PlotLine plotLineLowerBoundary;
		(plotLineLowerBoundary = new PlotLine()).setType(this.lineTypeLowerBoundary);
		plotLineLowerBoundary.setLineWidth(this.lineThicknessLowerBoundary);
		plotLineLowerBoundary.setColor(this.lineColorLowerBoundary);
		plotLineLowerBoundary.setTextSide(IndicatorPlugin.DEFAULT_TEXT_SIDE.getClass().cast(this.textSide));
		
		label = "Vol Ptax min";
		
		plotLineLowerBoundary.setLabel(label);
		plotLineLowerBoundary.setText(label);
		
		BarData openMarketBarData = getBarData().getPeriod(truncGraphEndDate, graphEndDate, adjusted);
		
		if (openMarketBarData != null && openMarketBarData.size() > 0) {
			
			//if ((openMarketBarData.getMax() - openMarketBarData.getMin()) < this.ptaxVol) {
//				this.ptaxVolMin = Functions.roundIntoSecurityIncrementPrice(openMarketBarData.getMax() - this.ptaxVol, this.getBarData().getSecurity());
//				this.ptaxVolMax = Functions.roundIntoSecurityIncrementPrice(openMarketBarData.getMin() + this.ptaxVol, this.getBarData().getSecurity());
			//}
			
			Double previousMax = Double.NEGATIVE_INFINITY;
			Double previousMin = Double.POSITIVE_INFINITY;
			
			for (int j = 0; j <openMarketBarData.size(); j++) {
				if (dateTimeUtils.isSameDay(openMarketBarData.getDate(j), graphEndDate)) {
					Bar currentBar = openMarketBarData.get(j);
					if (currentBar.getHigh(adjusted) > previousMax) {
						previousMax = currentBar.getHigh(adjusted);
						this.ptaxVolMin = Functions.roundIntoSecurityIncrementPrice(previousMax - this.ptaxVol, this.getBarData().getSecurity());
					}
					if (currentBar.getLow(adjusted) < previousMin) {
						previousMin = currentBar.getLow(adjusted);
						this.ptaxVolMax = Functions.roundIntoSecurityIncrementPrice(previousMin + this.ptaxVol, this.getBarData().getSecurity());
					}
					
//					if (openMarketBarData.getDate(j).getTime() <= this.timeOpenMarket10hMillis) {
						plotLineUpperBoundary.append(this.ptaxVolMax);
						plotLineLowerBoundary.append(this.ptaxVolMin);
//					}
				} else {
					break;
				}
			}

			this.getOutput().add(plotLineUpperBoundary);
			this.getOutput().add(plotLineLowerBoundary);

//			this.getOutput().setShowValueVerticalScale(this.verticalScaleValue);
//			this.getOutput().setScaleLevel(ScaleLevel.SIMPLE);
		}
		
    }

	private void loadHistory(Security security, TimeInterval interval) {
		Functions.logMessage("Loading history for " + security.getCode());

		//		if (this.isReplayMode) {
//			Functions.logMessage("Replay mode. Could not load history data.");
//			this.historyDataNotAvailable = true;
//		}
		
		if (this.updatingHistory) {
			return;
		}
        final IHistoryFeed feed = CorePlugin.createHistoryFeedPlugin(security.getDataSource().getHistoryId());
        if (feed != null) {
            feed.updateHistory(security, interval, false, this);
            feed.loadLocalLog(security, interval);
            this.updatingHistory = true;
        }
	}

    @Override
    public void doSetParameters(final Settings settings) {
    	
        this.lineTypeLowerBoundary = settings.getPlotLineType("lineTypeLowerBoundary", PlotLineType.DASH);
        this.lineThicknessLowerBoundary = settings.getInteger("lineThicknessLowerBoundary", VolPtaxIndicator.DEFAULT_LINE_THICKNESS);
        this.lineColorLowerBoundary = settings.getColor("lineColorLowerBoundary", VolPtaxIndicator.DEFAULT_LOWER_LINE_COLLOR);

        this.lineTypeUpperBoundary = settings.getPlotLineType("lineTypeUpperBoundary", PlotLineType.DASH);
        this.lineThicknessUpperBoundary = settings.getInteger("lineThicknessUpperBoundary", VolPtaxIndicator.DEFAULT_LINE_THICKNESS);
        this.lineColorUpperBoundary = settings.getColor("lineColorUpperBoundary", VolPtaxIndicator.DEFAULT_UPPER_LINE_COLLOR);

        this.textSide = settings.getTextSide("textSide", VolPtaxIndicator.DEFAULT_TEXT_SIDE);
        
        this.verticalScaleValue.setShowValue(settings.getBoolean("showData", VolPtaxIndicator.DEFAULT_V_SCALE_VALUE.isShowValue()));
        this.verticalScaleValue.setFractionDigits(settings.getInteger("fraction", VolPtaxIndicator.DEFAULT_V_SCALE_VALUE.getFractionDigits()));
        this.verticalScaleValue.setShorten(settings.getBoolean("shorten", VolPtaxIndicator.DEFAULT_V_SCALE_VALUE.isShorten()));
    }

    
	@Override
	public void historyUpdateFinished() {
		Functions.logMessage("historyUpdateFinished()");
		this.updatingHistory = false;
	}

    private Date truncDateByTimeInterval(Date dt, TimeInterval timeInterval) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		if (timeInterval.equals(TimeInterval.MONTHLY)) {
			cal.set(Calendar.DAY_OF_MONTH, 0);
		} else if (timeInterval.equals(TimeInterval.WEEKLY)) {
			cal.set(Calendar.DAY_OF_MONTH, 1);
		}
		return cal.getTime();
    }

}
