package com.ubt.en.alpha1e.module;

import java.util.List;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class GdprRequestListModule {

    private List<GdprRequestModule> gdprRequestModuleList;

    public List<GdprRequestModule> getGdprRequestModuleList() {
        return gdprRequestModuleList;
    }

    public void setGdprRequestModuleList(List<GdprRequestModule> gdprRequestModuleList) {
        this.gdprRequestModuleList = gdprRequestModuleList;
    }

    @Override
    public String toString() {
        return "GdprRequestListModule{" +
                "gdprRequestModuleList=" + gdprRequestModuleList +
                '}';
    }
}
