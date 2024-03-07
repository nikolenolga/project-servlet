package com.tictactoe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Field {
    private final Map<Integer, Sign> field;

    public Field() {
        field = new HashMap<>();
        field.put(0, Sign.EMPTY);
        field.put(1, Sign.EMPTY);
        field.put(2, Sign.EMPTY);
        field.put(3, Sign.EMPTY);
        field.put(4, Sign.EMPTY);
        field.put(5, Sign.EMPTY);
        field.put(6, Sign.EMPTY);
        field.put(7, Sign.EMPTY);
        field.put(8, Sign.EMPTY);
    }

    public Map<Integer, Sign> getField() {
        return field;
    }

    public int getEmptyFieldIndex() {
        return field.entrySet().stream()
                .filter(e -> e.getValue() == Sign.EMPTY)
                .map(Map.Entry::getKey)
                .findFirst().orElse(-1);
    }

    public List<Sign> getFieldData() {
        return field.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public Sign checkWin() {
        List<List<Integer>> winPossibilities = List.of(
                List.of(0, 1, 2),
                List.of(3, 4, 5),
                List.of(6, 7, 8),
                List.of(0, 3, 6),
                List.of(1, 4, 7),
                List.of(2, 5, 8),
                List.of(0, 4, 8),
                List.of(2, 4, 6)
        );

        for (List<Integer> winPossibility : winPossibilities) {
            if (field.get(winPossibility.get(0)) == field.get(winPossibility.get(1))
                && field.get(winPossibility.get(0)) == field.get(winPossibility.get(2))
                    && field.get(winPossibility.get(0)) != Sign.EMPTY) {

                    return field.get(winPossibility.get(0));
            }
        }
        return Sign.EMPTY;
    }

    public int getRandomEmptyFieldIndex() {
        List<Integer> list = field.entrySet().stream()
                .filter(e -> e.getValue() == Sign.EMPTY)
                .map(Map.Entry::getKey)
                .toList();

        int result = list.isEmpty() ? -1 : (int) (Math.random() * list.size());
        return result >= 0 ? list.get(result) : -1;
    }

    public int getSmartMoveIndex() {
        int crossWins = -1;
        List<List<Integer>> winPossibilities = List.of(
                List.of(0, 1, 2),
                List.of(3, 4, 5),
                List.of(6, 7, 8),
                List.of(0, 3, 6),
                List.of(1, 4, 7),
                List.of(2, 5, 8),
                List.of(0, 4, 8),
                List.of(2, 4, 6)
        );

        List<List<Integer>> winNoughtPossibilities = new ArrayList<>();
        List<List<Integer>> loseNoughtPossibilities = new ArrayList<>();

        //анализируем есть ли варианты победить
        for (List<Integer> winPossibility : winPossibilities) {
            int noughtCount = 0;
            int crossCount = 0;
            int emptyNot = -1;
            //считаем кресты и ноли для варианта
            for (int n : winPossibility) {
                if (field.get(n) == Sign.NOUGHT) {
                    noughtCount++;
                } else if (field.get(n) == Sign.CROSS) {
                    crossCount++;
                } else if (field.get(n) == Sign.EMPTY) {
                    emptyNot = n;
                }
            }
            if(noughtCount == 2 && emptyNot >= 0) {
                //нашли победный ход
                return emptyNot;
            } else if (crossCount == 2  && emptyNot >= 0){
                //нашли ход для предотвращения проигрыша
                crossWins = emptyNot;
            } else if (crossCount == 0 ){
                //сохраняем варианты где еще можно выйграть в список
                winNoughtPossibilities.add(winPossibility);
            } else if (noughtCount == 0 ){
                //сохраняем варианты где можно проиграть в список
                loseNoughtPossibilities.add(winPossibility);
            }

        }

        if(crossWins >= 0) return crossWins;

        int noughtWinPossibility = findMoveInMap(winNoughtPossibilities, field);
        if(noughtWinPossibility >= 0) return noughtWinPossibility;

        int noughtLosePossibility = findMoveInMap(loseNoughtPossibilities, field);
        if(noughtLosePossibility >= 0) return noughtLosePossibility;

        return getRandomEmptyFieldIndex();
    }

    public static int findMoveInMap (List<List<Integer>> possibilitiesList, Map<Integer, Sign> field) {
        Map<Integer, Integer> winMap = new HashMap<>();

        for (List<Integer> winPossibility : possibilitiesList) {
            for (int n : winPossibility) {
                if(field.get(n) == Sign.EMPTY) {
                    winMap.put(n,
                            winMap.containsKey(n)
                                    ? (winMap.get(n) + 1)
                                    : 1);
                }
            }

        }

        if (!winMap.isEmpty()) {
            int max = -1;
            int maxIndex = -1;
            for (Map.Entry<Integer, Integer> pair : winMap.entrySet()) {
                if(pair.getValue() > max) {
                    max = pair.getValue();
                    maxIndex = pair.getKey();
                }
            }

            return maxIndex;
        }

        return -1;
    }

}