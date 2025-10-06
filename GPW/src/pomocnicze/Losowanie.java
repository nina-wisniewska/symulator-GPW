package pomocnicze;

import java.util.Random;

public class Losowanie {
    private static Random rand = new Random();

    public static int losuj(int dolna, int gorna) {
        int pomocniczy = gorna - dolna + 1;
        int wynik = rand.nextInt(pomocniczy);
        return wynik + dolna;
    }
}
