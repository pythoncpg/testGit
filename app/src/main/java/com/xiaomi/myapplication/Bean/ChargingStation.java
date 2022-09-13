package com.xiaomi.myapplication.Bean;

import java.io.Serializable;

public class ChargingStation implements Serializable {
    private String id;// 充电桩id
    private String name;// 充电桩名称
    private String typecode; // 兴趣点类型编码
    private String type;  // 兴趣类型
    private String location;// 经纬度
    private Integer distance;// 距离中心点距离
    private String address;// 地址
    private String tel;// 电话
    private String pcode;// 所在省份编码
    private String pname;// POI 所在省份名称
    private String citycode;// 城市编码
    private String cityname;// 城市名
    private String adcode; // 区域编码
    private String adname;// 区域名称
    private String entrLocation;// POI 的入口经纬度
    private String businessData;// 所属商圈
    private String photoUrl;// 图片地址 ???
    private String timestamp;// 时间戳
    private Double price;// 价格
    private String chargeType; // 充电类型
    private String openTime; // 营业时间
    private String drivingTime; // 驾驶时间
    private String parkingInfo; // 停车信息
    private String chargeStatus; // 充电站状态
    private int useCount;// 当前已用充电桩
    private int totalCount; // 总充电桩数

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypecode() {
        return typecode;
    }

    public void setTypecode(String typecode) {
        this.typecode = typecode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }



    public String getAdname() {
        return adname;
    }

    public void setAdname(String adname) {
        this.adname = adname;
    }

    public String getEntrLocation() {
        return entrLocation;
    }

    public void setEntrLocation(String entrLocation) {
        this.entrLocation = entrLocation;
    }

    public String getBusinessData() {
        return businessData;
    }

    public void setBusinessData(String businessData) {
        this.businessData = businessData;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(String drivingTime) {
        this.drivingTime = drivingTime;
    }

    public String getParkingInfo() {
        return parkingInfo;
    }

    public void setParkingInfo(String parkingInfo) {
        this.parkingInfo = parkingInfo;
    }

    public String getChargeStatus() {
        return chargeStatus;
    }

    public void setChargeStatus(String chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "ChargingStation{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", typecode='" + typecode + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", distance=" + distance +
                ", address='" + address + '\'' +
                ", tel='" + tel + '\'' +
                ", pcode='" + pcode + '\'' +
                ", pname='" + pname + '\'' +
                ", citycode='" + citycode + '\'' +
                ", cityname='" + cityname + '\'' +
                ", Adcode='" + adcode + '\'' +
                ", adname='" + adname + '\'' +
                ", entrLocation='" + entrLocation + '\'' +
                ", businessData='" + businessData + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", price=" + price +
                ", chargeType='" + chargeType + '\'' +
                ", openTime='" + openTime + '\'' +
                ", drivingTime='" + drivingTime + '\'' +
                ", parkingInfo='" + parkingInfo + '\'' +
                ", chargeStatus='" + chargeStatus + '\'' +
                ", useCount=" + useCount +
                ", totalCount=" + totalCount +
                '}';
    }
}
