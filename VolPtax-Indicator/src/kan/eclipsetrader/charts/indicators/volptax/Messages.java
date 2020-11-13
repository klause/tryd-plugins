package kan.eclipsetrader.charts.indicators.volptax;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	public static String VolPtaxPreferencePage_Show;
    public static String VolPtaxPreferencePage_LabelSide;
    
    public static String VolPtaxPreferencePage_LineType_UpperBoundary;
    public static String VolPtaxPreferencePage_LineColor_UpperBoundary;
    public static String VolPtaxPreferencePage_LineType_LowerBoundary;
    public static String VolPtaxPreferencePage_LineColor_LowerBoundary;

    static {
        NLS.initializeMessages("kan.eclipsetrader.charts.indicators.volptax.messages", Messages.class);
    }
    
    private Messages() {
    }
}
