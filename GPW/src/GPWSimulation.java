import inwestorzy.Inwestor;
import inwestorzy.InwestorRandom;
import inwestorzy.InwestorSMA;
import systemTransakcji.SystemTransakcyjny;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class GPWSimulation {

    private static ArrayList<Inwestor> inwestorzy;
    private static HashMap<String, Integer> cenyAkcji;
    private static HashMap<String, Integer> ilośćAkcji;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Nie podano pliku");
            System.exit(0);
        } else if (args.length == 1) {
            System.out.println("Nie podano liczby tur");
            System.exit(0);
        }

        sprawdźCzyDodatniąLiczbą(args[1]);
        int liczbaTur = Integer.parseInt(args[1]);

        wczytajDane(args[0]);

        SystemTransakcyjny giełda =
                new SystemTransakcyjny(cenyAkcji, ilośćAkcji, liczbaTur);

        for (int i = 0; i < liczbaTur; i++) {

            giełda.obecnaTura(i);
            Collections.shuffle(inwestorzy);

            for (Inwestor inwestor : inwestorzy) {
                giełda.odpytajInwestora(inwestor);
            }

            giełda.realizujZlecenia();
        }
        wypiszInwestorów();
    }

    private static void wczytajDane(String nazwaPliku) {
        try (Scanner scanner = new Scanner(new File(nazwaPliku))) {

            String ostatni = wczytajInwestorów(scanner);

            String ilośćGotówki = wczytajAkcje(ostatni, scanner);

            wczytajStanPoczątkowy(scanner, ilośćGotówki);

        } catch (FileNotFoundException e) {
            System.out.println("Nie ma pliku " + nazwaPliku);
            System.exit(0);
        }
    }

    private static String wczytajInwestorów(Scanner scanner) {
        inwestorzy = new ArrayList<Inwestor>();

        while(scanner.hasNext()) {

            String typ = scanner.next();
            if (typ.charAt(0) == '#') typ = "#";

            switch (typ) {
                case "#":
                    scanner.nextLine();
                    break;
                case "R":
                    inwestorzy.add(new InwestorRandom());
                    break;
                case "S":
                    inwestorzy.add(new InwestorSMA());
                    break;
                default:
                    return typ; // zwróci rodzaj akcji
            }
        }
        return null;
    }

    private static String wczytajAkcje(String pierwszaAkcja, Scanner scanner) {
        cenyAkcji = new HashMap<String, Integer>();

        assert pierwszaAkcja != null;
        String wczytywana = pierwszaAkcja;

        while (scanner.hasNext()) {

            if (wczytywana.charAt(0) == '#') {
                scanner.nextLine();
            }
            else if (wczytywana.charAt(0) >= 'A' &&
                    wczytywana.charAt(0) <= 'Z') {

                String[] pomocnicza = wczytywana.split(":");

                assert pomocnicza.length == 2 : "Zły format " +
                        "identyfikatora i ceny";

                String nowaAkcja = pomocnicza[0];
                assert nowaAkcja.matches("[A-Z]{1,5}") : "Błedny format" +
                        " identyfikatora akcji " + nowaAkcja;

                sprawdźCzyDodatniąLiczbą(pomocnicza[1]);
                int cena = Integer.parseInt(pomocnicza[1]);

                if (cenyAkcji.containsKey(nowaAkcja)) {
                    assert cena != cenyAkcji.get(nowaAkcja) : "Sprzeczne dane";
                }
                cenyAkcji.put(nowaAkcja, cena);
            }
            else {
                return wczytywana;
            }
            wczytywana = scanner.next();
        }
        return null;
    }

    private static void wczytajStanPoczątkowy(Scanner scanner,
                                              String gotówka) {
        ilośćAkcji = new HashMap<String, Integer>();

        sprawdźCzyDodatniąLiczbą(gotówka);

        for (Inwestor i : inwestorzy) {
            i.setGotówka(Integer.parseInt(gotówka));
        }

        while (scanner.hasNext()) {

            String wczytywana = scanner.next();

            if (wczytywana.charAt(0) == '#') {
                scanner.nextLine();
            }
            else {
                String[] pomocnicza = wczytywana.split(":");

                assert pomocnicza.length == 2 : "Zły format " +
                        "identyfikatora i ilości akcji";

                assert cenyAkcji.containsKey(pomocnicza[0]) : "Identyfikator" +
                        " nieznany " + pomocnicza[0];

                for (Inwestor i : inwestorzy) {
                    i.dodajDoPorfelu(pomocnicza[0],
                            Integer.parseInt(pomocnicza[1]));
                }
                ilośćAkcji.put(pomocnicza[0],
                        Integer.parseInt(pomocnicza[1]) * inwestorzy.size());
            }
        }
    }

    private static void sprawdźCzyDodatniąLiczbą(String liczba) {
        assert liczba.matches("-?\\d+") : "Błędny format "
                + liczba + " nie jest liczbą";

        int l = Integer.parseInt(liczba);
        assert l > 0 : l + " nie jest liczbą dodatnią";
    }

    private static void wypiszInwestorów() {
        for (Inwestor i : inwestorzy) {
            i.wypiszInwestora();
        }
    }
}