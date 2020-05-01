package kan.eclipsetrader.charts.indicators.vwapvariation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.RGB;

import net.sourceforge.eclipsetrader.charts.GroupingMode;
import net.sourceforge.eclipsetrader.charts.Indicator;
import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.ScaleLevel;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.charts.ch;
import net.sourceforge.eclipsetrader.charts.indicators.VWAP;
import net.sourceforge.eclipsetrader.charts.internal.VerticalScaleValue;
import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.DateTimeUtils;
import net.sourceforge.eclipsetrader.core.Functions;
import net.sourceforge.eclipsetrader.core.IHistoryFeed;
import net.sourceforge.eclipsetrader.core.TimeInterval;
import net.sourceforge.eclipsetrader.core.db.Bar;
import net.sourceforge.eclipsetrader.core.db.BarData;
import net.sourceforge.eclipsetrader.core.db.History;
import net.sourceforge.eclipsetrader.core.db.PlotLineType;
import net.sourceforge.eclipsetrader.core.db.Security;

public class VwapVariationIndicator extends IndicatorPlugin implements IHistoryFeed.IHistoryFeedMonitor
{
    static final RGB DEFAULT_LINE_COLLOR;
    static final VerticalScaleValue DEFAULT_V_SCALE_VALUE;
    
    private PlotLineType lineTypeVwap;
    private int lineThicknessVwap;
    private RGB lineColorVwap;

    private PlotLineType lineTypePositiveVariations;
    private int lineThicknessPositiveVariations;
    private RGB lineColorPositiveVariations;
    
    private PlotLineType lineTypeNegativeVariations;
    private int lineThicknessNegativeVariations;
    private RGB lineColorNegativeVariations;

    private ch textSide;
    private VerticalScaleValue verticalScaleValue;

	private DateTimeUtils dateTimeUtils = DateTimeUtils.getInstance();
	
	private Double vwapValue;

	private Date graphDateRef;
	
	private Map<Float, Double> vwapVariationValues;
	
	private TimeInterval workingTimeInterval;
	private TimeInterval vwapRefTimeInterval;
	
	private boolean updatingHistory = false;
	
    static {
        DEFAULT_LINE_COLLOR = new RGB(255, 201, 14);
        (DEFAULT_V_SCALE_VALUE = VerticalScaleValue.createDefault()).setShowValue(true);
        VwapVariationIndicator.DEFAULT_V_SCALE_VALUE.setFractionDigits(2);
    }
    
    public VwapVariationIndicator() {
        this.lineTypeVwap = PlotLineType.DASH;
        this.lineThicknessVwap = VwapVariationIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorVwap = VwapVariationIndicator.DEFAULT_LINE_COLLOR;

    	this.lineTypePositiveVariations = VwapVariationIndicator.DEFAULT_PLOT_LINE_TYPE;
        this.lineThicknessPositiveVariations = VwapVariationIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorPositiveVariations = VwapVariationIndicator.DEFAULT_LINE_COLLOR;

        this.lineTypeNegativeVariations = VwapVariationIndicator.DEFAULT_PLOT_LINE_TYPE;
        this.lineThicknessNegativeVariations = VwapVariationIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorNegativeVariations = VwapVariationIndicator.DEFAULT_LINE_COLLOR;

        this.textSide = VwapVariationIndicator.DEFAULT_TEXT_SIDE;
        this.verticalScaleValue = DEFAULT_V_SCALE_VALUE;
    }
    
    @Override
    protected void doCalculate() {

		if (this.getBarData().size() == 0) {
			return;
		}
		
		Date endDate = this.getBarData().getEnd();
		
		if (needRecalculation()) {
			
			this.vwapValue = null;
			this.graphDateRef = null;
			this.workingTimeInterval = this.getBarData().getInterval();
			this.vwapRefTimeInterval = getVwapRefTimeInterval(); 
			
			Security security = this.getBarData().getSecurity();

			BarData vwapRefBarData = null;

			History history = this.getBarData().getSecurity().getHistory(vwapRefTimeInterval);
			if (history.size() == 0) {
				Functions.logMessage("Empty history data");
				loadHistory(security, this.vwapRefTimeInterval);
				return;
			}
			
			vwapRefBarData = new BarData(history.getList(), adjusted);

//			Date endDateTrunc = truncDateByTimeInterval(endDate, this.vwapRefTimeInterval);
			
			vwapRefBarData = vwapRefBarData.getPeriod(vwapRefBarData.getBegin(), endDate, adjusted);

			if (vwapRefBarData.size() < 2 || !isSamePeriod(vwapRefBarData.getEnd(), endDate, this.workingTimeInterval)) {
				Functions.logMessage("No enough history data");
				loadHistory(security, this.vwapRefTimeInterval);
				return;
			}
			
			this.updatingHistory = false;
			
			List<Bar> tmpBars = new ArrayList<Bar>(1);
			tmpBars.add(vwapRefBarData.get(vwapRefBarData.size()-2));
			
			vwapRefBarData = new BarData(tmpBars, adjusted);
			vwapRefBarData.setSecurity(security);

			this.vwapValue = calculateVwapValue(vwapRefBarData, security, GroupingMode.NONE);
			
			if (this.vwapValue != null) {
				this.graphDateRef = this.getBarData().getEnd();
				
		        vwapVariationValues = new HashMap<Float, Double>(9);
				vwapVariationValues.put(0.5F, Functions.roundIntoSecurityIncrementPrice(this.vwapValue + (0.005 * this.vwapValue), security));
				vwapVariationValues.put(1.0F, Functions.roundIntoSecurityIncrementPrice(this.vwapValue + (0.010 * this.vwapValue), security));
				vwapVariationValues.put(1.5F, Functions.roundIntoSecurityIncrementPrice(this.vwapValue + (0.015 * this.vwapValue), security));
				vwapVariationValues.put(2.0F, Functions.roundIntoSecurityIncrementPrice(this.vwapValue + (0.020 * this.vwapValue), security));

				vwapVariationValues.put(-0.5F, Functions.roundIntoSecurityIncrementPrice(this.vwapValue - (0.005 * this.vwapValue), security));
				vwapVariationValues.put(-1.0F, Functions.roundIntoSecurityIncrementPrice(this.vwapValue - (0.010 * this.vwapValue), security));
				vwapVariationValues.put(-1.5F, Functions.roundIntoSecurityIncrementPrice(this.vwapValue - (0.015 * this.vwapValue), security));
				vwapVariationValues.put(-2.0F, Functions.roundIntoSecurityIncrementPrice(this.vwapValue - (0.020 * this.vwapValue), security));
			}

		}
		
		if (this.vwapValue != null) {
	    	final Indicator output = this.getOutput();

	    	final PlotLine plotLineVwap;

			(plotLineVwap = new PlotLine()).setType(this.lineTypeVwap);
			plotLineVwap.setLineWidth(this.lineThicknessVwap);
			plotLineVwap.setColor(this.lineColorVwap);
			plotLineVwap.setTextSide(this.textSide);
			
			String vwapLineLabel = getVwapLineLabel(this.vwapRefTimeInterval);
			
			plotLineVwap.setLabel(vwapLineLabel);
			plotLineVwap.setText(vwapLineLabel);

			BarData barData = this.getBarData();
			for (int j = barData.size()-1; j >=0; j--) {
				if (this.isSamePeriod(barData.get(j).getDate(), this.graphDateRef, this.workingTimeInterval)) {
					plotLineVwap.append(this.vwapValue);
				} else {
					break;
				}
			}
			output.setMainPlotLine(plotLineVwap);
			
			for (Entry<Float, Double> entry : vwapVariationValues.entrySet()) {
				PlotLine varPlotLine = createVwapVariationPlotLine(entry.getKey(), entry.getValue());
				output.add(varPlotLine);
			}
			
	        output.setShowValueVerticalScale(this.verticalScaleValue);
	        output.setScaleLevel(ScaleLevel.SIMPLE);
		}
		
    }

	private Double calculateVwapValue(BarData vwapRefBarData, Security security, GroupingMode mode) {
		
		Double result = null;
		
		VWAP vwap;
		(vwap = new VWAP()).setInput(vwapRefBarData, true, new ArrayList<Object>());
		final Settings parameters;
		(parameters = new Settings()).set("groupingMode", mode.ordinal());
		vwap.setParameters(parameters);
		vwap.calculate();
		
		if (vwap.getOutput() != null && vwap.getOutput().size() > 0) {
			PlotLine plotLine = vwap.getOutput().get(0);
			this.vwapValue = plotLine.getDouble(plotLine.getSize() - 1);
			vwap.dispose();
			result = Functions.roundIntoSecurityIncrementPrice(this.vwapValue, security);
		}
		
		return result;
	}
    
    private String getVwapLineLabel(TimeInterval timeInterval) {
		if (timeInterval.equals(TimeInterval.MONTHLY)) {
			return Messages.VwapVarIndicator_LineLabel_LastMonthClose;
		} else if (timeInterval.equals(TimeInterval.WEEKLY)) {
			return Messages.VwapVarIndicator_LineLabel_LastWeekClose;
		}
		return Messages.VwapVarIndicator_LineLabel_LastDayClose;
    }
    
	private void loadHistory(Security security, TimeInterval interval) {
		if (this.updatingHistory) {
			return;
		}
        final IHistoryFeed feed = CorePlugin.createHistoryFeedPlugin(security.getDataSource().getHistoryId());
        if (feed != null) {
            feed.updateHistory(security, interval, false, this);
            feed.loadLocalLog(security, interval);
            this.updatingHistory = true;
        }
        
        
        
//        final CountDownLatch countDownLatch = new CountDownLatch(1);
//
//        try {
//            countDownLatch.await(5L, TimeUnit.SECONDS);
//        }
//        catch (InterruptedException ex) {}
//        for (int n = 1; feed.isWaitingForRealTimeData() && n <= 10; ++n) {
//            try {
//                this.wait(500L);
//            }
//            catch (InterruptedException ex2) {
//                return;
//            }
//        }
	}

	private PlotLine createVwapVariationPlotLine(float variation, double value) {
		BarData barData = this.getBarData();
		
    	final PlotLine plotLine;

		(plotLine = new PlotLine()).setType(variation < 0 ? this.lineTypeNegativeVariations : this.lineTypePositiveVariations);
		plotLine.setLineWidth(variation < 0 ? this.lineThicknessNegativeVariations : this.lineThicknessPositiveVariations);
		plotLine.setColor(variation < 0 ? this.lineColorNegativeVariations : this.lineColorPositiveVariations);
		plotLine.setTextSide(this.textSide);
		String label = String.format("VWAP %+.2f%%", variation);
		plotLine.setLabel(label);
		plotLine.setText(label);

		for (int j = barData.size()-1; j >=0; j--) {
			if (this.isSamePeriod(barData.get(j).getDate(), this.graphDateRef, this.workingTimeInterval)) {
				plotLine.append(value);
			}
		}
		
		return plotLine;
	}
    
    @Override
    public void doSetParameters(final Settings settings) {
    	
        this.lineTypeVwap = settings.getPlotLineType("lineTypeVwap", PlotLineType.DASH);
        this.lineThicknessVwap = settings.getInteger("lineThicknessVwap", VwapVariationIndicator.DEFAULT_LINE_THICKNESS);
        this.lineColorVwap = settings.getColor("lineColorVwap", VwapVariationIndicator.DEFAULT_LINE_COLLOR);
        
        this.lineTypeNegativeVariations = settings.getPlotLineType("lineTypeNegativeVariations", PlotLineType.DASH);
        this.lineThicknessNegativeVariations = settings.getInteger("lineThicknessNegativeVariations", VwapVariationIndicator.DEFAULT_LINE_THICKNESS);
        this.lineColorNegativeVariations = settings.getColor("lineColorNegativeVariations", VwapVariationIndicator.DEFAULT_LINE_COLLOR);

        this.lineTypePositiveVariations = settings.getPlotLineType("lineTypePositiveVariations", PlotLineType.DASH);
        this.lineThicknessPositiveVariations = settings.getInteger("lineThicknessPositiveVariations", VwapVariationIndicator.DEFAULT_LINE_THICKNESS);
        this.lineColorPositiveVariations = settings.getColor("lineColorPositiveVariations", VwapVariationIndicator.DEFAULT_LINE_COLLOR);

        this.textSide = settings.getTextSide("textSide", VwapVariationIndicator.DEFAULT_TEXT_SIDE);
        
        this.verticalScaleValue.setShowValue(settings.getBoolean("showData", VwapVariationIndicator.DEFAULT_V_SCALE_VALUE.isShowValue()));
        this.verticalScaleValue.setFractionDigits(settings.getInteger("fraction", VwapVariationIndicator.DEFAULT_V_SCALE_VALUE.getFractionDigits()));
        this.verticalScaleValue.setShorten(settings.getBoolean("shorten", VwapVariationIndicator.DEFAULT_V_SCALE_VALUE.isShorten()));
        
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
    
    private TimeInterval getVwapRefTimeInterval() {
    	TimeInterval graphTimeInterval = getBarData().getInterval();
    	
    	if (graphTimeInterval.equals(TimeInterval.DAILY)) {
    		return TimeInterval.WEEKLY;
    	} else if (graphTimeInterval.equals(TimeInterval.WEEKLY)) {
    		return TimeInterval.MONTHLY;
    	}
    	
    	return TimeInterval.DAILY;
    }
    
    private boolean needRecalculation() {
		TimeInterval currentGraphTimeInterval = this.getBarData().getInterval();

		if (this.vwapValue == null || !currentGraphTimeInterval.equals(this.workingTimeInterval)) {
			return true;
		}

		if ((currentGraphTimeInterval.isIntraday() || currentGraphTimeInterval.isTimed()) && !dateTimeUtils.isSameDay(graphDateRef, this.getBarData().getEnd())) {
			return true;
		}
		
		if (currentGraphTimeInterval.equals(TimeInterval.DAILY) && !this.isSameWeek(graphDateRef, this.getBarData().getEnd())) {
			return true;
		}

		if (currentGraphTimeInterval.equals(TimeInterval.WEEKLY) && !this.isSameMonth(graphDateRef, this.getBarData().getEnd())) {
			return true;
		}
		return false;
    }
    
    private boolean isSamePeriod(Date left, Date right, TimeInterval timeInterval) {
		if ((timeInterval.isIntraday() || !timeInterval.isTimed()) && dateTimeUtils.isSameDay(left, right)) {
			return true;
		}
		
		if (timeInterval.equals(TimeInterval.DAILY) && this.isSameWeek(left, right)) {
			return true;
		}

		if (timeInterval.equals(TimeInterval.WEEKLY) && this.isSameMonth(left, right)) {
			return true;
		}
		return false;
    }
    
    public boolean isSameWeek(final Date left, final Date right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        Calendar calendarLeft;
        (calendarLeft = Calendar.getInstance()).setTime(left);

        Calendar calendarRight;
        (calendarRight = Calendar.getInstance()).setTime(right);
        
        if (calendarLeft.get(Calendar.YEAR) == calendarRight.get(Calendar.YEAR) && calendarLeft.get(Calendar.WEEK_OF_YEAR) == calendarRight.get(Calendar.WEEK_OF_YEAR)) {
        	return true;
        }
        return false;
    }
    
    public boolean isSameMonth(final Date left, final Date right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        Calendar calendarLeft;
        (calendarLeft = Calendar.getInstance()).setTime(left);

        Calendar calendarRight;
        (calendarRight = Calendar.getInstance()).setTime(right);
        
        if (calendarLeft.get(Calendar.YEAR) == calendarRight.get(Calendar.YEAR) && calendarLeft.get(Calendar.MONTH) == calendarRight.get(Calendar.MONTH)) {
        	return true;
        }
        return false;
    }

	@Override
	public void historyUpdateFinished() {
		Functions.logMessage("historyUpdateFinished()");
		this.updatingHistory = false;
	}

}
