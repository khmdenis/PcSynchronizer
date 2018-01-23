package com.mykolyuk.pcsynchronizer;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class SynchronizeTask extends AsyncTask<Void, Void, Boolean>{

    private static final String TAG = "Synchronization";
    private Activity context;
    private File mainDirectory;
    private long startTimer, stopTimer;
    private static final long NANO = 1_000_000_000;
    private ProgressDialog progress;
    private int countOfFiles = 0;

    public SynchronizeTask(Activity context, File directory) {
        this.context = context;
        mainDirectory =  directory;
    }

    @Override
    protected void onPreExecute() {
         startTimer = System.nanoTime();
        Log.i(TAG, "Start data transfer...");
        progress = new ProgressDialog (context);
        progress.setMessage("Connection...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            stopTimer = System.nanoTime();
            long resultTime = stopTimer - startTimer;
            Log.i(TAG,"File sent for: " + resultTime / NANO + "sec");
        } else {
            Log.i(TAG,"Error!");
            showDialog("Please, start server on PC");
        }

        if(progress != null) {
            progress.dismiss();
        }
        Toast.makeText(context, "Download " + countOfFiles + " files.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try (
                Socket clientSocket = new Socket("192.168.0.106", 1755);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream())
        ) {
            progress.setMessage("Synchronizing...");
            synchronizeFolder(mainDirectory, outToServer, inFromServer);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
        return true;
    }


    private void synchronizeFolder(File folder, DataOutputStream out, DataInputStream in) throws IOException {
        folder.mkdir();
        List<File> existFiles = Arrays.asList(folder.listFiles());
        int fileCount;
        out.writeInt(existFiles.size());
        for (File file : existFiles) {
            out.writeUTF(file.getName());
        }
        fileCount = in.readInt();
        int n;
        byte[] buf = new byte[4092];
        for (int i = 0; i < fileCount; i++) {
            boolean isDir = in.readBoolean();
            String fileName = in.readUTF();
            File file = new File(folder, fileName);
            if (isDir) {
                file.mkdir();
                synchronizeFolder(file, out, in);
                continue;
            }
            long fileSize = in.readLong();
            FileOutputStream fos = new FileOutputStream(file);
            while (fileSize > 0 && (n = in.read(buf, 0, (int) Math.min(buf.length, fileSize))) != -1) {
                fos.write(buf, 0, n);
                fileSize -= n;
            }
            fos.close();
            countOfFiles++;
        }

    }

    private void showDialog(String message) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Error");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}
