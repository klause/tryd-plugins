package kan.eclipsetrader.charts.indicators.vwapvariation;

import org.eclipse.swt.widgets.Composite;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.IndicatorPluginPreferencePage;
import net.sourceforge.eclipsetrader.core.db.PlotLineType;
import net.sourceforge.eclipsetrader.core.ui.widgets.FlatCombo;

public class VwapVariationIndicatorPreferencePage extends IndicatorPluginPreferencePage
{
    @Override
    protected void doCreateFullContents(final Composite composite) {
    	
        addLineTypeSelector(composite, "lineTypeVwap", Messages.VwapVarPreferencePage_LineType_Vwap, PlotLineType.DASH, "lineThicknessVwap", 1);
        addColorSelector(composite, "lineColorVwap", Messages.VwapVarPreferencePage_LineColor_Vwap,  VwapVariationIndicator.DEFAULT_LINE_COLLOR);

        addLineTypeSelector(composite, "lineTypePositiveVariations", Messages.VwapVarPreferencePage_LineType_PositiveVariations, PlotLineType.LINE, "lineThicknessPositiveVariations", 1);
        addColorSelector(composite, "lineColorPositiveVariations", Messages.VwapVarPreferencePage_LineColor_PositiveVariations,  VwapVariationIndicator.DEFAULT_LINE_COLLOR);

        addLineTypeSelector(composite, "lineTypeNegativeVariations", Messages.VwapVarPreferencePage_LineType_NegativeVariations, PlotLineType.LINE, "lineThicknessNegativeVariations", 1);
        addColorSelector(composite, "lineColorNegativeVariations", Messages.VwapVarPreferencePage_LineColor_NegativeVariations,  VwapVariationIndicator.DEFAULT_LINE_COLLOR);
        
        addTextSideSelector(composite, Messages.VwapVarPreferencePage_LabelSide, "textSide", IndicatorPlugin.DEFAULT_TEXT_SIDE);
        addShowValueControl(composite, VwapVariationIndicator.DEFAULT_V_SCALE_VALUE);
        
    }

	@Override
	protected void doFillLineTypeComboSelector(FlatCombo combo) {
		combo.add(PlotLineType.LINE.getName());
		combo.setData(PlotLineType.LINE.getName(), (Object) PlotLineType.LINE);
		combo.add(PlotLineType.DOT.getName());
		combo.setData(PlotLineType.DOT.getName(), (Object) PlotLineType.DOT);
		combo.add(PlotLineType.DASH.getName());
		combo.setData(PlotLineType.DASH.getName(), (Object) PlotLineType.DASH);
		combo.add(PlotLineType.INVISIBLE.getName());
		combo.setData(PlotLineType.INVISIBLE.getName(), (Object) PlotLineType.INVISIBLE);
	}

}
