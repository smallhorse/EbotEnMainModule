package com.ubt.mainmodule.user.recycleview;

/**
 * @author：liuhai
 * @date：2017/10/27 11:52
 * @modifier：ubt
 * @modify_date：2017/10/27 11:52
 * [A brief description]
 * version
 */

public class LeftMenuModel {
    private int nameString;
    private boolean chick;   //标识
    private int imageId;
    private int countUnRead;

    public LeftMenuModel(int nameString) {
        this.nameString = nameString;
    }

    public LeftMenuModel(int nameString, boolean chick, int imageId, int countUnRead) {
        this.nameString = nameString;
        this.chick = chick;
        this.imageId = imageId;
        this.countUnRead = countUnRead;
    }

    public int getNameString() {
        return nameString;
    }

    public void setNameString(int nameString) {
        this.nameString = nameString;
    }

    public boolean isChick() {
        return chick;
    }

    public void setChick(boolean chick) {
        this.chick = chick;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getCountUnRead() {
        return countUnRead;
    }

    public void setCountUnRead(int countUnRead) {
        this.countUnRead = countUnRead;
    }

    @Override
    public String toString() {
        return "LeftMenuModel{" +
                "nameString='" + nameString + '\'' +
                ", chick=" + chick +
                ", imageId=" + imageId +
                ", countUnRead=" + countUnRead +
                '}';
    }
}
