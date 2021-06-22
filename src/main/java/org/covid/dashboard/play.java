package org.covid.dashboard;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class play {
    public static void main(String[] args) {
        String encoded = "6015ekx";

        int indexOfFirstLetter = -1;

        for (int i = 0; i < encoded.length(); i++) {
            if (Character.isLetter(encoded.charAt(i))){
                indexOfFirstLetter = i;
                break;
            }
        }

        int x = Integer.parseInt(encoded.substring(0, indexOfFirstLetter));
        String str = encoded.substring(indexOfFirstLetter);

        String key = "abcdefghijklmnopqrstuvwxyz";

/*        for (String s:str.split("")) {
            int correspondinIndex = key.indexOf(s);

            int calculatedChar = ((6015/26)%correspondinIndex) * correspondinIndex;
            System.out.println(calculatedChar);

            //System.out.println(Character.toString(key.charAt(calculatedChar)));
        }*/
/*        String result = Stream.of(str.split(""))
                .map(m1 -> key.indexOf(m1))
                .mapToInt(m2 -> ((6015/26)%m2) * m2)
                .mapToObj(m3 -> Character.toString(key.charAt(m3)))
                .collect(Collectors.joining(""));*/

        /*System.out.println(result);*/

       /* System.out.println(((6015/26)%4) *4);*/
        /*System.out.println(((6015/26)%10) *10);*/


        System.out.println(((6015%26)*23) % 23);

    }
}
