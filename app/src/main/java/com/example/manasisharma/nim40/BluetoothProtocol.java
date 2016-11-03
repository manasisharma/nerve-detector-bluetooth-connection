package com.example.manasisharma.nim40;

/**
 * Created by manasisharma on 8/20/15.
 */

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BluetoothProtocol {

    public static final String TAG = "NIM-BluetoothProtocol";
    public static final boolean DEBUG = BaseActivity.DEBUG;

    public static final long HEADER_PACKET_SIZE = 24; // Bytes in the header packet
    public static final long MSG_TYPE_START_INDEX = 8;
    public static final long MESSAGE_LENGTH_SIZE = 4; // If this changes, remember to adjust MSG_SIZE_STOP_INDEX and other indices accordingly
    public static final int MAX_MESSAGE_SIZE = 0x40000000; // Limit to ~1GB

    public static final String WEARABLE_ALERT_PATH = "/nim-alert";


    //
    // Message byte order:
    //
    public static final int MSG_SIZE_START_INDEX = 0;
    public static final int MSG_TYPE = 0;

    public static final String FILE_NAME = "";
    public static final long TEXT_SIZE_START_INDEX = 4;
    public static final int FILE_SIZE_START_INDEX = 8;


    public static class BluetoothHeader {
        public  long msize;           // Size of Message #1
        public  int type;             // Type of File #2
        public  int string_size;     // string size #3
        public  String fname;         // Name of the file as String #5
        public  long fsize;           // Size of the file #4
        public  String text;
        public Bitmap bmp;

        public BluetoothHeader(long messagesize, int type, int string_size, long fsize, String fname) {
            this.msize = messagesize;
            this.type = type;
            this.string_size = string_size;
            this.fname = fname;
            this.fsize = fsize;

        }
        public BluetoothHeader (){

        }


        public BluetoothHeader parseHeader(byte[] rawBuffer) throws IOException {
            long msize = 0;    //#1
            int type = 1;      //#2
            String fname = ""; //#5
            long fsize = 0;    //#4
            int ssize =0;      //#3
            if (rawBuffer.length < HEADER_PACKET_SIZE) {
                Log.w(TAG, "Header buffer has invalid length: " + rawBuffer.length);
                return null;
            }


            ByteBuffer bb = ByteBuffer.wrap(rawBuffer, 0, (int) HEADER_PACKET_SIZE);

            // Extract message type
            msize = bb.getLong(0); //#2
            type = bb.getInt(8);
            //   type = BluetoothProtocol.parseMessageSize(rawBuffer);//#1
            ssize = bb.getInt(12);
            fsize = bb.getLong(16);
            //fname will be empty

            // ByteBuffer c = ByteBuffer.wrap(rawBuffer, 24, 24+ssize);
// Extract message
            fname = new String(rawBuffer, 24, ssize);
            return new BluetoothHeader(msize, type, ssize ,fsize, fname);
        }


        public void parse(byte[] rawBuffer, long offset,long fsize,String fname) throws IOException {
            File photo=new File(Environment.getExternalStorageDirectory(), fname);

          /*  if (photo.exists()) {
                photo.delete();
            }

*/

            try {
                FileOutputStream fos=new FileOutputStream(photo.getPath(),true);

                fos.write(rawBuffer, (int) offset, (int) fsize);

                fos.close();
            }
            catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }

        }
    }

    public static long parseMessageSize(byte[] buffer) {
        if (buffer == null) {
            Log.e(TAG, "Null buffer!");
            return -1;
        }

        // Extract message size
        ByteBuffer msgSizeBuf = ByteBuffer.wrap(buffer); // 8 is for the 64 bit signed int
        return msgSizeBuf.getLong(0);
    }



}


