/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usbplotter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.util.List;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Robert Pyke
 */
public class USBPlotFrame extends JFrame {

    // The XYSeries we intend to add our values to
    XYSeries xySeries = null;

    /**
     * Construct a Frame to plot our USB Data.
     * 
     * The Frame contains an XY plot (scatter plot), with a single XYSeries.
     * 
     * @throws HeadlessException 
     */
    public USBPlotFrame() throws HeadlessException {
        super("USB Plot");

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeries = new XYSeries("Value");

        xySeriesCollection.addSeries(xySeries);
        ChartPanel chartPanel = constructChart(xySeriesCollection);

        add(chartPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Add a point to the XYSeries (the scatter plot)
     * 
     * @param x
     * @param y 
     */
    public void addPoint(Number x, Number y) {
        xySeries.add(x, y);
    }

    /**
     * Constructs the ChartPanel from the provided XYSeriesCollection
     * 
     * @param xySeriesCollection
     * @return The constructed ChartPanel containing the XY scatter plot
     */
    private ChartPanel constructChart(XYSeriesCollection xySeriesCollection) {
        // Construct the scatter plot from the provided xySeriesCollection
        JFreeChart chart = ChartFactory.createScatterPlot("Title", "X Axis", "Y Axis", xySeriesCollection);
        
        // Get the XY Plot from the scatter chart
        XYPlot xyPlot = (XYPlot) chart.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        
        // Create the renderer
        XYItemRenderer renderer = xyPlot.getRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        
        NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        
        domain.setVerticalTickLabels(true);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        return chartPanel;       
    }

    /**
     * Creates the XY Plot, and then listens to the USB interface (/dev/ttyUSB0) and plots the
     * read data. The USB Interface is read every second.
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String args[]) throws Exception {
        USBInterface myUSB = new USBInterface("/dev/ttyUSB0");
        USBPlotFrame frame = new USBPlotFrame();
        frame.setVisible(true);
        int count = 0;

        while (true) {
            Thread.sleep(1000);
            List<Double> values = myUSB.readAsDoubleArray();

            for (Double val : values) {
                System.out.println(val.toString());
                frame.addPoint(count++, val);
            }
        }

    }

}
