package com.algospot.alchemy;

import java.io.*;
import java.util.*;

public class Debug {

    static List<Integer> bitsToList(int mask, int N) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if ((mask & (1 << i)) != 0) {
                result.add(i + 1);
            }
        }
        return result;
    }

    public static String toBitString(int value) {
        return String.format("%32s", Integer.toBinaryString(value))
                .replace(' ', '0');
    }

    public static void main(String[] args) {
        // #1
        String input = new StringBuilder().append("3\n")
                .append("\n")
                .append("3\n").append("0\n").append("1 1\n").append("1 1\n")
                .append("\n")
                .append("6\n").append("0\n").append("0\n").append("0\n").append("0\n").append("2 1 2\n").append("4 1 2 3 4\n")
                .append("\n")
                .append("5\n").append("0\n").append("0\n").append("1 5\n").append("2 1 3\n").append("2 2 3")
                .toString();

        // #2
//        String input = new StringBuilder().append("1\n")
//                .append("\n")
//                .append("4\n")
//                .append("0\n")
//                .append("0\n")
//                .append("1 1\n")
//                .append("1 2")
//                .toString()
//                ;

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(in);

        StringBuilder out = new StringBuilder();

        int T = Integer.parseInt(sc.nextLine());
        for (int testCaseIndex = 0; testCaseIndex < T; testCaseIndex++) {
            sc.nextLine();      // read dummy line

            int numberOfMaterials = Integer.parseInt(sc.nextLine());

            int[] numberOfMaterial = new int[numberOfMaterials + 1];
            int[] masksOfRecipe = new int[numberOfMaterials + 1];

            for (int i = 1; i <= numberOfMaterials; i++) {
                String[] ingredients = sc.nextLine().split(" ");

                int numberOfIngredients = ingredients.length - 1;
                numberOfMaterial[i] = numberOfIngredients;

                int mask = 0;
                for (int j = 1; j <= numberOfIngredients; j++) {
                    int ingredient = Integer.parseInt(ingredients[j]);
                    mask |= (1 << (ingredient - 1));
                }
                masksOfRecipe[i] = mask;
            }

            System.out.println("numberOfMaterial : " + Arrays.toString(numberOfMaterial));
            System.out.println("masksOfRecipe : " + Arrays.toString(masksOfRecipe));
            for (int i = 1; i <= numberOfMaterials; i++) {
                System.out.println(i + " : " + toBitString(masksOfRecipe[i]));
            }

//            if (true) {
//                continue;
//            }

            // Map recipe mask -> max index having that mask
            // 같은 레시피를 가지는 mask 중 가장 마지막의 인덱스를 저장하는 map변수
            HashMap<Integer, Integer> maxIdxByMask = new HashMap<>();
            for (int i = 1; i <= numberOfMaterials; i++) {
                int mask = masksOfRecipe[i];
                Integer prev = maxIdxByMask.get(mask);
                if (prev == null || i > prev) {
                    maxIdxByMask.put(mask, i);
                }
            }

            System.out.println("maxIdxByMask : " + maxIdxByMask);

            if (testCaseIndex > 0) {
                out.append('\n'); // blank line between test cases
            }

            for (int i = 1; i <= numberOfMaterials; i++) {
                if (numberOfMaterial[i] == 0) { // non-synthesizable
                    out.append("0\n");
                    continue;
                }

                int mask = masksOfRecipe[i];

                // If there exists W > t with the same recipe, T loses the tie at the end.
                // 같은 레시피를 가진 성분이 마지막 인덱스에 도달하기전, 먼저 출현하면 "IMPOSSIBLE"
                if (maxIdxByMask.get(mask) > i) {
                    out.append("IMPOSSIBLE\n");
                    continue;
                }

                // Intersection over all synthesizable U with S(U) ⊆ S(T)
                // 모든 합성가능한 성분에 대해서 교집합이 존재하는지 확인
                // 만약, 합성가능한 성분의 독립적으로 생성가능하다면 intersection은 mask 와 같게 됨
                int intersection = mask;
                for (int j = 1; j <= numberOfMaterials; j++) {
                    if (j == i) {
                        continue;
                    }

                    int tempMask = masksOfRecipe[j];
                    if (tempMask != 0 && (tempMask & mask) == tempMask) { // subset and synthesizable
                        intersection &= tempMask;
                    }
                }

                System.out.println("intersection : " + intersection);

                // no single last ingredient blocks all subset recipes
                // 독립적으로 생성가능하지 않으면서, 교집합이 존재하지 않으면 "IMPOSSIBLE"
                if (intersection == 0) {
                    out.append("IMPOSSIBLE\n");
                    continue;
                }

                // Pick any ingredient from intersection as the last one
                // 현재 물질에서 필요로 하는 합성물질중 모든 물질의 교집합(intersection)과 또 한번의 교집합의 물질의 min index
                int minIndexMaterial = -1;
                for (int j = 0; j < numberOfMaterials; j++) {
                    if ((intersection & (1 << j)) != 0) {
                        minIndexMaterial = j + 1;
                        break;
                    }
                }

                System.out.println("minIndexMaterial : " + minIndexMaterial);

                // Order: all ingredients except last, then last
                List<Integer> all = bitsToList(mask, numberOfMaterials);
                System.out.println("all : " + all);

                // 합성원소 개수 출력
                StringBuilder line = new StringBuilder();
                line.append(numberOfMaterial[i]);

                // 교집합 재료중 최초성분 제외하고 나머지 성분을 추가
                for (int indexOfIngredient : all) {
                    if (indexOfIngredient != minIndexMaterial) {
                        line.append(' ').append(indexOfIngredient);
                    }
                }

                // 교집합 재료중 최초성분를 마지막에 추가
                line.append(' ').append(minIndexMaterial);
                out.append(line).append('\n');
            }
        }

        System.out.print(out);
    }
}
