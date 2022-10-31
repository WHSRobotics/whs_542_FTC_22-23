package org.whitneyrobotics.ftc.teamcode.lib.file;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.util.ReadWriteFile;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiresApi(api = Build.VERSION_CODES.N)
public final class RobotDataUtil {

    @JsonIgnored
    static AppUtil util = AppUtil.getInstance();

    @JsonIgnored
    public synchronized static Field[] getClassWriteableFields(Class<?> dataClass){
        return Arrays.stream(dataClass.getFields()).filter(f ->
                f.getAnnotation(JsonIgnored.class) == null && Modifier.isStatic(f.getModifiers()) && f.getType().isPrimitive()
        ).toArray(Field[]::new);
    }

    @JsonIgnored
    public synchronized static void save(Class<?> dataClass){
        save(dataClass, dataClass.getName());
    }

    @JsonIgnored
    public synchronized static void save(final Class<?> dataClass, String filename){
        Map<String, Object> valueMap = new Hashtable<>();
        Field[] fields = getClassWriteableFields(dataClass);
        for (Field f : fields){
            try {
                if(f.getName() != null) {
                    valueMap.put(f.getName(), f.get(null));
                }
            } catch (IllegalAccessException e) {
                RobotLog.ee(String.format("Failed to capture internal class field %s",f.getName()), e.getMessage());
            }
        }
        JSONObject json = new JSONObject(valueMap);
        File file = loadFile(filename, ".json");
        ReadWriteFile.writeFile(file,json.toString());

    }

    @JsonIgnored
    public synchronized static void load(Class<?> dataClass) {
        load(dataClass, dataClass.getName());
    }

    @JsonIgnored
    public synchronized static void load(Class<?> dataClass, String filename){
        File file = loadFile(filename, ".json");
        try {
            JSONObject json = new JSONObject(ReadWriteFile.readFile(file));
            for (Field f : getClassWriteableFields(dataClass)){
                Class<?> clazz = f.getType();
                try {
                    if(json.has(f.getName())) {
                        f.set(dataClass, json.get(f.getName()));
                    }
                } catch (Exception e){
                    RobotLog.ee(String.format("Could not set field %s", f.getName()),e.getLocalizedMessage());
                }
            }
        } catch (JSONException e) {
            RobotLog.ee(String.format("Unable to parse JSON file %s",filename+".json"),e.getLocalizedMessage());
        }
    }

    @JsonIgnored
    public static synchronized File loadFile(String filename, String ext){
        File file = new File(filename + ext);
        if (!file.isAbsolute())
        {
            util.ensureDirectoryExists(file,true);
            file = new File(AppUtil.ROBOT_DATA_DIR, filename + ext);
        }
        return file;
    }
}
