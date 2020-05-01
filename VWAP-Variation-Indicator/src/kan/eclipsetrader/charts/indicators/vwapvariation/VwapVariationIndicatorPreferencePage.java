package kan.eclipsetrader.charts.indicators.vwapvariation;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.IndicatorPluginPreferencePage;
import net.sourceforge.eclipsetrader.core.db.PlotLineType;
import net.sourceforge.eclipsetrader.core.ui.layout.GridDataWrapper;
import net.sourceforge.eclipsetrader.core.ui.layout.GridLayoutWrapper;

public class VwapVariationIndicatorPreferencePage extends IndicatorPluginPreferencePage
{
    @Override
    protected void doCreateContents(final Composite composite) {
    	
//        final Label labelVwapRef;
//        (labelVwapRef = new Label(composite, 0)).setText(Messages.VwapVarIndicatorPreferencePage_Label_VwapRef);
//        labelVwapRef.setLayoutData((Object)new GridDataWrapper(128, -1).getGridData());
//
//        final Composite composite2;
//        (composite2 = new Composite(composite, 0)).setLayout((Layout)new GridLayoutWrapper(2, false).getLayout());
//        composite2.setLayoutData((Object)new GridDataWrapper(4, 1, true, false).getGridData());
        
//        final Combo comboVwapRef;
//        comboVwapRef = new Combo(composite2, 8);
//        comboVwapRef.add(Messages.VwapVarIndicatorPreferencePage_VwapRef_LastClose);
//        comboVwapRef.add(Messages.VwapVarIndicatorPreferencePage_VwapRef_LastWeek);
//        comboVwapRef.add(Messages.VwapVarIndicatorPreferencePage_VwapRef_LastMonth);
//        comboVwapRef.select((int)this.getSettings().getInteger("vwapRef", 0));
//        this.addControl("vwapRef", (Control)comboVwapRef);

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
    protected void doFillLineTypeComboSelector(final Combo combo) {
        combo.add(PlotLineType.LINE.getName());
        combo.setData(PlotLineType.LINE.getName(), (Object)PlotLineType.LINE);
        combo.add(PlotLineType.DOT.getName());
        combo.setData(PlotLineType.DOT.getName(), (Object)PlotLineType.DOT);
        combo.add(PlotLineType.DASH.getName());
        combo.setData(PlotLineType.DASH.getName(), (Object)PlotLineType.DASH);
        combo.add(PlotLineType.INVISIBLE.getName());
        combo.setData(PlotLineType.INVISIBLE.getName(), (Object)PlotLineType.INVISIBLE);
    }
    
}
