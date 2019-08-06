package kim.daegi.models;

import static kim.daegi.ConsoleColors.*;

public class Finance {
    private String name;
    private String symbol;
    private String status;
    private double previousPrice;
    private double openPrice;
    private double currentPrice;
    private double lowPrice;
    private double highPrice;
    private double diff;
    private double changeRates;
    private double volume;
    private double previousVolume;

    public Finance(String name, String symbol, String status, double previousPrice, double openPrice, double currentPrice, double lowPrice, double highPrice, double diff, double changeRates, double volume, double previousVolume) {
        this.name = name;
        this.symbol = symbol;
        this.status = status;
        this.previousPrice = previousPrice;
        this.openPrice = openPrice;
        this.currentPrice = currentPrice;
        this.lowPrice = lowPrice;
        this.highPrice = highPrice;
        this.diff = diff;
        this.changeRates = changeRates;
        this.volume = volume;
        this.previousVolume = previousVolume;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-10s", this.symbol));
        sb.append(String.format("%10s", this.status));
        sb.append(String.format("%,15.0f", this.volume));
        if(this instanceof NaverFinance) {
            sb.append(String.format("%8s", String.format("(%,.3f)", this.volume/this.previousVolume)));
        }

        if(this.highPrice == this.currentPrice) {
            sb.append(String.format(GREEN_BOLD_BRIGHT+"%,11.0f"+ RESET, this.currentPrice));
        } else if(this.lowPrice == this.currentPrice) {
            sb.append(String.format(RED_BOLD_BRIGHT+"%,11.0f"+ RESET, this.currentPrice));
        } else {
            sb.append(String.format(WHITE_BOLD+"%,11.0f"+ RESET, this.currentPrice));
        }

        String color = getConsoleColor();

        sb.append(String.format(color+"%13s"+ RESET, String.format("%,.0f", this.diff)+" "+(this.diff>0?"▲":this.diff<0?"▼":"-")));
        sb.append(String.format(color+"%10s"+ RESET, String.format("(%.2f%%)", this.changeRates)));
        sb.append(candleChart(this.lowPrice, this.highPrice, this.currentPrice, this.openPrice, color));
        sb.append(String.format(" %-15s\n", this.name));

        return sb.toString();
    }

    private String candleChart(double low, double high, double current, double ov, String crColor) {
        if(low == 0) {
            return String.format("%16s", "");
        }

        final int sizeOfChart = 15;

        int indicator = (int) ((current - low) / (high - low) * sizeOfChart);
        indicator = indicator==sizeOfChart?indicator-1:indicator;

        int prevIndicator = (int) ((ov - low) / (high - low) * sizeOfChart);;
        prevIndicator = prevIndicator==sizeOfChart?prevIndicator-1:prevIndicator;

        StringBuilder sb = new StringBuilder(" ");
        String color = current>ov? GREEN_BOLD_BRIGHT:current<ov? RED_BOLD_BRIGHT:crColor;
        sb.append(color);

        int lowIndex = Math.min(indicator, prevIndicator);
        int highIndex = Math.max(indicator, prevIndicator);

        for (int i = 0; i < sizeOfChart; i++) {
            sb.append(i==indicator?"█":i>=lowIndex&&i<=highIndex?"■":"─");
        }
        sb.append(RESET);

        return sb.toString();
    }

    private String getConsoleColor() {
        return this.changeRates==0?"":this.changeRates>0? GREEN_BOLD_BRIGHT: RED_BOLD_BRIGHT;
    }
}
