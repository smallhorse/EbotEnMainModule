package com.ubt.mainmodule.user.profile;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @作者：bin.zhang@ubtrobot.com
 * @日期: 2018/4/24 09:52
 * @描述: 用户信息
 */

public class UserModel implements Parcelable {
    public static final int MALE = 0;
    public static final int FEMALE = 1;
    public static final int OTHER = 2;

    private String iconPath;
    private String name;
    private String id;
    private String birthday;
    private int genderId;
    private String gender;
    private String country;

    public String getIcon() {
        return iconPath;
    }

    public void setIcon(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGenderId() {
        return genderId;
    }

    public void setGenderId(int genderId) {
        this.genderId = genderId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.iconPath);
        dest.writeString(this.name);
        dest.writeString(this.id);
        dest.writeString(this.birthday);
        dest.writeInt(this.genderId);
        dest.writeString(this.gender);
        dest.writeString(this.country);
    }

    public UserModel() {
    }

    protected UserModel(Parcel in) {
        this.iconPath = in.readString();
        this.name = in.readString();
        this.id = in.readString();
        this.birthday = in.readString();
        this.genderId = in.readInt();
        this.gender = in.readString();
        this.country = in.readString();
    }

    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}
