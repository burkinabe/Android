package com.accurascandemo.model;

public class BarcodeTypeSelection {
    public String barcodeTitle;
    public boolean isSelected;
    public int formatsType;

    public BarcodeTypeSelection(String barcodeTitle, boolean isSelected, int formatsType) {
        this.barcodeTitle = barcodeTitle;
        this.isSelected = isSelected;
        this.formatsType = formatsType;
    }
}
