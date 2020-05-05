package kan.eclipsetrader.charts.indicators.vtc;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.graphics.RGB;
import org.osgi.service.prefs.Preferences;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.ScaleLevel;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.charts.ch;
import net.sourceforge.eclipsetrader.charts.internal.VerticalScaleValue;
import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.DateTimeUtils;
import net.sourceforge.eclipsetrader.core.EnhancedObservableList;
import net.sourceforge.eclipsetrader.core.Functions;
import net.sourceforge.eclipsetrader.core.db.BarData;
import net.sourceforge.eclipsetrader.core.db.Chart;
import net.sourceforge.eclipsetrader.core.db.NewsItem;
import net.sourceforge.eclipsetrader.core.db.PlotLineType;
import net.sourceforge.eclipsetrader.core.db.Security;
import net.sourceforge.eclipsetrader.news.NewsPlugin;

public class VtcIndicator extends IndicatorPlugin
{

	public static String PLUGIN_ID = "kan.eclipsetrader.charts.indicators.vtc";

	public static final String VTC_SAVED_DATE = "VTC_SAVED_DATE";
	public static final String VTC_SAVED_VALUE = "VTC_SAVED_VALUE";
	
    static final VerticalScaleValue DEFAULT_V_SCALE_VALUE;

    static final RGB DEFAULT_LINE_COLLOR;
    
    static final long NEWS_FEED_RESTART_INTERVAL_MS = 1000 * 60 * 3; // 3 MINUTES
    
    private NewsPlugin newsDefaultPlugin = NewsPlugin.getDefault();
    
    private PlotLineType lineTypeVtc;
    private int lineThicknessVtc;
    private RGB lineColorVtc;

    private PlotLineType lineTypeVariacaoPositiva;
    private int lineThicknessVariacaoPositiva;
    private RGB lineColorVariacaoPositiva;
    
    private PlotLineType lineTypeVariacaoNegativa;
    private int lineThicknessVariacaoNegativa;
    private RGB lineColorVariacaoNegativa;

    private Double valueVtc;
    
    private ch textSide;
    private VerticalScaleValue verticalScaleValue;

    private EnhancedObservableList.IListObserver listObserver;

	private Pattern vtcTitleMessagePattern;

    private String vencimentoDollar;
    
    private Date vtcDateFound = null;

	private boolean getValueFromNews;
	
	private DateTimeUtils dateTimeUtils = DateTimeUtils.getInstance();

	private Double valueVtcVariacaoPositiva;

	private Double valueVtcVariacaoNegativa;
	
	private boolean flagLoadedFromPrefs = false;
	
	private long lastNewsFeedRestart;
	
	private Date loadedPrefsVtcDate;

	private Double loadedPrefsVtcValue;
	
	private boolean isReplayMode;

    static {
        DEFAULT_LINE_COLLOR = new RGB(255, 201, 14);
        (DEFAULT_V_SCALE_VALUE = VerticalScaleValue.createDefault()).setShowValue(true);
        VtcIndicator.DEFAULT_V_SCALE_VALUE.setFractionDigits(2);
    }
    
    public VtcIndicator() {
        this.lineTypeVtc = PlotLineType.DASH;
        this.lineThicknessVtc = VtcIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorVtc = VtcIndicator.DEFAULT_LINE_COLLOR;

    	this.lineTypeVariacaoPositiva = VtcIndicator.DEFAULT_PLOT_LINE_TYPE;
        this.lineThicknessVariacaoPositiva = VtcIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorVariacaoPositiva = VtcIndicator.DEFAULT_LINE_COLLOR;

        this.lineTypeVariacaoNegativa = VtcIndicator.DEFAULT_PLOT_LINE_TYPE;
        this.lineThicknessVariacaoNegativa = VtcIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorVariacaoNegativa = VtcIndicator.DEFAULT_LINE_COLLOR;

        this.valueVtc = 0.0;
        
        this.textSide = VtcIndicator.DEFAULT_TEXT_SIDE;
        this.verticalScaleValue = DEFAULT_V_SCALE_VALUE;
        
        this.isReplayMode = CorePlugin.getDefault().isReplayMode();
    }
    
    @Override
	protected void doCalculate() {

		if (this.getBarData() != null || this.getBarData().size() > 0) {

			initVencimentoDollar();

//          Functions.logMessage("doCalculate() " + vtcDateFound + " - " + valueVtc);

			if (this.getValueFromNews) {
				if (lastNewsFeedRestart == 0) {
					lastNewsFeedRestart = (new Date()).getTime() - (1000 * 60 * 2);
				}
				setValueFromLoadedPreferences();
				checkNewsForVtc();
			}

			if (vtcDateFound != null || valueVtc > 0) {
				final PlotLine plotLineVariacaoPositiva;
				final PlotLine plotLineVariacaoNegativa;
				final PlotLine plotLineVtc;

				calculateVtcVariations();

				plotLineVtc = new PlotLine();
				plotLineVariacaoPositiva = new PlotLine();
				plotLineVariacaoNegativa = new PlotLine();

				Date dateToCompare = null;
				if (this.getValueFromNews) {
					dateToCompare = this.vtcDateFound;
				} else {
					dateToCompare = getBarData().getEnd();
				}

				BarData barData = this.getBarData();
				for (int j = 0; j < barData.size(); ++j) {
					if (dateTimeUtils.isSameDay(barData.get(j).getDate(), dateToCompare)) {
						plotLineVtc.append(this.valueVtc);
						plotLineVariacaoPositiva.append(this.valueVtcVariacaoPositiva);
						plotLineVariacaoNegativa.append(this.valueVtcVariacaoNegativa);
					}
				}

				if (plotLineVtc.getSize() > 0) {

					plotLineVtc.setType(this.lineTypeVtc);
					plotLineVtc.setLineWidth(this.lineThicknessVtc);
					plotLineVtc.setColor(this.lineColorVtc);
					plotLineVtc.setTextSide(this.textSide);

					plotLineVariacaoPositiva.setType(this.lineTypeVariacaoPositiva);
					plotLineVariacaoPositiva.setLineWidth(this.lineThicknessVariacaoPositiva);
					plotLineVariacaoPositiva.setColor(this.lineColorVariacaoPositiva);
					plotLineVariacaoPositiva.setTextSide(this.textSide);

					plotLineVariacaoNegativa.setType(this.lineTypeVariacaoNegativa);
					plotLineVariacaoNegativa.setLineWidth(this.lineThicknessVariacaoNegativa);
					plotLineVariacaoNegativa.setColor(this.lineColorVariacaoNegativa);
					plotLineVariacaoNegativa.setTextSide(this.textSide);

					plotLineVtc.setLabel("VTC");
					plotLineVtc.setText("VTC");

					plotLineVariacaoPositiva.setLabel("VTC +0,5%");
					plotLineVariacaoPositiva.setText("VTC +0,5%");

					plotLineVariacaoNegativa.setLabel("VTC -0,5%");
					plotLineVariacaoNegativa.setText("VTC -0,5%");

					this.getOutput().add(plotLineVariacaoPositiva);
					this.getOutput().add(plotLineVariacaoNegativa);
					this.getOutput().add(plotLineVtc);

					this.getOutput().setShowValueVerticalScale(this.verticalScaleValue);
					this.getOutput().setScaleLevel(ScaleLevel.SIMPLE);
				}

			}
		}
	}
    
    @Override
    public void doSetParameters(final Settings settings) {
    	
//        Functions.logMessage("doSetParameters()");

    	this.getValueFromNews = settings.getBoolean("getValueFromNews", true);

    	if (!this.getValueFromNews) {
        	this.valueVtc = settings.getInteger("vtcValue", 0).doubleValue();
    	} else if (!flagLoadedFromPrefs) {
    		loadVtcValueFromPreferences();
    	}
    	
        this.lineTypeVtc = settings.getPlotLineType("lineTypeVtc", PlotLineType.DASH);
        this.lineThicknessVtc = settings.getInteger("lineThicknessVtc", VtcIndicator.DEFAULT_LINE_THICKNESS);
        this.lineColorVtc = settings.getColor("lineColorVtc", VtcIndicator.DEFAULT_LINE_COLLOR);

        this.lineTypeVariacaoNegativa = settings.getPlotLineType("lineTypeVariacaoNegativa", VtcIndicator.DEFAULT_PLOT_LINE_TYPE);
        this.lineThicknessVariacaoNegativa = settings.getInteger("lineThicknessVariacaoNegativa", VtcIndicator.DEFAULT_LINE_THICKNESS);
        this.lineColorVariacaoNegativa = settings.getColor("lineColorVariacaoNegativa", VtcIndicator.DEFAULT_LINE_COLLOR);
        
        this.lineTypeVariacaoPositiva = settings.getPlotLineType("lineTypeVariacaoPositiva", VtcIndicator.DEFAULT_PLOT_LINE_TYPE);
        this.lineThicknessVariacaoPositiva = settings.getInteger("lineThicknessVariacaoPositiva", VtcIndicator.DEFAULT_LINE_THICKNESS);
        this.lineColorVariacaoPositiva = settings.getColor("lineColorVariacaoPositiva", VtcIndicator.DEFAULT_LINE_COLLOR);
        
        this.textSide = settings.getTextSide("textSide", VtcIndicator.DEFAULT_TEXT_SIDE);
        
        this.verticalScaleValue.setShowValue(settings.getBoolean("showData", VtcIndicator.DEFAULT_V_SCALE_VALUE.isShowValue()));
        this.verticalScaleValue.setFractionDigits(settings.getInteger("fraction", VtcIndicator.DEFAULT_V_SCALE_VALUE.getFractionDigits()));
        this.verticalScaleValue.setShorten(settings.getBoolean("shorten", VtcIndicator.DEFAULT_V_SCALE_VALUE.isShorten()));
        
    }
    
    @Override
    public boolean isApplicable(final Chart chart, final StringBuilder sb) {
        boolean b = true;
        final Security security;
        if (chart != null && (security = chart.getSecurity()) != null && !security.isDollar()) {
            b = false;
            sb.append(Messages.VtcIndicator_NotApplicableMessage);
        }
        return b;
    }
    
    private void checkNewsForVtc() {
    	
    	if (this.isReplayMode) {
    		return;
    	}

    	if (vtcDateFound != null && !dateTimeUtils.isSameDay(vtcDateFound, getBarData().getEnd())) {
    		vtcDateFound = null;
    	}

    	if (vtcDateFound == null) {
    		long currentTimeMillis = (new Date()).getTime();
    		if (!newsDefaultPlugin.newsViewIsOpened() && CorePlugin.getDefault().isFeedRunning() && currentTimeMillis > (lastNewsFeedRestart + NEWS_FEED_RESTART_INTERVAL_MS)) {
        		Functions.logMessage("Stopping News Feed");
        		newsDefaultPlugin.stopFeed();
        		Functions.logMessage("Starting News Feed");
        		newsDefaultPlugin.startFeed();
        		lastNewsFeedRestart = (new Date()).getTime();
            }
    		EnhancedObservableList<NewsItem> allNewsList = CorePlugin.getRepository().allNews();
        	if (!allNewsList.isEmpty()) {
        		CopyOnWriteArrayList<NewsItem> allNews = new CopyOnWriteArrayList<NewsItem>(CorePlugin.getRepository().allNews());        		
            	for (Iterator<NewsItem> iterator = allNews.iterator(); iterator.hasNext();) {
            		proccessNewsMessage(iterator.next());
				}
            }
    	}
    	
    	if (vtcDateFound == null) {
        	initNewsObserver();
    	}
    }
    
    private void initVencimentoDollar() {
    	if (vencimentoDollar == null) {
        	Security security = getBarData().getSecurity();

        	Pattern p = Pattern.compile("[A-Z]{3}([FGHJKMNQUVXZ][0-9]{2})");
            Matcher m = p.matcher(security.getCode());
            if (m.find()) {
            	vencimentoDollar = m.group(1);
            	vtcTitleMessagePattern =  Pattern.compile("Call\\s+para\\s+a\\s+serie\\s+VTC"
            			+ vencimentoDollar
            			+ ".*\\s+Futuro\\s+([0-9]{3,}\\.[0-9]+)\\s+.*");
            }
    	}
    }
    
    private void initNewsObserver() {
        if (this.listObserver != null) {
        	return ;
        }

    	this.listObserver = (EnhancedObservableList.IListObserver)new EnhancedObservableList.IListObserver() {

			@Override
			public void allItemsRemoved() {
			}

			@Override
			public void itemAdded(Observable observable) {
                final List<Observable> observables = new ArrayList<Observable>();
                observables.add(observable);
                this.itemsAdded(observables);
			}

			@Override
			public void itemAdded(int index, Observable observable) {
				itemAdded(observable);
			}

			@Override
			public void itemChanged(Observable observable) {
			}

			@Override
			public void itemRemoved(Observable observable) {
			}

			@Override
			public void itemsAdded(Collection<? extends Observable> list) {
                for (final Observable observable : list) {
                    final NewsItem newsItem = (NewsItem)observable;
                	Functions.logMessage("News added: " + newsItem.getTitle());
            		proccessNewsMessage(newsItem);
                }
			}

			@Override
			public void itemsRemoved(List<Observable> list) {
			}
        	
        };
        CorePlugin.getRepository().allNews().addObserver(this.listObserver);
    }
    
    private void proccessNewsMessage(NewsItem newsItem) {
    	if (vtcDateFound == null && newsItem != null && getBarData() != null && dateTimeUtils.isSameDay(newsItem.getDate(), getBarData().getEnd())) {
        	String vtcStringValue = "";
        	try {
    			Matcher matcher = vtcTitleMessagePattern.matcher(newsItem.getTitle());
    			
    			if (matcher.find()) {
    				Functions.logMessage("Notícia VTC encontrada: " + newsItem.getTitle());
    				vtcStringValue = matcher.group(1);
    				valueVtc = Double.parseDouble(vtcStringValue);
    				vtcDateFound = getBarData().getEnd();
    				valueVtc = Functions.roundIntoSecurityIncrementPrice(valueVtc, getBarData().getSecurity());
    				Functions.logMessage("Valor de VTC encontrado na notícia: " + valueVtc);
    				saveVtcValue();

    	            CorePlugin.getRepository().allNews().deleteObserver(this.listObserver);
    	            this.listObserver = null;
    			}
    		} catch (NumberFormatException e) {
    			Functions.logError("Erro ao extrair valor VTC do título da notícia: " + valueVtc, e);
    		}
    	}
    	
    }
    
    private void calculateVtcVariations() {
    	if (this.valueVtcVariacaoPositiva == null && this.valueVtcVariacaoNegativa == null) {
            this.valueVtcVariacaoPositiva = Functions.roundIntoSecurityIncrementPrice(this.valueVtc + (this.valueVtc * 0.005), getBarData().getSecurity());
            this.valueVtcVariacaoNegativa = Functions.roundIntoSecurityIncrementPrice(this.valueVtc - (this.valueVtc * 0.005), getBarData().getSecurity());
    	}
    }
    
    @Override
    public void dispose() {
    	if (!newsDefaultPlugin.newsViewIsOpened()) {
        	CorePlugin.getRepository().allNews().deleteObserver(this.listObserver);
    	}
    	super.dispose();
    }
    
    private void setValueFromLoadedPreferences() {
    	if (this.loadedPrefsVtcDate != null && this.loadedPrefsVtcValue != null && getBarData() != null && dateTimeUtils.isSameDay(this.loadedPrefsVtcDate, getBarData().getEnd())) {
			this.vtcDateFound = this.loadedPrefsVtcDate;
			this.valueVtc = this.loadedPrefsVtcValue;
    	}
    }
    
	private void saveVtcValue() {
		try {
			Preferences prefs = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
			this.loadedPrefsVtcDate = this.vtcDateFound;
			this.loadedPrefsVtcValue = this.valueVtc;
			prefs.put(VTC_SAVED_DATE, dateTimeUtils.format(this.vtcDateFound, "dd/MM/yyyy"));
			prefs.putDouble(VTC_SAVED_VALUE, this.valueVtc);
			prefs.flush();
		} catch (Exception e) {
			Functions.logError("Error saving VTC Indicator preferences", e);
		}
	}
	
	private void loadVtcValueFromPreferences() {
		if (flagLoadedFromPrefs) {
			return;
		}
		try {
			Preferences prefs = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
			String dateStr = prefs.get(VTC_SAVED_DATE, null);
			Double savedVtcValue = prefs.getDouble(VTC_SAVED_VALUE, -1);

			if (dateStr != null && !dateStr.trim().isEmpty() && savedVtcValue > 0) {
				this.loadedPrefsVtcDate = dateTimeUtils.parse(dateStr.trim(), "dd/MM/yyyy");
				this.loadedPrefsVtcValue = savedVtcValue;
				setValueFromLoadedPreferences();
			}
		} catch (NumberFormatException e) {
			this.vtcDateFound = null;
			this.valueVtc = null;
			Functions.logError("Error reading VTC_SAVED_DATE from preferences", e);
		} catch (ParseException e) {
			this.vtcDateFound = null;
			this.valueVtc = null;
			Functions.logError("Error reading VTC_SAVED_VALUE from preferences", e);
		} finally {
			flagLoadedFromPrefs = true;
		}
	}

}
