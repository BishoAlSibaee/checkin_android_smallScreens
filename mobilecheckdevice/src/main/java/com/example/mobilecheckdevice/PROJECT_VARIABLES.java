package com.example.mobilecheckdevice;

import org.json.JSONException;
import org.json.JSONObject;

public class PROJECT_VARIABLES {
    public int id;
    public String projectName;
    public int Hotel ;
    JSONObject ServiceSwitchButtons ;
    public int Temp ;
    public int Interval;
    public int DoorWarning;
    public int CheckinModeActive;
    public int CheckinModeTime;
    public String CheckinActions;
    public int CheckoutModeActive;
    public int CheckoutModeTime;
    public String CheckoutActions;
    public String WelcomeMessage;
    public String Logo;
    public int PoweroffClientIn;
    public int PoweroffAfterHK;
    private int ACSenarioActive;
    public String OnClientBack;
    public int HKCleanTime;
    int cleanupButton,laundryButton,dndButton,checkoutButton;

    public PROJECT_VARIABLES(int id, String projectName, int hotel, int temp, int interval, int doorWarning, int checkinModeActive, int checkinModeTime, String checkinActions, int checkoutModeActive, int checkoutModeTime, String checkoutActions, String welcomeMessage, String logo, int poweroffClientIn, int poweroffAfterHK, int ACSenarioActive, String onClientBack, int HKCleanTime) {
        this.id = id;
        this.projectName = projectName;
        Hotel = hotel;
        Temp = temp;
        Interval = interval;
        DoorWarning = doorWarning;
        CheckinModeActive = checkinModeActive;
        CheckinModeTime = checkinModeTime;
        CheckinActions = checkinActions;
        CheckoutModeActive = checkoutModeActive;
        CheckoutModeTime = checkoutModeTime;
        CheckoutActions = checkoutActions;
        WelcomeMessage = welcomeMessage;
        Logo = logo;
        PoweroffClientIn = poweroffClientIn;
        PoweroffAfterHK = poweroffAfterHK;
        this.ACSenarioActive = ACSenarioActive;
        OnClientBack = onClientBack;
        this.HKCleanTime = HKCleanTime;
    }

    boolean getAcSenarioActive() {
        if (ACSenarioActive == 1) {
            return true ;
        }
        return false ;
    }

    public void setAcSenarioActive(int ac) {
        ACSenarioActive = ac ;
    }

    boolean getCheckoutModeActive() {
        if (CheckoutModeActive == 1) {
            return true ;
        }
        return false ;
    }

    boolean getCheckinModeActive() {
        if (CheckinModeActive == 1) {
            return true ;
        }
        return false ;
    }

    public JSONObject getServiceSwitchButtons() {
        return ServiceSwitchButtons;
    }

    public void setServiceSwitchButtons(JSONObject serviceSwitchButtons) {
        ServiceSwitchButtons = serviceSwitchButtons;
        try {
            if (serviceSwitchButtons.getInt("cleanup") != 0) {
                cleanupButton = serviceSwitchButtons.getInt("cleanup");
            }
            if (serviceSwitchButtons.getInt("laundry") != 0) {
                laundryButton = serviceSwitchButtons.getInt("laundry");
            }
            if (serviceSwitchButtons.getInt("dnd") != 0) {
                dndButton = serviceSwitchButtons.getInt("dnd");
            }
            if (serviceSwitchButtons.getInt("checkout") != 0) {
                checkoutButton = serviceSwitchButtons.getInt("checkout");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
