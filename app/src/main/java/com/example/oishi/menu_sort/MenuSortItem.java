package com.example.oishi.menu_sort;

public class MenuSortItem {
    private String name;
    private int viewType;

    public MenuSortItem(String name, int viewType) {
        this.name = name;
        this.viewType = viewType;
    }

    public String getName() {
        return name;
    }

    public int getViewType() {
        return viewType;
    }
}
