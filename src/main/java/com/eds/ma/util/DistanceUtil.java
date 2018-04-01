package com.eds.ma.util;

/**
 * 距离工具类
 *
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public class DistanceUtil {

    private static final double EARTH_RADIUS = 6378137;
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据经纬度计算X范围内的最大经纬度和最小经纬度
     *
     * @param lat    已知点的纬度
     * @param lng    已知点的经度
     * @param raidus 单位米
     * @return
     */
    public static double[] getAround(double lat, double lng, int raidus) {

        Double latitude = lat;
        Double longitude = lng;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        return new double[]{minLat, maxLat, minLng, maxLng};
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     *
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

} 
