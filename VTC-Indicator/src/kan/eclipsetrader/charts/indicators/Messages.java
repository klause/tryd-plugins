package kan.eclipsetrader.charts.indicators;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    public static String VtcIndicatorPreferencePage_Show;
    public static String VtcIndicatorPreferencePage_LabelSide;
    public static String VtcIndicator_NotApplicableMessage;
    public static String VtcIndicatorPreferencePage_Value;
    public static String VtcIndicatorPreferencePage_LineColor_VariacaoPositiva;
    public static String VtcIndicatorPreferencePage_LineColor_VariacaoNegativa;
    public static String VtcIndicatorPreferencePage_LineType_VariacaoPositiva;
    public static String VtcIndicatorPreferencePage_LineType_VariacaoNegativa;

    
    static {
        NLS.initializeMessages("kan.eclipsetrader.charts.indicators.messages", Messages.class);
    }
    
    private Messages() {
    }
}
