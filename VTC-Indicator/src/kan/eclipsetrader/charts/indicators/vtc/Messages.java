package kan.eclipsetrader.charts.indicators.vtc;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    public static String VtcIndicatorPreferencePage_LineType_Vtc;
	public static String VtcIndicatorPreferencePage_LineColor_Vtc;
	public static String VtcIndicatorPreferencePage_Show;
    public static String VtcIndicatorPreferencePage_LabelSide;
    public static String VtcIndicator_NotApplicableMessage;
    public static String VtcIndicatorPreferencePage_LabelCallValue;
    public static String VtcIndicatorPreferencePage_LabelDeltaValue;
    public static String VtcIndicatorPreferencePage_LineColor_VariacaoPositiva;
    public static String VtcIndicatorPreferencePage_LineColor_VariacaoNegativa;
    public static String VtcIndicatorPreferencePage_LineType_VariacaoPositiva;
    public static String VtcIndicatorPreferencePage_LineType_VariacaoNegativa;
    public static String VtcIndicatorPreferencePage_GetValueFromNews;
    
    static {
        NLS.initializeMessages("kan.eclipsetrader.charts.indicators.vtc.messages", Messages.class);
    }
    
    private Messages() {
    }
}
