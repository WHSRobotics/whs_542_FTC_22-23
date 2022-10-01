package org.whitneyrobotics.ftc.teamcode.lib.util;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class DataTools {

    public static void encode(Context appContext, Object[] rawData, String fileName){
        Data formattedItems = new Data<Object>();
        for(int i = 0; i< rawData.length; i++){
            formattedItems.put(i,rawData[i]);
        }
        encode(appContext,formattedItems,fileName);
    }

    //I think you can use hardwareMap.appContext for this
    public static void encode(Context appContext, Data<Object> data, String fileName){
        File dir = new File(appContext.getFilesDir(),"TeamData");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File content = new File(dir, fileName);
            FileWriter encoder = new FileWriter(content);
            for(Object i : data.keySet()){
                encoder.append(String.format("%s=%s\n",i.toString(), data.get(i).toString()));
            }
            encoder.flush();
            encoder.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Data<Object> decode(Context appContext, String fileName){
        Data<Object> data = new Data<>();
        try {
            File child = new File(appContext.getFilesDir()+"/TeamData/"+fileName);
            Scanner parser = new Scanner(child);
            while (parser.hasNextLine()) {
                String content = parser.nextLine();
                String[] contentSeparated = content.split("=");
                data.put(contentSeparated[0],contentSeparated[1]);
            }
            parser.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return data;
    }

    public static class Data<Object> extends LinkedHashMap {
        public Data(){super();}

        public void put(String key, Object value){
            if (key.contains("=") || key.contains(",")) {
                throw new IllegalArgumentException("Forbidden Character in Key");
            }
            super.put(key,value);
        }

    }
    //hasMap.put(key,value);

}

