package kan.eclipsetrader.charts.indicators.volptax;

import org.eclipse.swt.widgets.Composite;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.IndicatorPluginPreferencePage;
import net.sourceforge.eclipsetrader.core.db.PlotLineType;
import net.sourceforge.eclipsetrader.core.ui.widgets.FlatCombo;

public class VolPtaxIndicatorPreferencePage extends IndicatorPluginPreferencePage
{
    @Override
    protected void doCreateFullContents(final Composite composite) {
    	
        addLineTypeSelector(composite, "lineTypeUpperBoundary", Messages.VolPtaxPreferencePage_LineType_UpperBoundary, PlotLineType.DASH, "lineThicknessUpperBoundary", 1);
        addColorSelector(composite, "lineColorUpperBoundary", Messages.VolPtaxPreferencePage_LineColor_UpperBoundary,  VolPtaxIndicator.DEFAULT_UPPER_LINE_COLLOR);

        addLineTypeSelector(composite, "lineTypeLowerBoundary", Messages.VolPtaxPreferencePage_LineType_LowerBoundary, PlotLineType.DASH, "lineThicknessLowerBoundary", 1);
        addColorSelector(composite, "lineColorLowerBoundary", Messages.VolPtaxPreferencePage_LineColor_LowerBoundary,  VolPtaxIndicator.DEFAULT_LOWER_LINE_COLLOR);

        addTextSideSelector(composite, Messages.VolPtaxPreferencePage_LabelSide, "textSide", IndicatorPlugin.DEFAULT_TEXT_SIDE);
        addShowValueControl(composite, VolPtaxIndicator.DEFAULT_V_SCALE_VALUE);
        
    }
    
	@Override
	protected void doFillLineTypeComboSelector(FlatCombo combo) {
        combo.setData(Integer.toString(combo.getItemCount()), PlotLineType.LINE);
        combo.add(PlotLineType.LINE.getName());
        combo.setData(Integer.toString(combo.getItemCount()), PlotLineType.DOT);
        combo.add(PlotLineType.DOT.getName());
        combo.setData(Integer.toString(combo.getItemCount()), PlotLineType.DASH);
        combo.add(PlotLineType.DASH.getName());
        combo.setData(Integer.toString(combo.getItemCount()), PlotLineType.INVISIBLE);
        combo.add(PlotLineType.INVISIBLE.getName());
	}
    
}
