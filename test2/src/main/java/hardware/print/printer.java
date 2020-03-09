package hardware.print;
import android.content.Context;
import android.graphics.Bitmap;

/************************************************************
 * Copyright 2000-2066 Olc Corp., Ltd.
 * All rights reserved.
 * <p>
 * Description     : The Main activity for the Camera application
 * History        :( ID, Date, Author, Description)
 * v1.0, 2017/1/20,  Administrator, create
 ************************************************************/

public class printer {
    public int Open(Context context)
    {
        return 1;
    }
    public int Close()
    {
        return 0;
    }
    public int Step(byte bStep) { return -1; }
    public int Unreeling(byte bStep) { return -1; }
    public void GoToNextPage() { }
    //public int PrintImage(short[] data)
    public int PrintImageEx(byte[] data,int nBit)
    {
        return -1;
    }
    public int PrintString24(byte[] data)
    {
        return -1;
    }
    public int IsReady()
    {
        return -1;
    }
    public int SetGrayLevel(byte blevel)
    {
        return -1;
    }
    public int ReadData(byte[] data)
    {
        return -1;
    }
    public int ReadDataEx(byte[] data, int noffset, int ncount)
    {
        return -1;
    }
    public void PrintBitmapAtCenter(Bitmap bm, int labelHeight){

    }

}