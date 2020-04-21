package kan.eclipsetrader.charts.indicators;
import org.eclipse.swt.graphics.RGB;

import net.sourceforge.eclipsetrader.charts.Indicator;
import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.ScaleLevel;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.charts.ch;
import net.sourceforge.eclipsetrader.charts.internal.VerticalScaleValue;
import net.sourceforge.eclipsetrader.core.Functions;
import net.sourceforge.eclipsetrader.core.db.Chart;
import net.sourceforge.eclipsetrader.core.db.PlotLineType;
import net.sourceforge.eclipsetrader.core.db.Security;

public class VtcIndicator extends IndicatorPlugin
{
    static final RGB DEFAULT_LINE_COLLOR;
    
    private PlotLineType lineTypeVariacaoPositiva;
    private int lineThicknessVariacaoPositiva;
    private RGB lineColorVariacaoPositiva;
    
    private PlotLineType lineTypeVariacaoNegativa;
    private int lineThicknessVariacaoNegativa;
    private RGB lineColorVariacaoNegativa;

    private int valueVtc;
    
    private ch textSide;
    private VerticalScaleValue verticalScaleValue;
    
    
    static final VerticalScaleValue DEFAULT_V_SCALE_VALUE;
    
    static {
        DEFAULT_LINE_COLLOR = new RGB(255, 201, 14);
        (DEFAULT_V_SCALE_VALUE = VerticalScaleValue.createDefault()).setShowValue(true);
        VtcIndicator.DEFAULT_V_SCALE_VALUE.setFractionDigits(2);
    }
    
    public VtcIndicator() {
        this.lineTypeVariacaoPositiva = VtcIndicator.DEFAULT_PLOT_LINE_TYPE;
        this.lineThicknessVariacaoPositiva = VtcIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorVariacaoPositiva = VtcIndicator.DEFAULT_LINE_COLLOR;

        this.lineTypeVariacaoNegativa = VtcIndicator.DEFAULT_PLOT_LINE_TYPE;
        this.lineThicknessVariacaoNegativa = VtcIndicator.DEFAULT_LINE_THICKNESS;
        this.lineColorVariacaoNegativa = VtcIndicator.DEFAULT_LINE_COLLOR;

        this.valueVtc = 3000;
        
        this.textSide = VtcIndicator.DEFAULT_TEXT_SIDE;
        this.verticalScaleValue = DEFAULT_V_SCALE_VALUE;
    }
    
    @Override
    protected void doCalculate() {
        final Indicator output = this.getOutput();

        if (valueVtc > 0) {
            final PlotLine plotLineVariacaoPositiva;
            final PlotLine plotLineVariacaoNegativa;
            
            Functions.logMessage("Valor do VTC: " + valueVtc);

            double valueVtcVariacaoPositiva = this.valueVtc + (this.valueVtc * 0.005);
            valueVtcVariacaoPositiva = Functions.roundIntoSecurityIncrementPrice(valueVtcVariacaoPositiva, getBarData().getSecurity());
            Functions.logMessage("Variação positiva: " + valueVtcVariacaoPositiva);
            
            double valueVtcVariacaoNegativa = this.valueVtc - (this.valueVtc * 0.005);
            valueVtcVariacaoNegativa = Functions.roundIntoSecurityIncrementPrice(valueVtcVariacaoNegativa, getBarData().getSecurity());
            Functions.logMessage("Variação negativa: " + valueVtcVariacaoNegativa);
            
            (plotLineVariacaoPositiva = new PlotLine()).setType(this.lineTypeVariacaoPositiva);
            plotLineVariacaoPositiva.setLineWidth(this.lineThicknessVariacaoPositiva);
            plotLineVariacaoPositiva.setColor(this.lineColorVariacaoPositiva);
            plotLineVariacaoPositiva.setTextSide(this.textSide);

            (plotLineVariacaoNegativa = new PlotLine()).setType(this.lineTypeVariacaoNegativa);
            plotLineVariacaoNegativa.setLineWidth(this.lineThicknessVariacaoNegativa);
            plotLineVariacaoNegativa.setColor(this.lineColorVariacaoNegativa);
            plotLineVariacaoNegativa.setTextSide(this.textSide);

            for (int j = 0; j < this.getBarData().size(); ++j) {
            	plotLineVariacaoPositiva.append(valueVtcVariacaoPositiva);
            	plotLineVariacaoNegativa.append(valueVtcVariacaoNegativa);
            }

            plotLineVariacaoPositiva.setLabel("VTC +0,5%");
            plotLineVariacaoPositiva.setText("VTC +0,5%");

            plotLineVariacaoNegativa.setLabel("VTC -0,5%");
            plotLineVariacaoNegativa.setText("VTC -0,5%");
            
            output.add(plotLineVariacaoPositiva);
            output.add(plotLineVariacaoNegativa);
        }

        output.setShowValueVerticalScale(this.verticalScaleValue);
        output.setScaleLevel(ScaleLevel.SIMPLE);
    }
    
    @Override
    public void doSetParameters(final Settings settings) {
    	
    	this.valueVtc = settings.getInteger("vtcValue", 3000);
    	
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
    
    
}
