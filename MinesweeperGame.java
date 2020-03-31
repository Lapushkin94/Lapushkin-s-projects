package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private static final String MINE = "💣";
    private static final String FLAG = "🚩";
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                setCellValue(x, y, "");
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE);
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if ((gameField[y][x]).isMine == false) {
                    for (GameObject i : getNeighbors(gameField[y][x])) {
                        if (i.isMine == true) {
                            (gameField[y][x]).countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (((gameField[y][x]).isOpen == false) && ((gameField[y][x]).isFlag == false) && (isGameStopped == false)) {

            if ((gameField[y][x]).isMine == true) {
                setCellValueEx(x, y, Color.RED, MINE); // Убрал строку isOpen = true;
                gameOver();
            }
            else {
                if ((gameField[y][x]).countMineNeighbors == 0) {
                    setCellValue(x, y, "");
                    (gameField[y][x]).isOpen = true;
                    setCellColor(x, y, Color.GREEN);
                    score = score + 5;

                    for (GameObject i : getNeighbors(gameField[y][x])) {
                        openTile(i.x, i.y);
                    }
                } else {
                    setCellNumber(x, y, (gameField[y][x]).countMineNeighbors);
                    (gameField[y][x]).isOpen = true;
                    setCellColor(x, y, Color.YELLOW);
                    score = score + 5;
                }
                countClosedTiles--;
            }
            setScore(score);
            if (countClosedTiles == countMinesOnField) {
                win();
            }
        }
    }

    private void markTile(int x, int y) {
        if (isGameStopped == false) {
            if ((!(gameField[y][x]).isOpen) && (countFlags != 0) && ((gameField[y][x]).isFlag == false)) {
                (gameField[y][x]).isFlag = true;
                countFlags--;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.BEIGE);
            } else if ((!(gameField[y][x]).isOpen) && ((gameField[y][x]).isFlag == true)) {
                (gameField[y][x]).isFlag = false;
                countFlags++;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.ORANGE);
            }
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped == true) {
            restart();
        }
        else {
            openTile(x, y);
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BROWN, "Game over!", Color.BISQUE, 80);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.DARKCYAN, "You win!", Color.SALMON, 80);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        createGame();
    }


}