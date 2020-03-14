package me.kpotatto.simpletowns.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.kpotatto.simpletowns.towns.Town;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SerializerManager{

    private Gson gson;

    public SerializerManager() {
        this.gson = createGson();
    }

    private Gson createGson(){
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    public String serialize(Object o){
        return gson.toJson(o);
    }

    public void saveToFile(File file, String text){
        final FileWriter fw;
        try{
            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            fw = new FileWriter(file);
            fw.write(text);
            fw.flush();
            fw.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Town deserializeTown(String json){
        return gson.fromJson(json, Town.class);
    }

}
