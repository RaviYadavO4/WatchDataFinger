package com.payment.watchdatafinger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.RelativeLayout;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UtilsCapture {
    private static SharedPreferences sharedPreferences;
    public static final String PREFS_NAME = "MODULE_PREFS";

    public DeviceDataModel NextBIoData (RelativeLayout ParentLayout, String mainData){
        DeviceDataModel dataModel = new DeviceDataModel();
        dataModel.setErrMsg("Please connect your NextBioMetric device!");
        dataModel.setErrCode("111");
        try {
            if (mainData != null) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new ByteArrayInputStream(mainData.getBytes("UTF-8")));

                Node node = doc.getElementsByTagName("PidData").item(0);
                Element element = (Element) node;

                NodeList nodeList1 = doc.getElementsByTagName("Resp");
                Element element1 = (Element) nodeList1.item(0);

                dataModel.setErrCode(element1.getAttribute("errCode"));
                dataModel.setErrMsg(element1.getAttribute("errInfo"));
//                dataModel.setNmPoints(element1.getAttribute("nmPoints"));

                if (element1.getAttribute("errCode").equalsIgnoreCase("0")) {

                    NodeList nodeList2 = doc.getElementsByTagName("Skey");
                    Element element2 = (Element) nodeList2.item(0);

                    NodeList nodeList3 = doc.getElementsByTagName("DeviceInfo");
                    Element element3 = (Element) nodeList3.item(0);

                    NodeList nodeList4 = ((Element) nodeList3.item(0)).getElementsByTagName("additional_info");
                    Element serialNo = (Element) nodeList4.item(0).getChildNodes().item(1);
                    String srno = serialNo.getAttribute("value");

                    serialNo = (Element) nodeList4.item(0).getChildNodes().item(3);
//                    String sysid = element4.getAttribute("value");
//                    element4 = (Element) nodeList4.item(0).getChildNodes().item(5);
                    String ts = serialNo.getAttribute("value");
                    //2018-05-02T17:38:28+05:30

//                    NodeList nodeList = doc.getElementsByTagName("DeviceInfo");
//                    NodeList n2 = ((Element) nodeList.item(0)).getElementsByTagName("additional_info");
//                    Element serialNo = (Element) n2.item(0).getChildNodes().item(1);
//                    serialNo.getAttribute("value")

                    dataModel.setFingerData("" + getValue("Data", element));
                    dataModel.setHmac("" + getValue("Hmac", element));
                    dataModel.setSkey("" + getValue("Skey", element));
                    dataModel.setCi("" + element2.getAttribute("ci"));
                    dataModel.setDc("" + element3.getAttribute("dc"));
                    dataModel.setDpId("" + element3.getAttribute("dpId"));
                    dataModel.setMc("" + element3.getAttribute("mc"));
                    dataModel.setMi("" + element3.getAttribute("mi"));
                    dataModel.setRdsId("" + element3.getAttribute("rdsId"));
                    dataModel.setRdsVer("" + element3.getAttribute("rdsVer"));
                    dataModel.setSrno("" + srno);
                    //   dataModel.setSysid("" + sysid);
                    dataModel.setTs("" + ts);


//                    new util().snackBar(ParentLayout, "Finger Captured Successfully!", SnackBarBackGroundColorGreen);

                }


            }
        } catch (Exception e) {
            //  new util().snackBar(ParentLayout, CheckMantra, SnackBarBackGroundColor);
            dataModel.setErrMsg("Please connect your NextBioMetric device!");
            e.printStackTrace();
        }

        return dataModel;
    }

    private static String getValue(String mainStr, Element element) {
        NodeList nodeList = element.getElementsByTagName(mainStr).item(0).getChildNodes();
        Node node = nodeList.item(0);

        return node.getNodeValue();
    }

    public static String getPreferredPackage(Context context, String deviceId) {
        String data = "";
        try {
            Intent intent = new Intent();
            intent.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
            PackageManager packageManager = context.getPackageManager();
            final ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
            String temp = resolveInfo.activityInfo.packageName;
            if (temp.equalsIgnoreCase("android") || temp.equalsIgnoreCase(deviceId))
                data = "";
            else
                data = temp;//packageManager.getApplicationLabel(resolveInfo.activityInfo.applicationInfo).toString();
            // data=" ("+packageManager.getApplicationLabel(resolveInfo.activityInfo.applicationInfo).toString()+")";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
    public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public static String SELECTED_DEVICE = "selected_device";
    public static String SELECTED_DEVICE_INDEX = "selected_device_INDEX";
    public static String getValue(Context context, String PREF_KEY) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PREF_KEY, null);
    }
    public static void setValue(Context context, String PREF_KEY, String PREF_VALUE) {
        SharedPreferences.Editor editor;
        sharedPreferences =  context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = sharedPreferences.edit(); //2
        editor.putString(PREF_KEY, PREF_VALUE); //3
        editor.commit(); //4
    }

}
