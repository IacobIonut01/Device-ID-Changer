package com.iacob.idchanger.id_parser;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class rootCheck {

    public static boolean IAmRoot() {
        boolean retval = false;
        Process suProcess;

        try {
            suProcess = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

            if (null != os && null != osRes) {
                os.writeBytes("id\n");
                os.flush();
                String currUid = osRes.readLine();
                boolean exitSu = false;
                if (null == currUid) {
                    retval = false;
                    exitSu = false;
                    Log.d("ROOT", "Can't get root access or denied by user");
                } else if (currUid.contains("uid=0")) {
                    retval = true;
                    exitSu = true;
                    Log.d("ROOT", "Root access granted");
                } else {
                    retval = false;
                    exitSu = true;
                    Log.d("ROOT", "Root access rejected: " + currUid);
                }
                if (exitSu) {
                    os.writeBytes("exit\n");
                    os.flush();
                }
            }
        } catch (Exception e) {
            retval = false;
            Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
        }

        return retval;
    }

    public final boolean execute() {
        boolean retval = false;
        try {
            ArrayList<String> commands = getCommandsToExecute();
            if (null != commands && commands.size() > 0) {
                Process suProcess = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                for (String currCommand : commands) {
                    os.writeBytes(currCommand + "\n");
                    os.flush();
                }
                os.writeBytes("exit\n");
                os.flush();
                try {
                    int suProcessRetval = suProcess.waitFor();
                    retval = 255 != suProcessRetval;
                } catch (Exception ex) {
                    Log.e("ROOT", "Error executing root action", ex);
                }
            }
        } catch (IOException | SecurityException ex) {
            Log.w("ROOT", "Can't get root access", ex);
        } catch (Exception ex) {
            Log.w("ROOT", "Error executing internal operation", ex);
        }
        return retval;
    }

    public static boolean execute(String command) {
        boolean retval = false;
        try {
            Process suProcess = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            os.writeBytes(command + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            try {
                int suProcessRetval = suProcess.waitFor();
                retval = 255 != suProcessRetval;
            } catch (Exception ex) {
                Log.e("ROOT", "Error executing root action", ex);
            }
        } catch (IOException | SecurityException ex) {
            Log.w("ROOT", "Can't get root access", ex);
        } catch (Exception ex) {
            Log.w("ROOT", "Error executing internal operation", ex);
        }
        return retval;
    }

    public static String read(String paramString) {
        try {
            Process process = Runtime.getRuntime().exec("su\n");
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(paramString);
            stringBuilder.append("\nexit\n");
            dataOutputStream.writeBytes(stringBuilder.toString());
            dataOutputStream.flush();
            try {
                process.waitFor();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            if (bufferedReader.ready())
                return bufferedReader.lines().collect(Collectors.joining());
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
        return "";
    }

    protected abstract ArrayList<String> getCommandsToExecute();
}
