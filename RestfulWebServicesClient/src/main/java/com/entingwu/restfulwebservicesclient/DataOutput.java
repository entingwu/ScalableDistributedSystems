package com.entingwu.restfulwebservicesclient;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DataOutput extends JFrame {
    private XYSeriesCollection dataset;
    
    public DataOutput() {
        this.dataset = new XYSeriesCollection();
    }
    
    public void generateChart(List<Long> data, String name) throws IOException {
        // 1. Input data
        XYSeries series = new XYSeries(name);
        for (int i = 0; i < data.size(); i++) {
            series.add(i, data.get(i));
        }
        dataset.addSeries(series);
        
        // 2. Create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Throughput", "Count", "Latency", dataset);
        chart.getLegend().setFrame(BlockBorder.NONE);
        
        // 3. Write to file
        File file = new File(name);
        ChartUtilities.saveChartAsJPEG(file, chart, 640, 480);
    }
}
