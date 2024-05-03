package com.payment.watchdatafinger

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.payment.indistart.API.APIClient.getClient
import com.payment.indistart.API.APIInterface
import com.payment.watchdatafinger.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    var IciciPidData = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"PP\" /> <CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
    var PidData = "<?xml version=\"1.0\"?> <PidOptions ver=\"1.0\"> <Opts fCount=\"1\" fType=\"2\" iCount=\"0\" pCount=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\" posh=\"UNKNOWN\" env=\"P\" /> <CustOpts><Param name=\"mantrakey\" value=\"\" /></CustOpts> </PidOptions>"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmitBe.setOnClickListener {

            NextBiometric()
        }
    }

    private fun NextBiometric() {
        val packageManager: PackageManager = getPackageManager()
        if (isPackageInstalled("com.nextbiometrics.onetouchrdservice", packageManager)) {
            val intent2 = Intent()
            intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE")
            intent2.putExtra("PID_OPTIONS", IciciPidData)
            intent2.setPackage("com.nextbiometrics.onetouchrdservice")
            startActivityForResult(intent2, 11111)
        } else {
            val alertDialog = AlertDialog.Builder(this@MainActivity, R.style.alertDialog_sdk)
            // final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Get Service")
            alertDialog.setMessage("NEXT Biometrics L0 Is Not Found.Click OK to Download Now.")
            alertDialog.setPositiveButton(
                "OK"
            ) { dialog, which ->
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("NEXT_RD_SERVICE_PLAY_STORE_ADDRESS")
                        )
                    )
                } catch (e: Exception) {
                    Toast.makeText(this, "Somethig went Wrong$e", Toast.LENGTH_SHORT).show()
                    //                        new util().snackBar(ParentLayout, SomethingWentWrong, SnackBarBackGroundColor);
                    e.printStackTrace()
                }
            }
            alertDialog.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()

            //Toast.makeText(context, "RD services not found!!", Toast.LENGTH_SHORT).show();
        }
    }

    fun isPackageInstalled(packagename: String?, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packagename!!, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            11111 -> if (resultCode == RESULT_OK) {
                var dataModel: DeviceDataModel? = null
                if (data != null) {
                    dataModel = UtilsCapture().NextBIoData(binding.ParentLayout, data.getStringExtra("PID_DATA"))
                }
                val pidData = data!!.getStringExtra("PID_DATA")
                Log.e("abhishek", "" + pidData)
                if (dataModel?.getErrCode().equals("0",ignoreCase = true)) {
                    if (pidData != null) {
                        binding.pressBar.visibility = View.VISIBLE
                        getTransaction(pidData)
                    }
//                    balnceInquiryApi(pidData)
                    //                        networkCallUsingVolleyApi(Constants.URL.AEPSTRANSACTION, param(pidData));
                } else {
                    Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
                    //                        if (getPreferredPackage(IciciBalanceInquiryActivity.this, NEXT_RD_SERVICE_PACKAGE_NAME).equalsIgnoreCase(""))
//                            new util().snackBar(ParentLayout, dataModel.getErrCode() + " : NextBioMetric " + dataModel.getErrMsg() + getPreferredPackage(IciciBalanceInquiryActivity.this, NEXT_RD_SERVICE_PACKAGE_NAME), SnackBarBackGroundColor);
//
//                        else
//                            new utilDevices().clearDefaultSetting(IciciBalanceInquiryActivity.this, getPreferredPackage(IciciBalanceInquiryActivity.this, NEXT_RD_SERVICE_PACKAGE_NAME));
//                        Log.e("error", "" + dataModel.getErrMsg());
                }
            }
        }
    }

    private fun getTransaction(pidData:String){

        val jsonObject = JSONObject().apply {
            put("aadhaarNumber", binding.etAadhaarNo.text.toString())
            put("agentId", "user_id")
            put("finger", pidData)
            put("iin", "607153")
            put("requestType", "balanceEnquiry")
            put("bcName", "Satish Kumar")
            put("bcPincode", "226012")
            put("bcDistict", "UP")
            put("bcState", "UP")
        }
        val jsonBody = jsonObject.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        getClient().create(APIInterface::class.java).doCreateUserWithField(jsonBody).enqueue(object :Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                binding.pressBar.visibility = View.GONE


            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                call.cancel()
                binding.pressBar.visibility = View.GONE
            }

        })
    }
}