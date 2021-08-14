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
    public VtcIndicatorPreferencePage() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void doCreateFullContents(final Composite composite) {
    	
        Button valueFromNewsSelector = addBooleanSelector(composite, "getValueFromNews", Messages.VtcIndicatorPreferencePage_GetValueFromNews, true);
        
    	final LongSpinner callValueSelector = addIntegerValueSelector(composite, "vtcValue", Messages.VtcIndicatorPreferencePage_LabelCallValue, 1, 99999, 1);
    	callValueSelector.setEnabled(!valueFromNewsSelector.getSelection());

    	final LongSpinner deltaValueSelector = addDoubleValueSelector(composite, "deltaValue", Messages.VtcIndicatorPreferencePage_LabelDeltaValue, 2, 0.05, 1.0, 0.5);
    	deltaValueSelector.setEnabled(!valueFromNewsSelector.getSelection());
    	
    	callValueSelector.setEnabled(!valueFromNewsSelector.getSelection());

    	valueFromNewsSelector.addSelectionListener(new SelectionAdapter() {
    		@Override
    		public void widgetSelected(SelectionEvent e) {
    			Button button = (Button) e.widget;
    			callValueSelector.setEnabled(!button.getSelection());
    			deltaValueSelector.setEnabled(!button.getSelection());
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

    /*
    protected Combo addOrigemPrecoSelector(final Composite composite) {
        new Label(composite, 0).setText(Messages.VtcIndicatorPreferencePage_LabelOrigemPreco);
        
        Combo combo = new Combo(composite, 8);
        
        combo.add(Messages.VtcIndicatorPreferencePage_OrigemPreco_CodigoSerie);
        combo.add(Messages.VtcIndicatorPreferencePage_OrigemPreco_Contrato);
        combo.select(VtcIndicator.DEFAULT_ORIGEM_PRECO);
        
        addControl("priceOrigin", combo);
        return combo;
    } */

}
