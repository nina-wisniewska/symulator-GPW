import inwestorzy.Inwestor;
import inwestorzy.InwestorRandom;
import inwestorzy.InwestorSMA;
import org.junit.jupiter.api.Test;
import pomocnicze.Losowanie;
import systemTransakcji.SystemTransakcyjny;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GPWtesty {


    // Testy całościowe

    @Test
    public void samiRandomowiRównyStart() {
        System.out.println("\nsamiRandomowiRównyStart");
        ArrayList<Inwestor> inwestorzy = new ArrayList<Inwestor>();

        int gotówka = 10000 * 10;
        int liczbaInwestorów = 10;

        for (int i = 0; i < liczbaInwestorów; i++) {
            inwestorzy.add(new InwestorRandom());
        }
        for (Inwestor i : inwestorzy) {
            i.setGotówka(gotówka / liczbaInwestorów);
        }

        HashMap<String, Integer> cenyAkcji = new HashMap<String, Integer>() {{
            put("AAA", 200);
            put("BBB", 300);
            put("CCC", 400);
            put("DDD", 1000);
        }};

        ArrayList<String> listaAkcji = new ArrayList<>(cenyAkcji.keySet());

        HashMap<String, Integer> ilośćAkcji = new HashMap<String, Integer>() {{
            put("AAA", 20);
            put("BBB", 30);
            put("CCC", 20);
            put("DDD", 10);
        }};

        // ilość akcji dla każdego inwestora

        for (Inwestor i : inwestorzy) {
            for (String akcja : listaAkcji) {
                i.dodajDoPorfelu(akcja,
                        ilośćAkcji.get(akcja) / liczbaInwestorów);
            }
        }

        int liczbaTur = 1000;
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

        HashMap<String, Integer> podsumowanieAkcji = new HashMap<String, Integer>() {{
            put("AAA", 0);
            put("BBB", 0);
            put("CCC", 0);
            put("DDD", 0);
        }};

        int sumaGotówki = 0;
        for (Inwestor i : inwestorzy) {
            i.wypiszInwestora();
            sumaGotówki += i.getGotówka();
            for (String akcja : listaAkcji) {
                int ile = podsumowanieAkcji.get(akcja);
                ile += i.getLiczbaAkcji(akcja);
                podsumowanieAkcji.put(akcja, ile);
            }
        }

        // Niezmienniki symulacji

        assertEquals(gotówka, sumaGotówki);
        for (String akcja : listaAkcji) {
            int oczekiwanaIlość = ilośćAkcji.get(akcja);
            int rzeczywistaIlość = podsumowanieAkcji.get(akcja);
            assertEquals(oczekiwanaIlość, rzeczywistaIlość);
        }
    }

    @Test
    public void Moodle() {
        System.out.println("\nMoodle");
        ArrayList<Inwestor> inwestorzy = new ArrayList<Inwestor>();

        int liczbaInwestorów = 6;
        int gotówka = 100000;

        for (int i = 0; i < liczbaInwestorów - 2; i++) {
            inwestorzy.add(new InwestorRandom());
        }
        for (int i = liczbaInwestorów - 2; i < liczbaInwestorów; i++) {
            inwestorzy.add(new InwestorSMA());
        }
        for (Inwestor i : inwestorzy) {
            i.setGotówka(gotówka);
        }

        HashMap<String, Integer> cenyAkcji = new HashMap<String, Integer>() {{
            put("APL", 145);
            put("MSFT", 300);
            put("GOOGL", 2700);
        }};

        ArrayList<String> listaAkcji = new ArrayList<>(cenyAkcji.keySet());

        HashMap<String, Integer> ilośćAkcji = new HashMap<String, Integer>() {{
            put("APL", 5 * liczbaInwestorów);
            put("MSFT", 15 * liczbaInwestorów);
            put("GOOGL", 3 * liczbaInwestorów);
        }};

        // ilość akcji dla każdego inwestora

        for (Inwestor i : inwestorzy) {
            for (String akcja : listaAkcji) {
                i.dodajDoPorfelu(akcja,
                        ilośćAkcji.get(akcja) / liczbaInwestorów);
            }
        }

        int liczbaTur = 10000;
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

        HashMap<String, Integer> podsumowanieAkcji = new HashMap<String, Integer>() {{
            put("APL", 0);
            put("MSFT", 0);
            put("GOOGL", 0);
        }};

        int sumaGotówki = 0;
        for (Inwestor i : inwestorzy) {
            i.wypiszInwestora();
            sumaGotówki += i.getGotówka();
            for (String akcja : listaAkcji) {
                int ile = podsumowanieAkcji.get(akcja);
                ile += i.getLiczbaAkcji(akcja);
                podsumowanieAkcji.put(akcja, ile);
            }
        }

        // Niezmienniki symulacji

        assertEquals(gotówka * liczbaInwestorów, sumaGotówki);
        for (String akcja : listaAkcji) {
            int oczekiwanaIlość = ilośćAkcji.get(akcja);
            int rzeczywistaIlość = podsumowanieAkcji.get(akcja);
            assertEquals(oczekiwanaIlość, rzeczywistaIlość);
        }
    }

    @Test
    public void MoodleOdwróceniInwestorzy() {
        System.out.println("\nMoodleOdwróceniInwestorzy");
        ArrayList<Inwestor> inwestorzy = new ArrayList<Inwestor>();

        int liczbaInwestorów = 6;
        int gotówka = 100000;

        for (int i = 0; i < liczbaInwestorów - 2; i++) {
            inwestorzy.add(new InwestorSMA());
        }
        for (int i = liczbaInwestorów - 2; i < liczbaInwestorów; i++) {
            inwestorzy.add(new InwestorRandom());
        }

        for (Inwestor i : inwestorzy) {
            i.setGotówka(gotówka);
        }

        HashMap<String, Integer> cenyAkcji = new HashMap<String, Integer>() {{
            put("APL", 145);
            put("MSFT", 300);
            put("GOOGL", 2700);
        }};

        ArrayList<String> listaAkcji = new ArrayList<>(cenyAkcji.keySet());

        HashMap<String, Integer> ilośćAkcji = new HashMap<String, Integer>() {{
            put("APL", 5 * liczbaInwestorów);
            put("MSFT", 15 * liczbaInwestorów);
            put("GOOGL", 3 * liczbaInwestorów);
        }};

        // ilość akcji dla każdego inwestora
        for (Inwestor i : inwestorzy) {
            for (String akcja : listaAkcji) {
                i.dodajDoPorfelu(akcja,
                        ilośćAkcji.get(akcja) / liczbaInwestorów);
            }
        }

        int liczbaTur = 10000;
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

        HashMap<String, Integer> podsumowanieAkcji = new HashMap<String, Integer>() {{
            put("APL", 0);
            put("MSFT", 0);
            put("GOOGL", 0);
        }};

        int sumaGotówki = 0;
        for (Inwestor i : inwestorzy) {
            i.wypiszInwestora();
            sumaGotówki += i.getGotówka();
            for (String akcja : listaAkcji) {
                int ile = podsumowanieAkcji.get(akcja);
                ile += i.getLiczbaAkcji(akcja);
                podsumowanieAkcji.put(akcja, ile);
            }
        }

        // Niezmienniki symulacji

        assertEquals(gotówka * liczbaInwestorów, sumaGotówki);
        for (String akcja : listaAkcji) {
            int oczekiwanaIlość = ilośćAkcji.get(akcja);
            int rzeczywistaIlość = podsumowanieAkcji.get(akcja);
            assertEquals(oczekiwanaIlość, rzeczywistaIlość);
        }
    }

    @Test
    public void MoodleWięcejRandomówIAkcji() {
        System.out.println("\nMoodleWięcejRandomówIAkcji");
        ArrayList<Inwestor> inwestorzy = new ArrayList<Inwestor>();

        int liczbaInwestorów = 12;
        int gotówka = 100000;

        for (int i = 0; i < liczbaInwestorów - 2; i++) {
            inwestorzy.add(new InwestorRandom());
        }
        for (int i = liczbaInwestorów - 2; i < liczbaInwestorów; i++) {
            inwestorzy.add(new InwestorSMA());
        }

        for (Inwestor i : inwestorzy) {
            i.setGotówka(gotówka);
        }

        HashMap<String, Integer> cenyAkcji = new HashMap<String, Integer>() {{
            put("APL", 145);
            put("MSFT", 300);
            put("GOOGL", 2700);
            put("AAA", 700);
            put("BBB", 2000);
        }};

        ArrayList<String> listaAkcji = new ArrayList<>(cenyAkcji.keySet());

        HashMap<String, Integer> ilośćAkcji = new HashMap<String, Integer>() {{
            put("APL", 5 * liczbaInwestorów);
            put("MSFT", 15 * liczbaInwestorów);
            put("GOOGL", 3 * liczbaInwestorów);
            put("AAA", 7 * liczbaInwestorów);
            put("BBB", 20 * liczbaInwestorów);
        }};

        // ilość akcji dla każdego inwestora
        for (Inwestor i : inwestorzy) {
            for (String akcja : listaAkcji) {
                i.dodajDoPorfelu(akcja,
                        ilośćAkcji.get(akcja) / liczbaInwestorów);
            }
        }

        int liczbaTur = 10000;
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

        HashMap<String, Integer> podsumowanieAkcji = new HashMap<String, Integer>() {{
            put("APL", 0);
            put("MSFT", 0);
            put("GOOGL", 0);
            put("AAA", 0);
            put("BBB", 0);
        }};

        int sumaGotówki = 0;
        for (Inwestor i : inwestorzy) {
            i.wypiszInwestora();
            sumaGotówki += i.getGotówka();
            for (String akcja : listaAkcji) {
                int ile = podsumowanieAkcji.get(akcja);
                ile += i.getLiczbaAkcji(akcja);
                podsumowanieAkcji.put(akcja, ile);
            }
        }

        // Niezmienniki symulacji

        assertEquals(gotówka * liczbaInwestorów, sumaGotówki);
        for (String akcja : listaAkcji) {
            int oczekiwanaIlość = ilośćAkcji.get(akcja);
            int rzeczywistaIlość = podsumowanieAkcji.get(akcja);
            assertEquals(oczekiwanaIlość, rzeczywistaIlość);
        }
    }

    @Test
    public void samiRandomowiRównyStartWięcejAkcjiITur() {
        System.out.println("\nsamiRandomowiRównyStartWięcejAkcjiITur");
        ArrayList<Inwestor> inwestorzy = new ArrayList<>();

        int liczbaInwestorów = 10;
        int gotówka = 10000 * liczbaInwestorów;

        for (int i = 0; i < liczbaInwestorów; i++) {
            inwestorzy.add(new InwestorRandom());
        }
        for (Inwestor i : inwestorzy) {
            i.setGotówka(gotówka / liczbaInwestorów);
        }

        ArrayList<String> listaAkcji = new ArrayList<>(List.of(
                "AAA", "BBB", "CCC", "DDD", "EEE",
                "FFF", "GGG", "HHH", "JJJ"));

        HashMap<String, Integer> cenyAkcji = new HashMap<String, Integer>();

        // Początkowa ilość akcji Losowane
        HashMap<String, Integer> ilośćAkcji = new HashMap<String, Integer>();

        HashMap<String, Integer> podsumowanieAkcji = new HashMap<String, Integer>();

        for (String akcje : listaAkcji) {
            cenyAkcji.put(akcje, Losowanie.losuj(50, 500));

            ilośćAkcji.put(akcje,
                    Losowanie.losuj(1, 20) * liczbaInwestorów);

            podsumowanieAkcji.put(akcje, 0);
        }

        for (Inwestor i : inwestorzy) {
            for (String akcja : listaAkcji) {
                i.dodajDoPorfelu(akcja,
                        ilośćAkcji.get(akcja) / liczbaInwestorów);
            }
        }

        int liczbaTur = 10000;
        SystemTransakcyjny giełda =
                new SystemTransakcyjny(cenyAkcji, ilośćAkcji, liczbaTur);

        // Wykonywanie kolejnych tur symulacji
        for (int i = 0; i < liczbaTur; i++) {
            giełda.obecnaTura(i);
            Collections.shuffle(inwestorzy);

            for (Inwestor inwestor : inwestorzy) {
                giełda.odpytajInwestora(inwestor);
            }

            giełda.realizujZlecenia();
        }

        // Sumowanie wyników symulacji

        int sumaGotówki = 0;
        for (Inwestor i : inwestorzy) {
            i.wypiszInwestora();
            sumaGotówki += i.getGotówka();
            for (String akcja : listaAkcji) {
                int ile = podsumowanieAkcji.get(akcja);
                ile += i.getLiczbaAkcji(akcja);
                podsumowanieAkcji.put(akcja, ile);
            }
        }

        assertEquals(gotówka, sumaGotówki);

        for (String akcja : listaAkcji) {
            int oczekiwanaIlość = ilośćAkcji.get(akcja);
            int rzeczywistaIlość = podsumowanieAkcji.get(akcja);
            assertEquals(oczekiwanaIlość, rzeczywistaIlość);
        }
    }


    // oczekiwany brak zmian i transakcji
    @Test
    public void samiSMARównyStartr() {
        System.out.println("\nsamiSMARównyStart");
        ArrayList<Inwestor> inwestorzy = new ArrayList<>();

        int liczbaInwestorów = 5;
        int gotówka = 10000 * liczbaInwestorów;

        for (int i = 0; i < liczbaInwestorów; i++) {
            inwestorzy.add(new InwestorSMA());
        }
        for (Inwestor i : inwestorzy) {
            i.setGotówka(gotówka / liczbaInwestorów);
        }

        ArrayList<String> listaAkcji = new ArrayList<>(List.of(
                "AAA", "BBB", "CCC", "DDD", "EEE",
                "FFF", "GGG", "HHH"));

        HashMap<String, Integer> cenyAkcji = new HashMap<String, Integer>();

        // Początkowa ilość akcji Losowane
        HashMap<String, Integer> ilośćAkcji = new HashMap<String, Integer>();

        HashMap<String, Integer> podsumowanieAkcji = new HashMap<String, Integer>();

        for (String akcje : listaAkcji) {
            cenyAkcji.put(akcje, Losowanie.losuj(50, 500));

            ilośćAkcji.put(akcje,
                    Losowanie.losuj(1, 20) * liczbaInwestorów);

            podsumowanieAkcji.put(akcje, 0);
        }

        for (Inwestor i : inwestorzy) {
            for (String akcja : listaAkcji) {
                i.dodajDoPorfelu(akcja,
                        ilośćAkcji.get(akcja) / liczbaInwestorów);
            }
        }

        int liczbaTur = 10000;
        SystemTransakcyjny giełda =
                new SystemTransakcyjny(cenyAkcji, ilośćAkcji, liczbaTur);

        // Wykonywanie kolejnych tur symulacji
        for (int i = 0; i < liczbaTur; i++) {
            giełda.obecnaTura(i);
            Collections.shuffle(inwestorzy);

            for (Inwestor inwestor : inwestorzy) {
                giełda.odpytajInwestora(inwestor);
            }

            giełda.realizujZlecenia();
        }

        // Sumowanie wyników symulacji

        int sumaGotówki = 0;
        for (Inwestor i : inwestorzy) {
            i.wypiszInwestora();
            sumaGotówki += i.getGotówka();
            for (String akcja : listaAkcji) {
                int ile = podsumowanieAkcji.get(akcja);
                ile += i.getLiczbaAkcji(akcja);
                podsumowanieAkcji.put(akcja, ile);
            }
        }

        assertEquals(gotówka, sumaGotówki);

        for (String akcja : listaAkcji) {
            int oczekiwanaIlość = ilośćAkcji.get(akcja);
            int rzeczywistaIlość = podsumowanieAkcji.get(akcja);
            assertEquals(oczekiwanaIlość, rzeczywistaIlość);
        }
    }


    @Test
    public void samiRandomowiNieRównyStart() {
        System.out.println("\nsamiRandomowiNieRównyStart");
        ArrayList<Inwestor> inwestorzy = new ArrayList<>();

        int liczbaInwestorów = 10;
        int gotówka = 10000 * liczbaInwestorów;

        for (int i = 0; i < liczbaInwestorów; i++) {
            inwestorzy.add(new InwestorRandom());
        }

        for (int i = 0; i < liczbaInwestorów / 2; i++) {
            inwestorzy.get(i).setGotówka(gotówka / liczbaInwestorów / 2);
        }
        for (int i = liczbaInwestorów / 2; i < liczbaInwestorów; i++) {
            inwestorzy.get(i).setGotówka(gotówka / liczbaInwestorów * 2);
        }

        ArrayList<String> listaAkcji = new ArrayList<>(List.of(
                "AAA", "BBB", "CCC", "DDD", "EEE",
                "FFF", "GGG", "HHH", "JJJ"));

        HashMap<String, Integer> cenyAkcji = new HashMap<String, Integer>();

        // Początkowa ilość akcji Losowane
        HashMap<String, Integer> ilośćAkcji = new HashMap<String, Integer>();

        HashMap<String, Integer> podsumowanieAkcji = new HashMap<String, Integer>();

        for (String akcje : listaAkcji) {
            cenyAkcji.put(akcje, Losowanie.losuj(50, 500));

            ilośćAkcji.put(akcje,
                    Losowanie.losuj(1, 20) * liczbaInwestorów);

            podsumowanieAkcji.put(akcje, 0);
        }

        for (Inwestor i : inwestorzy) {
            for (String akcja : listaAkcji) {
                i.dodajDoPorfelu(akcja,
                        ilośćAkcji.get(akcja) / liczbaInwestorów);
            }
        }

        // chcemy zobaczyć jak wygląda strukturaportfeli
        System.out.println("Stan początkowy: ");
        gotówka = 0;
        for (Inwestor i : inwestorzy) {
            i.wypiszInwestora();
            gotówka += i.getGotówka();
        }

        int liczbaTur = 10000;
        SystemTransakcyjny giełda =
                new SystemTransakcyjny(cenyAkcji, ilośćAkcji, liczbaTur);

        // Wykonywanie kolejnych tur symulacji
        for (int i = 0; i < liczbaTur; i++) {
            giełda.obecnaTura(i);
            Collections.shuffle(inwestorzy);

            for (Inwestor inwestor : inwestorzy) {
                giełda.odpytajInwestora(inwestor);
            }

            giełda.realizujZlecenia();
        }

        System.out.println("Stan portfeli inwestorów po symulacji:");
        // Sumowanie wyników symulacji

        int sumaGotówki = 0;
        for (Inwestor i : inwestorzy) {
            i.wypiszInwestora();
            sumaGotówki += i.getGotówka();
            for (String akcja : listaAkcji) {
                int ile = podsumowanieAkcji.get(akcja);
                ile += i.getLiczbaAkcji(akcja);
                podsumowanieAkcji.put(akcja, ile);
            }
        }

        assertEquals(gotówka, sumaGotówki);

        for (String akcja : listaAkcji) {
            int oczekiwanaIlość = ilośćAkcji.get(akcja);
            int rzeczywistaIlość = podsumowanieAkcji.get(akcja);
            assertEquals(oczekiwanaIlość, rzeczywistaIlość);
        }
    }

}

