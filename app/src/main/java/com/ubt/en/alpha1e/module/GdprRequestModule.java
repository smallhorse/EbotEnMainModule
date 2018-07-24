package com.ubt.en.alpha1e.module;

/**
 * @author admin
 * @className
 * @description
 * @date
 * @update
 */


public class GdprRequestModule {


    private String productId;
    private int type;
    private String version;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public String toString() {
        return "GdprRequestModule{" +
                "productId='" + productId + '\'' +
                ", type=" + type +
                ", version='" + version + '\'' +
                '}';
    }
}
