package com.example.navabattlebsj.persistences;

import com.example.navabattlebsj.patterns.GameState;

import java.io.*;

/**
 * Encargado exclusivamente de serializar y deserializar el {@link GameState}
 * completo (tableros, flotas, turno) a/desde un archivo binario .dat.
 */
public class SerializationManager {

    public void save(GameState state, String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(state);
        }
    }

    public GameState load(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (GameState) in.readObject();
        }
    }

    public boolean exists(String filePath) {
        return new File(filePath).exists();
    }

    public void delete(String filePath) {
        File file = new File(filePath);
        if (file.exists()) file.delete();
    }
}