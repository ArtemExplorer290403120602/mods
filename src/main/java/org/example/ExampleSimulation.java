package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class ExampleSimulation {

    private static final int N = 1000;
    private static final double[] gran1 = new double[]{200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200};
    private static final double[] gran2 = new double[]{0.2, 0.32, 0.44, 0.56, 0.68, 0.8, 0.92, 1.04, 1.16, 1.28, 1.4};

    private static double simulateSearchTime() {
        return 0.2 + (1.4 - 0.2) * Math.random();
    }

    private static double simulateI() {
        Random rand = new Random();
        double U = rand.nextDouble();

        if (U <= 0.2) {
            return (U + 0.2) / 0.001;
        } else if (U <= 0.8) {
            return (U + 0.4) / 0.0015;
        } else {
            return (U - 0.4) / 0.0005;
        }
    }

    public static void main(String[] args) {
        double[] m1 = new double[gran1.length - 1];
        double[] m2 = new double[gran2.length - 1];
        double totalTime = 0;
        int countExceed = 0;

        for (int i = 0; i < N; i++) {
            double x = simulateI();
            for (int j = 0; j < gran1.length - 1; j++) {
                if (x > gran1[j] && x <= gran1[j + 1]) {
                    m1[j]++;
                }
            }

            double searchTime = simulateSearchTime();
            for (int j = 0; j < gran2.length - 1; j++) {
                if (searchTime > gran2[j] && searchTime <= gran2[j + 1]) {
                    m2[j]++;
                }
            }

            double T_disk_read = x * 1000 / (1024 * 1024);
            double T_total = searchTime + T_disk_read;
            totalTime += T_total;

            if (T_total > 3) {
                countExceed++;
            }
        }

        displayHistogram(m1, "Гистограмма частот значений I", gran1);
        displayHistogram(m2, "Гистограмма частот значений T_search", gran2);

        double averageTime = totalTime / N;
        double probExceed = (double)countExceed / N;

        System.out.printf("Среднее время ответа на запрос: %.6f мс ", averageTime);
                System.out.printf("Вероятность того, что время ответа на запрос превысит 3 мс: %.4f ", probExceed);
    }

    private static void displayHistogram(double[] m, String title, double[] gran) {
        HistogramDataset dataset = new HistogramDataset();
        double[] frequencies = Arrays.stream(m).map(v -> v / N).toArray();

        // Отладочный вывод
        System.out.println("Частоты: " + Arrays.toString(frequencies));

        dataset.addSeries(title, frequencies, gran.length - 1); // здесь количество интервалов

        JFreeChart histogram = ChartFactory.createHistogram(
                title,
                "Интервалы",
                "Частота",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
        );

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new ChartPanel(histogram), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}