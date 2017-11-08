package com.yso.mybranch.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Created by Admin on 23-Oct-17.
 */

public final class ReadExcel
{
    private static final String TAG = ReadExcel.class.getSimpleName();

    public static void readExcelFile(Context context, String filename)
    {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.e(TAG, "Storage not available or read only");
            return;
        }

        try
        {
            // Creating Input Stream
            AssetManager am = context.getAssets();
            InputStream inputStream = am.open(filename);
            File file = /*new File(context.getExternalFilesDir(null), filename)*/createFileFromInputStream(inputStream, filename);
            ;
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            while (rowIter.hasNext())
            {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while (cellIter.hasNext())
                {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " + myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return;
    }

    private static File createFileFromInputStream(InputStream inputStream, String fileName)
    {

        try
        {
            File f = new File(fileName);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0)
            {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isExternalStorageReadOnly()
    {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState))
        {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable()
    {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState))
        {
            return true;
        }
        return false;
    }
}
