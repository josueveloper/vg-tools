/*
 * Copyright 2024 Josue Ruiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vgtools.utils;

import java.io.BufferedWriter;
import java.io.IOException;

public final class ConsoleManager {
    public static char[][] create() {
        int rows = 24;
        int cols = 80;
        char[][] matrix = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = 'x';
            }
        }
        return matrix;
    }

    public static void update(String text, char[][] matrix, int[] cursor) {
        if (matrix == null)
            return;

        text = text.replace("\\x1b[0;1;7m", "").replace("\\x1b[0;1m", "").replace("\\x1b[0m", "")
                .replace("\\x1b[0;7m", "").replace("\\x1b[0;1;7;5m", "").replace("\\x1b[0;5m", "")
                .replace("\\x1b[7m", "").replace("\\x1b[m", "").replace("\\x0f", "").replace("\\x1b[?25h", "")
                .replace("\\x1b[?25l", "").replace("\\x1b[?", "").replace("\\x1b(B", "").replace("\\x1b)0", "");
        text = toFormattedString(text);

        int i = 0;
        int row = cursor[0];
        int col = cursor[1];
        while (i < text.length()) {
            // # retorno de carro
            if (text.charAt(i) == 13)
                col = 0;
            // # backspace
            else if (text.charAt(i) == 8) {
                if (col > 0)
                    col = col - 1;
                else if (col == 0 && row > 0) {
                    row = row - 1;
                    col = 79;
                }
            }
            // # salto de linea
            else if (text.charAt(i) == 10) {
                if (row < 23)
                    row = row + 1;
            } else if (text.charAt(i) == 27) {
                if (i + 2 >= text.length())
                    ;

                // # Mover cursor una posicion arriba
                else if (text.charAt(i + 2) == 'A') {
                    if (row > 0)
                        row = row - 1;
                    i = i + 2;
                }
                // # Borrar desde la posición actual del cursor hasta el final de la línea
                else if (text.charAt(i + 2) == 'K') {
                    for (int j = col; j < 80; j++) {
                        matrix[row][j] = ' ';
                    }
                    i = i + 2;
                }
                // # Mover cursor una posicion a la derecha
                else if (text.charAt(i + 2) == 'C') {
                    if (col < 79)
                        col = col + 1;
                    i = i + 2;
                }
                // # Volver al inicio
                else if (text.charAt(i + 2) == 'H') {
                    col = 0;
                    row = 0;
                    i = i + 2;
                }
                // # Borra desde el cursor hasta el final de la pantalla
                else if (text.charAt(i + 2) == 'J') {
                    int _col = col;
                    for (int j = row; j < 24; j++) {
                        for (int k = _col; k < 80; k++) {
                            matrix[j][k] = ' ';
                        }
                        _col = 0;
                    }
                    i = i + 2;
                }
                // # Coordenadas
                else if (text.charAt(i + 2) >= 49 && text.charAt(i + 2) <= 57) {
                    if (text.charAt(i + 3) >= 48 && text.charAt(i + 3) <= 57) {
                        row = Integer.parseInt(new String(new char[] { text.charAt(i + 2), text.charAt(i + 3) })) - 1;
                        i = i + 3;
                    } else {
                        row = Integer.parseInt(new String(new char[] { text.charAt(i + 2) })) - 1;
                        i = i + 2;
                    }
                    if (text.charAt(i + 3) >= 48 && text.charAt(i + 3) <= 57) {
                        col = Integer.parseInt(new String(new char[] { text.charAt(i + 2), text.charAt(i + 3) })) - 1;
                        i = i + 4;
                    } else {
                        col = Integer.parseInt(new String(new char[] { text.charAt(i + 2) })) - 1;
                        i = i + 3;
                    }
                }
            } else {
                if (col >= 80) {
                    col = 0;
                    if (row < 23) {
                        row = row + 1;
                    }
                    matrix[row][col] = text.charAt(i);
                } else {
                    matrix[row][col] = text.charAt(i);
                    col = col + 1;
                }
            }
            i = i + 1;
        }
        cursor[0] = row;
        cursor[1] = col;
    }

    public static void log(char[][] matrix, BufferedWriter writer) throws IOException {
        for (char[] row : matrix) {
            writer.write(row);
            writer.write("\n");
        }
    }

    private static String toFormattedString(String str) {
        return str.toString()
                .replaceAll("\\\\x1b", "\u001B")
                .replaceAll("\\\\x0f", "\u000F")
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\r", "\r");
    }
}
