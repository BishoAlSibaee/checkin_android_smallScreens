package com.syriasoft.hotelservices;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.ArrayList;
import java.util.List;

public class Moods extends AppCompatActivity {

    Activity act ;
    List<SceneBean> MoodsScenes ;
    List<SceneBean> livingMood,sleepMood,workMood,romanceMood,readMood,masterOffMood;
    Button living,sleep,work,romance,read,masterOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moods);
        setActivity();
        setActivityActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActivity();
    }

    void setActivity() {
        act = this ;
        MoodsScenes = new ArrayList<>() ;
        livingMood = new ArrayList<>() ;
        sleepMood = new ArrayList<>() ;
        workMood = new ArrayList<>() ;
        romanceMood = new ArrayList<>() ;
        readMood = new ArrayList<>() ;
        masterOffMood = new ArrayList<>() ;
        living = findViewById(R.id.livingMood);
        sleep = findViewById(R.id.sleepingMood);
        work = findViewById(R.id.workMood);
        romance = findViewById(R.id.romanceMood);
        read = findViewById(R.id.readingMood);
        masterOff = findViewById(R.id.masterOffMood);
        if (MyApp.MY_SCENES != null && MyApp.MY_SCENES.size() != 0) {
            for (int i=0; i < MyApp.MY_SCENES.size();i++) {
                if (MyApp.MY_SCENES.get(i).getName().contains("Mood")) {
                    MoodsScenes.add(MyApp.MY_SCENES.get(i));
                }
            }
        }
        if (MoodsScenes.size() > 0) {
            for (int i=0;i<MoodsScenes.size();i++) {
                if (MoodsScenes.get(i).getName().contains("Living")) {
                    livingMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Sleep")) {
                    sleepMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Work")) {
                    workMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Romance")) {
                    romanceMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Read")) {
                    readMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("MasterOff")) {
                    masterOffMood.add(MoodsScenes.get(i));
                }
            }
        }
        if (livingMood.size() > 0) {
            living.setBackgroundResource(R.color.wight);
        }
        else {
            living.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (sleepMood.size() > 0) {
            sleep.setBackgroundResource(R.color.wight);
        }
        else {
            sleep.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (workMood.size() > 0) {
            work.setBackgroundResource(R.color.wight);
        }
        else {
            work.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (romanceMood.size() > 0) {
            romance.setBackgroundResource(R.color.wight);
        }
        else {
            romance.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (readMood.size() > 0) {
            read.setBackgroundResource(R.color.wight);
        }
        else {
            read.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (masterOffMood.size() > 0) {
            masterOff.setBackgroundResource(R.color.wight);
        }
        else {
            masterOff.setBackgroundResource(R.drawable.btn_bg_normal);
        }
    }

    void setActivityActions() {
        living.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (livingMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Living Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Living Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<livingMood.size();i++) {
                                        SceneBean sb = livingMood.get(i) ;
                                        TuyaHomeSdk.newSceneInstance(livingMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new messageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                MyApp.MY_SCENES.remove(sb);
                                                livingMood.remove(sb);
                                                if (livingMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new messageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sleepMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Sleep Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Sleep Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<sleepMood.size();i++) {
                                        SceneBean sb = sleepMood.get(i) ;
                                        TuyaHomeSdk.newSceneInstance(sleepMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new messageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                MyApp.MY_SCENES.remove(sb);
                                                sleepMood.remove(sb);
                                                if (sleepMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new messageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (workMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Work Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Work Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<workMood.size();i++) {
                                        SceneBean sb = workMood.get(i);
                                        TuyaHomeSdk.newSceneInstance(workMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new messageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                MyApp.MY_SCENES.remove(sb);
                                                workMood.remove(sb);
                                                if (workMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new messageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        romance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (romanceMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Romance Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Romance Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<romanceMood.size();i++) {
                                        SceneBean sb = romanceMood.get(i);
                                        TuyaHomeSdk.newSceneInstance(romanceMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new messageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                MyApp.MY_SCENES.remove(sb);
                                                romanceMood.remove(sb);
                                                if (romanceMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new messageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Read Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Read Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<readMood.size();i++) {
                                        SceneBean sb = readMood.get(i);
                                        int finalI = i;
                                        TuyaHomeSdk.newSceneInstance(readMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new messageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                MyApp.MY_SCENES.remove(sb);
                                                readMood.remove(sb);
                                                if (readMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new messageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        masterOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (masterOffMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","MasterOff Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("MasterOff Mood ")
                            .setMessage("what you want to do with MasterOff mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<masterOffMood.size();i++) {
                                        SceneBean sb = masterOffMood.get(i);
                                        int finalI = i;
                                        TuyaHomeSdk.newSceneInstance(masterOffMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new messageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                MyApp.MY_SCENES.remove(sb);
                                                masterOffMood.remove(sb);
                                                if (masterOffMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new messageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
    }
}