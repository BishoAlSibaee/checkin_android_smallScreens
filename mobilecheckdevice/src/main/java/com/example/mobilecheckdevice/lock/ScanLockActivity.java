package com.example.mobilecheckdevice.lock;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;

import java.util.LinkedList;


public class ScanLockActivity extends AppCompatActivity implements LockListAdapter.onLockItemClick{
    //ActivityScanLockBinding binding;
    protected static final int REQUEST_PERMISSION_REQ_CODE = 11;
    private LockListAdapter mListApapter;
    private String mInitLockData;
    //mHotelInfoStr should get from server by call Url
    private String mHotelInfoStr = "LTExMywtMTE2LC0xMTYsLTExNiwtMTEwLC0xMTUsLTEyMSwtMzgsLTExNiwtMTE0LC0xMTcsLTEyMSwtNDAsLTM3LC0zNywtMzUsLTExNSwtMTEwLC0xMTYsLTExMywtMTEzLC0xMTQsLTM1LC0xMjIsLTM1LC0zNywtMTE0LC0xMTYsLTEyMSwtMzYsLTExOCwtMzYsLTExNywtMzcsLTExMywtMzMsLTM1LC0xMTgsLTExNiwtMTE1LC0xMTcsLTM4LC0xMTMsLTExNCwtNDAsLTExMywtMzMsLTExNSwtMzcsLTExNiwtMTEwLC0xMTYsLTExNywtMTIxLC0xMTksLTExNiwtMTE0LC0xMjAsLTEyMCwzMg==";
    //the number of your hotel building
    private int mBuildingNumber = 1;
    //the number of your hotel floor
    private int mFloorNumber = 1;
    static Activity act ;
    private LinkedList<ExtendedBluetoothDevice> mDataList ;
    RecyclerView bb ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_scan_lock);
        //binding = DataBindingUtil.setContentView(this,R.layout.activity_scan_lock);
        act = this;
        //initList();
        //initBtService();
        //initListener();

    }
/*
    private void initList(){
        mDataList = new LinkedList<ExtendedBluetoothDevice>();
        mListApapter = new LockListAdapter(this , mDataList);
        //binding.rvLockList.setAdapter(mListApapter);
        //binding.rvLockList.setLayoutManager(new LinearLayoutManager(this));
        mListApapter.setOnLockItemClick(this);
        //bb = (RecyclerView) findViewById(R.id.rv_lock_list);
        LinearLayoutManager manager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        bb.setLayoutManager(manager);
    }

 */

    /**
     * prepareBTService should be called first,or all TTLock SDK function will not be run correctly
     */
    /*
    private void initBtService(){
        TTLockClient.getDefault().prepareBTService(getApplicationContext());
    }

    private void initListener(){
        Button enable = (Button) findViewById(com.syriasoft.hotelservices.R.id.btn_enable_ble);
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBtEnable = TTLockClient.getDefault().isBLEEnabled(ScanLockActivity.this);
                if (!isBtEnable) {
                    TTLockClient.getDefault().requestBleEnable(ScanLockActivity.this);
                }
            }
        });
        Button scan = (Button) findViewById(com.syriasoft.hotelservices.R.id.btn_start_scan);
        Button stop = (Button) findViewById(com.syriasoft.hotelservices.R.id.btn_stop_scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TTLockClient.getDefault().stopScanLock();
            }
        });
       // binding.btnStartScan.setOnClickListener(v -> startScan());
       //binding.btnStopScan.setOnClickListener(v -> TTLockClient.getDefault().stopScanLock());
    }



     */
    /**
     * before call startScanLock,the location permission should be granted.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void startScan(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }

        //getScanLockCallback();
    }

    @Override
    public void onClick(ExtendedBluetoothDevice device) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * start scan BT lock
     */
    /*
    private void getScanLockCallback(){
        TTLockClient.getDefault().startScanLock(new ScanLockCallback() {
            @Override
            public void onScanLockSuccess(ExtendedBluetoothDevice device) {
                //ToastMaker.MakeToast(device.getName(),act);
                mDataList.add(device);
                mListApapter = new LockListAdapter(act , mDataList);
                bb.setAdapter(mListApapter);
                if(mListApapter != null){
                   //mListApapter.updateData(device);
                   mListApapter.notifyDataSetChanged();
                  //bb.setAdapter(mListApapter);
                }
            }

            @Override
            public void onFail(LockError error) {
                Log.e("tttt",error.getErrorMsg());
                ToastMaker.MakeToast(error.getErrorMsg(),act);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length == 0 ){

            return;
        }

        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getScanLockCallback();
                } else {
                    if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)){

                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

     */
        /**
         * BT service should be released before Activity finished.
         */
        /*
        TTLockClient.getDefault().stopBTService();
    }

    @Override
    public  void onClick(final ExtendedBluetoothDevice device) {
        ToastMaker.MakeToast("--start init lock--" ,act );


        // if you need to add a hotel lock you should set hotel data for lock init.

//        HotelData hotelData = new HotelData();
////        hotelData.setBuildingNumber(mBuildingNumber);
//        hotelData.setFloorNumber(LogIn.room.);
//        hotelData.setHotelInfo(mHotelInfoStr);
//        try {
//            device.setHotelData(hotelData);
//        } catch (ParamInvalidException e){
//
//        }



         */
        /**
         * lockData: the server api lockData param need
         * isNBLock: is a NB-IoT lock.
         */
        /*
        TTLockClient.getDefault().initLock(device, new InitLockCallback() {
            @Override
            public void onInitLockSuccess(String lockData) {
                //this must be done after lock is initialized,call server api to post to your server
                if(FeatureValueUtil.isSupportFeature(lockData, FeatureValue.NB_LOCK)){
                    setNBServerForNBLock(lockData,device.getAddress());
                }else {
                    ToastMaker.MakeToast("--lock is initialized success--",act);
                    upload2Server(lockData);
                }


            }

            @Override
            public void onFail(LockError error) {
                ToastMaker.MakeToast(error.getErrorMsg(),act);
            }
        });
    }

         */

    /**
     * if a NB-IoT lock you'd better do set NB-IoT server before upload lockData to server to active NB-IoT lock service.
     * And no matter callback is success or fail,upload lockData to server.
     * @param lockData
     * @param lockMac
     */
    /*
    public static void setNBServerForNBLock(final String lockData, String lockMac){
        //NB server port
        short mNBServerPort = 8011;
        String mNBServerAddress = "192.127.123.11";
        TTLockClient.getDefault().setNBServerInfo(mNBServerPort, mNBServerAddress, lockData, new SetNBServerCallback() {
            @Override
            public void onSetNBServerSuccess(int battery) {
                ToastMaker.MakeToast("--set NB server success--",act);
                upload2Server(lockData);
            }

            @Override
            public void onFail(LockError error) {
                ToastMaker.MakeToast(error.getErrorMsg(),act);
                //no matter callback is success or fail,upload lockData to server.
                upload2Server(lockData);
            }
        });
    }

    public static void upload2Server(String lockData){
        Calendar c = Calendar.getInstance() ;
        c.setTimeInMillis(System.currentTimeMillis());
        String lockAlias = "MyTestLock" + c.get(Calendar.DAY_OF_MONTH);//DateUtils.getMillsTimeFormat(System.currentTimeMillis());
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<ResponseBody> call = apiService.lockInit(ApiService.CLIENT_ID,LogIn.acc.getAccess_token() , lockData,lockAlias,System.currentTimeMillis());
        RetrofitAPIManager.enqueue(call, new TypeToken<LockInitResultObj>() {
        }, new ApiResponse.Listener<ApiResult<LockInitResultObj>>() {
            @Override
            public void onResponse(ApiResult<LockInitResultObj> result) {
                if (!result.success) {
                    ToastMaker.MakeToast("-init fail-to server-" + result.getMsg(), act);
                    //if upload fail you should cache lockData and upload again until success,or you should reset lock and do init again.
                    return;
                }
                ToastMaker.MakeToast("--init lock success--", act);
                Intent intent = new Intent(act, UserLockActivity.class);
                act.startActivity(intent);
                act.finish();

            }
        }, new ApiResponse.ErrorListener() {
            @Override
            public void onErrorResponse(Throwable requestError) {
                ToastMaker.MakeToast(requestError.getMessage()+"error", act);
            }
        });
    }

     */
}
