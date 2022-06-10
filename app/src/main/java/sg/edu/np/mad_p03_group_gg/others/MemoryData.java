package sg.edu.np.mad_p03_group_gg.others;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MemoryData {
    public static void saveLastMsgTimeStamp(String data, String chatId, Context context){
        try{
            FileOutputStream fileOutputStream = context.openFileOutput(chatId+".txt", Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String getLastMsgTimeStamp (Context context, String chatId){
        String data = "0";
        try{
            FileInputStream fis = context.openFileInput(chatId+".txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return data;
    }
}
