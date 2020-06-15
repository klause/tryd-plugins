package kan.eclipsetrader.charts.indicators.vtc;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.IndicatorPluginPreferencePage;
import net.sourceforge.eclipsetrader.core.db.PlotLineType;
import net.sourceforge.eclipsetrader.core.ui.widgets.FlatCombo;
import net.sourceforge.eclipsetrader.core.ui.widgets.LongSpinner;

public class VtcIndicatorPreferencePage extends IndicatorPluginPreferencePage
{
    @Override
    protected void doCreateContents(final Composite composite) {
    	
        Button valueFromNewsSelector = addBooleanSelector(composite, "getValueFromNews", Messages.VtcIndicatorPreferencePage_GetValueFromNews, true);
        
    	final LongSpinner vtcValueSelector = addIntegerValueSelector(composite, "vtcValue", Messages.VtcIndicatorPreferencePage_Value, 1, 99999, 1);
    	vtcValueSelector.setEnabled(!valueFromNewsSelector.getSelection());

    	valueFromNewsSelector.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			Button button = (Button) e.widget;
    			vtcValueSelector.setEnabled(!button.getSelection());
    		}
		});
    	
        addLineTypeSelector(composite, "lineTypeVtc", Messages.VtcIndicatorPreferencePage_LineType_Vtc, PlotLineType.DASH, "lineThicknessVtc", 1);
        addColorSelector(composite, "lineColorVtc", Messages.VtcIndicatorPreferencePage_LineColor_Vtc, VtcIndicator.DEFAULT_LINE_COLLOR);

        addLineTypeSelector(composite, "lineTypeVariacaoPositiva", Messages.VtcIndicatorPreferencePage_LineType_VariacaoPositiva, IndicatorPlugin.DEFAULT_PLOT_LINE_TYPE, "lineThicknessVariacaoPositiva", 1);
        addColorSelector(composite, "lineColorVariacaoPositiva", Messages.VtcIndicatorPreferencePage_LineColor_VariacaoPositiva, VtcIndicator.DEFAULT_LINE_COLLOR);
        
        addLineTypeSelector(composite, "lineTypeVariacaoNegativa", Messages.VtcIndicatorPreferencePage_LineType_VariacaoNegativa, IndicatorPlugin.DEFAULT_PLOT_LINE_TYPE, "lineThicknessVariacaoNegativa", 1);
        addColorSelector(composite, "lineColorVariacaoNegativa", Messages.VtcIndicatorPreferencePage_LineColor_VariacaoNegativa, VtcIndicator.DEFAULT_LINE_COLLOR);
        
        addTextSideSelector(composite, Messages.VtcIndicatorPreferencePage_LabelSide, "textSide", IndicatorPlugin.DEFAULT_TEXT_SIDE);
        addShowValueControl(composite, VtcIndicator.DEFAULT_V_SCALE_VALUE);
    }
    
    @Override
    protected void doFillLineTypeComboSelector(final FlatCombo combo) {
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
