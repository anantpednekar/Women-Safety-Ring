package com.example.wsr_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputBinding;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {

    private static final String TAG = "//***** BCS *****//";

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    private ConnectThread mConnectThread;
    private BluetoothDevice mDevice;
    private UUID deviceUUID;
    private ConnectedThread mConnectedThread;
    private ByteArrayOutputStream BAOS;



    public BluetoothConnectionService(Context context,BluetoothDevice FromMain) {

        Log.d(TAG, " : BluetoothConnectionService() Constructor Called ");

        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();

        startClient(FromMain,MY_UUID_INSECURE,mContext);
    }

    public void startClient(BluetoothDevice device,UUID uuid,Context context){

        Log.d(TAG," startClient - : - started");

        mConnectThread = new ConnectThread(device,uuid,context);

        mConnectThread.setName("ConnectThread");

        mConnectThread.start();
    }

    private class ConnectThread extends Thread{
        private BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device,UUID uuid,Context context){

            Log.d(TAG," ConnectThread : Constructor Called");


            mDevice = device;
            deviceUUID = uuid;
            mContext = context;


        }
        public void run(){

            BluetoothSocket tmp = null;

            Log.d(TAG," run :  RUN ConnectThread  ");



            try {
                Method methd;
                methd = mDevice.getClass().getMethod("createRfcommSocket", new Class [] {int.class});
                tmp = (BluetoothSocket) methd.invoke(mDevice,1);
            } catch (NoSuchMethodException e) {

                Log.d(TAG," run :  NoSuchMethodException  ");
                e.printStackTrace();

            } catch (IllegalAccessException e) {

                Log.d(TAG," run :  IllegalAccessException  ");

                e.printStackTrace();

            } catch (InvocationTargetException e) {

                Log.d(TAG," run :  InvocationTargetException  ");

                e.printStackTrace();
            }


            Log.d(TAG," run : createRfcommSocketToServiceRecord Socket Created");

            mSocket = tmp;

            try {
                mSocket.connect();

                Log.d(TAG," run : mSocket connected");

                connected(mSocket,mDevice,mContext);

            }catch (IOException e){

                Log.e(TAG," run : ConnectThread: Could not connect to UUID :"+e.getMessage());

                try{
                    mSocket.close();

                    Log.d(TAG," run : Closed Socket");

                }catch(IOException e1){
                    Log.e(TAG," mConnectThread : run : Unable to close socket : "+e.getMessage());
                }
            }
        }
    }

    private void connected(BluetoothSocket mSocket,BluetoothDevice mDevice,Context context){

        Log.d(TAG," connected() Called");

        mConnectedThread = new ConnectedThread(mSocket,context);
        mConnectedThread.setName("ConnectedThread");
        mConnectedThread.start();
    }

    private static class ConnectedThread extends Thread {
        private final BluetoothSocket msocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;
        Context mContext;

        public ConnectedThread(BluetoothSocket socket,Context context){

            Log.d(TAG," ConnectedThread : Constructor Called");

            mContext = context;
            msocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {

                tmpIn = msocket.getInputStream();
                tmpOut = msocket.getOutputStream();

            }catch (IOException e){

                e.printStackTrace();

                Log.d(TAG," ConnectedThread : Error getting I/O Stream "+e.getMessage());

            }
            mInputStream = tmpIn;
            mOutputStream = tmpOut;

        }

        public void run(){

            Log.d(TAG," run : ConnectedThread ");

            byte[] buffer;
            byte[] FinalImageByteBuffer;

            int bytes;
            int ImageSegCount = 0;
            int NoofImage = 5;
            int FileCount = 0;

            int chunkSize = 990;
            String ImageName;
            String Final_Image_String;

            File Directory_path = null;
            FileOutputStream fos = null;
            File Pic_file =null;

            ByteArrayOutputStream BAOS;
            //Make a Seperate Directory in Phone Memory To Save Pictures
            Directory_path = new File(Environment.getExternalStorageDirectory()+"/WSR");
            if(!Directory_path.exists()){
                Log.d(TAG," run : ConnectedThread  : Directory_path.mkdir() Called ");
                Directory_path.mkdir();
            }
            while (true){

                try {
                    // Continously Read InputStream
                    buffer = new byte[chunkSize];
                    bytes = 0;
                    bytes = mInputStream.read(buffer);//Read Data From Input Stream
                    //If No Data Recieved Continue Reading
                    if(bytes == 0)
                        continue;
                    else{
                        Log.d(TAG," run : ConnectedThread  : Data Receiving Started ..... " );

                        liveloc loc = new liveloc(mContext);
                        loc.start();
                        //Iterate For Number of Images Times

                        for(int j = 0;j<NoofImage; j++){
                            BAOS = new ByteArrayOutputStream();
                            if(j>0)
                                bytes = mInputStream.read(buffer);
                            //Read no of transmissions Required
                            ImageSegCount = Integer.parseInt(new String(buffer, 0, bytes));
                            Log.d(TAG," run : ConnectedThread  : Recieved Segment Count as :" + ImageSegCount );
                            //Iterate for ImageSegCount times //Receiving chunkSize Byte Array //Sending Intermediate Acknowledgement WSR_ACK
                            for(int i=0;i<ImageSegCount;i++){
                                bytes = mInputStream.read(buffer);
                                //check for empty bytes
                                if(bytes == 0){
                                    //If Empty Bytes Redo the Iteration
                                    i--;
                                    continue;
                                }
                                //Write The Byte Array in Sequence to the  ByteArrayOutputStream
                                BAOS.write(buffer);


                                //Refresh The Buffer
                                buffer = null;
                                buffer = new byte[chunkSize];
                                //Send Intermediate Acknowledgement WSR_ACK
                                mOutputStream.write("WSR_ACK".getBytes());
                                mOutputStream.flush();
                            }

                            Log.d(TAG," run : ConnectedThread  : Received (" + j + ") Image Data" );

                            //Once Done Send Final Acknowledgement WSR_DNE
                            mOutputStream.write("WSR_DNE".getBytes());

                            Log.d(TAG," run : ConnectedThread  : Send WSR_DNE for Image " + j );

                            try {

                                ImageName = "WSR_Image_Panic_"+(FileCount++)+".jpeg";
                                Pic_file = new File(Directory_path,ImageName);
                                fos = new FileOutputStream(Pic_file);
                                //Convert ByteArrayOutputStream to String
                                Final_Image_String = BAOS.toString();



                                //fos.write(Final_Image_String.getBytes());



                                //Convert Base64 String to Byte Array
                                FinalImageByteBuffer = Base64.decode(Final_Image_String,Base64.DEFAULT);
                                //Convert Byte Array To Bitmap
                                Bitmap bmp = BitmapFactory.decodeByteArray(FinalImageByteBuffer,0,FinalImageByteBuffer.length);
                                //Save Bitmap as JPEG File
                                bmp.compress(Bitmap.CompressFormat.JPEG,100,fos);







                                Log.d(TAG," run : ConnectedThread  : Image " + j +" Compression Successfull");


                                //Send to Upload in Cloud
                                //Pic_file

                                Log.d(TAG," run : ConnectedThread  : Creating UploadThread IUT Object ");

                                UploadThread IUT = new UploadThread(Pic_file,mContext);
                                IUT.start();

                                Log.d(TAG," run : ConnectedThread  : UploadThread IUT.start() Called ");
                                //Refresh Buffer and String
                                fos.flush();
                                BAOS.flush();
                                fos = null;
                                BAOS = null;
                                bmp = null;
                                Pic_file = null;
                                Final_Image_String = null;
                                FinalImageByteBuffer = null;

                            }
                            catch(java.io.IOException e){

                                Log.e(TAG, " Exception in Saving PICTURE -- "+e.getMessage());
                            }
                            //Go for Next Image
                            Log.d(TAG," run : ConnectedThread  : Going for Next Image ");
                        }
                        FileCount = 0;
                    }
                }catch (IOException e){

                    Log.e(TAG," write: Error Reading inputstream."+e.getMessage());

					/*
					Write Code to go Back to Main Activity  To Select again and connect again
					and make null

					*/


                    break;
                }

				//End of While
            }

            Log.d(TAG," run : ConnectedThread Run : Exiting ");


        }

		//End of ConnectedThread Class
    }




}