package pomocnicze;

import java.util.ArrayList;

public class WskaźnikSMA {
    private ArrayList<Integer> ceny;
    private int średnia5;
    private int średnia10;
    private int poprzedniaŚrednia5;
    private int poprzedniaŚrednia10;
    private final int NDUŻE = 10;
    private final int NMAŁE = 5;

    public WskaźnikSMA() {
        ceny = new ArrayList<Integer>();
    }

    public void dodaj(int cena) {
        if (ceny.size() < NDUŻE) {
            ceny.add(cena);
        }
        else {
            ceny.remove(0);
            ceny.add(cena);
        }
        ustawŚrednie();
    }

    private void ustawŚrednie() {

        if (ceny.size() != NDUŻE) return;
        poprzedniaŚrednia5 = średnia5;
        poprzedniaŚrednia10 = średnia10;

        średnia5 = 0;
        średnia10 = 0;

        for (int cena : ceny) {
            średnia10 += cena;
        }
        średnia10 /= NDUŻE;

        for (int i = NMAŁE; i < NDUŻE; i++) {
            średnia5 += ceny.get(i);
        }
        średnia5 /= NMAŁE;
    }

    // sygnał kupna w chwili przecięcia od dołu dłuższej średniej
    // - SMA 10 przez krótszą - SMA 5
    public boolean sygnałKupna() {
        if (ceny.size() != NDUŻE) return false;
        if (poprzedniaŚrednia10 > poprzedniaŚrednia5) {
            if (średnia5 >= średnia10) return true;
        }
        return false;
    }

    // sygnał sprzedaży, gdy SMA 5 przebija od góry SMA 10
    public boolean sygnałSprzedaży() {
        if (ceny.size() != NDUŻE) return false;
        if (poprzedniaŚrednia5 > poprzedniaŚrednia10) {
            if (średnia10 >= średnia5) return true;
        }
        return false;
    }

    public int getCena() {
        return ceny.get(ceny.size() - 1);
    }
}
