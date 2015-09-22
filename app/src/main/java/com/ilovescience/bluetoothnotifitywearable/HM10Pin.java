package com.ilovescience.bluetoothnotifitywearable;


public class HM10Pin {
//    //public static final String IO_REQUEST = "AT+COL??";
//    public static final String IO_AT_CONNECTION = "AT+AFTC"; //append ? to query, 000-3FF to set
//    public static final String IO_AT_POWER_ON = "AT+BEFC"; //append ? to query, 000-3FF to set
//    public static final String IO_STATE = "AT+PIO"; //append pinChar and ? to query or 1/0 to Change OUTPUT to HIGH/LOW for this session

    private char pinChar; //IO pins from 0-B
    private int pinValue; //highest value B = 1,

    public static final String AT_IO_AtConnection="AT+AFTC";
    public static final String AT_IO_AtPowerOn="AT+BEFC";
    private String AT_IO_HIGH_AtPowerOn ="AT+BEFC";
    private String AT_IO_LOW_AtPowerOn ="AT+BEFC";
    private String AT_IO_PinQuery = "AT+PIO";
    private String AT_IO_HIGH_temporary="AT+PIO";
    private String AT_IO_LOW_temporary="AT+PIO";
    private String AT_VOLTAGE="AT+ADC";
    private String pinName="P";

    private String recentATcommand=""; //used to decide how to treat reply from AT command

    private boolean isOutput = false; //set true if the pin is an output
    private boolean isLOW = true; //by defualt pins are low
    private boolean pinState = false; //true if voltage is HIGH - either set to OUTPUT or connected to a voltage source
    private boolean reserved;
    //private boolean[] pinState= new boolean[2]; //0.0 0.1 1.0 1.1 = IN_LOW,IN_HIGH,OUT_LOW,OUT_HIGH,SYSTEM

    //private float inputVolt; //probably just return a value rather than store it.
    public HM10Pin(char _pinChar)
    {
        pinChar = _pinChar;

        //Naming the pin
        String tempString ="";
        tempString += pinChar;
        int tempInt = Integer.parseInt(tempString, 16);
        pinValue = 2048 >> tempInt;

        //Setting each command for this specific pin
        AT_IO_HIGH_AtPowerOn =AT_IO_HIGH_AtPowerOn + pinChar + '1';
        AT_IO_LOW_AtPowerOn += pinChar + '0';
        AT_IO_HIGH_temporary = AT_IO_HIGH_temporary + pinChar + '1';
        AT_IO_LOW_temporary = AT_IO_LOW_temporary + pinChar + '0';
        AT_IO_PinQuery = AT_IO_PinQuery + pinChar + '?';
        AT_VOLTAGE = AT_VOLTAGE + pinChar + '?';


        if(tempInt > 3)
        {
            pinName = pinName + '0'+'_'+ pinChar;
            //pinState = false;
            reserved = false;
        }
        else if(tempInt > 1) //only P1_1 and P1_0 can be used. P1_2 and P1_3 are reserved for the system
        {
            pinName = pinName +'1' + '_' + pinChar;
            //pinState = true; //I *think* output mode turns the pin on.
            reserved = false;
            //pinState = 1;
        }
        else
        {
            pinName = pinName + '1'+'_' + pinChar +" RESERVED";
            reserved = true;
            //pinState = 4;
        }
    }


    public String toString() {
        return (pinName);
    }
    //receive the incoming response from the HM-10 and
    public String ATreplyHandler(String reply)
    {
        String userMessage="";
        switch(recentATcommand)
        {
            case "AT":
                if(reply.equals("OK")) userMessage = "Everything is working correctly";
                else userMessage = "Something has gone wrong";
                break;
            case "AT+PIO":
                userMessage = pinName+" is ";
                if(reply.substring(7).equals("1"))
                    userMessage+= "HIGH";
                else
                    userMessage+= "LOW";

                break;

        }
        return userMessage;

    }
    public String getCommand(int commandIndex, int pinStates)
    {
        int newPinStates;
        String sPinStates = "";

        switch(commandIndex)
        {
            case 0:
                if(changeRequired(pinStates,true)) {
                    newPinStates = pinStates + pinValue;
                    sPinStates = AT_IO_AtPowerOn + intToThreeDigitHex(newPinStates);
                    return sPinStates;
                }
                else
                {
                    sPinStates = AT_IO_AtPowerOn + intToThreeDigitHex(pinStates);
                    return sPinStates;
                }
            case 1:
                return AT_IO_LOW_AtPowerOn;

            case 2: //High at at connection
                if(changeRequired(pinStates,true)) {
                    newPinStates = pinStates + pinValue;
                    sPinStates = AT_IO_AtConnection + intToThreeDigitHex(newPinStates);
                    return sPinStates;
                }
                else
                {
                    sPinStates = AT_IO_AtConnection + intToThreeDigitHex(pinStates);
                    return sPinStates;
                }

            case 3: //low at connection
                if(changeRequired(pinStates,false)) {
                    newPinStates = pinStates - pinValue;
                    sPinStates = AT_IO_AtConnection + intToThreeDigitHex(newPinStates);
                    return sPinStates;
                }
                else
                    sPinStates = AT_IO_AtConnection + intToThreeDigitHex(pinStates);
                return sPinStates;

            case 4:
                return AT_IO_HIGH_temporary;

            case 5:
                return AT_IO_LOW_temporary;

            case 6:
                return AT_IO_PinQuery;
            case 7:
                return AT_VOLTAGE;
            default:
                return "AT"; //Will produce a callback of OK, but nothing is actually done
        }
    }
    public String getATvoltageCommand()
    {
        return AT_VOLTAGE;
    }
    private boolean changeRequired(int _pinStates, boolean highLow) //determines if the state already matches what it is being set to
    {
        int tempInt = _pinStates & pinValue; //returns either 0 or pinValue

       return ((tempInt != 0) ^ highLow); //both high or both low returns false

    }

    private String intToThreeDigitHex(int _pinStates)
    {
        String shortString = Integer.toHexString(_pinStates).toUpperCase();

        if(shortString.length()<2){
            return ("0"+"0"+ shortString);
        }
        else if(shortString.length()<3) {
            return ("0"+shortString);
        }
        else
            return shortString;
    }
//    public void updateIOState(boolean _pinState)
//    {
//        pinState = _pinState;
//    }
}
