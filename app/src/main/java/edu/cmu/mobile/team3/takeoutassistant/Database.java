package edu.cmu.mobile.team3.takeoutassistant;

import android.os.Environment;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wgtmac on 7/29/15.
 */
public class Database {
    private static final String DATABASE = Environment
            .getExternalStorageDirectory().toString() + "/TakeoutAssistant-master/database.txt";

    public static void write(List<PaperMenu> list) {
        if (list == null || list.size() == 0) return;

        FileWriter out = null;
        String json = JsonWriter.objectToJson(list);

        try {
            out = new FileWriter(DATABASE);
            out.write(json);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<PaperMenu> read() {
        List<PaperMenu> list = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(DATABASE);
            if (in != null) {
                JsonReader jr = new JsonReader(in);
                list = (List<PaperMenu>) jr.readObject();
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                list = new ArrayList<PaperMenu>();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }
}
