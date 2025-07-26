package com.smartstock;


public class ConversionDto {
    private double conversionRate;
    private double conversionResult;

    public ConversionDto() {}

    public ConversionDto(double conversionRate, double conversionResult) {
        this.conversionRate = conversionRate;
        this.conversionResult = conversionResult;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public double getConversionResult() {
        return conversionResult;
    }

    public void setConversionResult(double conversionResult) {
        this.conversionResult = conversionResult;
    }



}

