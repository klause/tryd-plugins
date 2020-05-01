package kan.eclipsetrader.charts.indicators.vwapvariation;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	public static String VwapVarPreferencePage_Show;
    public static String VwapVarPreferencePage_LabelSide;
    
    public static String VwapVarIndicatorPreferencePage_Label_VwapRef;
    public static String VwapVarIndicatorPreferencePage_VwapRef_LastClose;
    public static String VwapVarIndicatorPreferencePage_VwapRef_LastWeek;
    public static String VwapVarIndicatorPreferencePage_VwapRef_LastMonth;

    public static String VwapVarPreferencePage_LineType_Vwap;
    public static String VwapVarPreferencePage_LineColor_Vwap;
    public static String VwapVarPreferencePage_LineType_PositiveVariations;
    public static String VwapVarPreferencePage_LineColor_PositiveVariations;
    public static String VwapVarPreferencePage_LineType_NegativeVariations;
    public static String VwapVarPreferencePage_LineColor_NegativeVariations;

	public static String VwapVarIndicator_LineLabel_LastMonthClose;
	public static String VwapVarIndicator_LineLabel_LastWeekClose;
	public static String VwapVarIndicator_LineLabel_LastDayClose;

    static {
        NLS.initializeMessages("kan.eclipsetrader.charts.indicators.vwapvariation.messages", Messages.class);
    }
    
    private Messages() {
    }
}
