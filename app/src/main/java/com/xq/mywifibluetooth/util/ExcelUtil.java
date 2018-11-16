package com.xq.mywifibluetooth.util;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.xq.mywifibluetooth.MyApplication;

import java.io.File;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelUtil {

    public static String getExcelDir() {
        // SD卡指定文件夹
        String sdcardPath = Environment.getExternalStorageDirectory().toString();
        File dir = new File(sdcardPath + File.separator + "Excel"+ File.separator + "Person");
        if (dir.exists()) {
            return dir.toString();
        } else {
            dir.mkdirs();
            Log.e("BAG", "保存路径不存在,");
            return dir.toString();
        }
    }

    public static void saveExcel(String[] codeList, String[] sheets, String sheetName) throws Exception{
        String excelPath = getExcelDir()+ File.separator+"wify与蓝牙记录表.xls";
        File file = new File(excelPath);
        WritableSheet ws = null;
        WritableWorkbook wwb = null;
        if (!file.exists()) {
            wwb = Workbook.createWorkbook(file);
            WritableSheet ws1 = wwb.createSheet(sheetName, 0);
            WritableSheet ws2 = wwb.createSheet(sheetName, 1);
            if(null!=sheetName && "wify".equals(sheetName)){
                ws = ws1;
            }else {
                ws = ws2;
            }
            int i = 0;
            for(String sheet:sheets){   // 在指定单元格插入数据
                Label lbl = new Label(i, 0, sheet);
                ws.addCell(lbl);
                i++;
            }
        }else {
            Workbook oldWwb = Workbook.getWorkbook(file);
            wwb = Workbook.createWorkbook(file, oldWwb);
            if(null!=sheetName && "wify".equals(sheetName)){
                ws = wwb.getSheet(0);
            }else {
                ws = wwb.getSheet(1);
            }
        }
        addExcelData(codeList, ws);
        wwb.write();
        wwb.close();
        Toast.makeText(MyApplication.getInstance(), "保存成功", Toast.LENGTH_SHORT).show();
    }

    public static void addExcelData(String [] codeList, WritableSheet ws) throws Exception {
        int row = ws.getRows(); // 当前行数
        int i = 0;
        for(String code:codeList){
            Label lbl = new Label(i, row, code);
            ws.addCell(lbl);
            i++;
        }
    }
}
