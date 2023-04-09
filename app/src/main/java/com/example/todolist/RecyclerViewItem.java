package com.example.todolist;

public class RecyclerViewItem {

    private String text;
    private boolean isSelected;
    private int amountOfProduct;
    private String key;

    public RecyclerViewItem(String text, boolean isSelected, String key) {
        this.text = text;
        this.amountOfProduct = 1;
        this.isSelected = isSelected;
        this.key = key;
    }

    public RecyclerViewItem() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getAmountOfProduct() {
        return amountOfProduct;
    }

    public void setAmountOfProduct(int amountOfProduct) {
        this.amountOfProduct = amountOfProduct;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getKey() {
        return key;
    }
}
