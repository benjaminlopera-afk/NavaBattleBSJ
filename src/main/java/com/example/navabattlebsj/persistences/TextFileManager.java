package com.example.navabattlebsj.persistences;

import java.io.*;

/**
 * Encargado del archivo plano de la partida: nickname del jugador y
 * cantidad de barcos hundidos por cada bando (HU-5).
 * Formato: una línea de texto separada por comas.
 * nickname,barcosHundidosJugador,barcosHundidosMaquina
 */
public class TextFileManager {

    public void save(String filePath, String nickname, int humanSunkByMachine, int machineSunkByHuman) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(nickname + "," + humanSunkByMachine + "," + machineSunkByHuman);
        }
    }

    public String[] load(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line == null) return null;
            return line.split(",");
        }
    }
}