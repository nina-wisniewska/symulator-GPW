package systemTransakcji;

import inwestorzy.Inwestor;
import pomocnicze.ZleceniaComparator;
import zlecenia.Typ;
import zlecenia.WykonajLubAnuluj;
import zlecenia.Zlecenie;

import java.util.*;

public class SystemTransakcyjny {

    private int tura;
    private final int ostatniaTura;
    private HashMap<String, Integer> cenyAkcji;
    private HashMap<String, Integer> ilośćAkcji;
    private ArrayList<String> akcje;
    private HashMap<String, ArkuszZleceń> zlecenia;
    private Comparator comparator;


    public SystemTransakcyjny(HashMap<String, Integer> cenyAkcji,
                              HashMap<String, Integer> ilośćAkcji,
                              int ostatniaTura) {
        this.cenyAkcji = cenyAkcji;
        this.ilośćAkcji = ilośćAkcji;
        this.ostatniaTura = ostatniaTura;

        Set<String> identyfikatory = ilośćAkcji.keySet();
        akcje = new ArrayList<>(identyfikatory);
        zlecenia = new HashMap<String, ArkuszZleceń>();
        for (String s : akcje) {
            zlecenia.put(s, new ArkuszZleceń());
        }
        comparator = new ZleceniaComparator();
    }

    public void obecnaTura(int tura) {
        this.tura = tura;
        for (String s : akcje) {
            zlecenia.get(s).nowaTura();
        }
    }

    public void odpytajInwestora(Inwestor inwestor) {

        inwestor.odpytaj(this);

        Zlecenie nowe = inwestor.złóżZlecenie(tura, ostatniaTura);
        if (nowe != null) {
            String akcja = nowe.getIdentyfikatorAkcji();
            zlecenia.get(akcja).dodajZlecenie(nowe);
        }

    }

    public void realizujZlecenia() {

        for (String s : akcje) {
            zlecenia.get(s).posortuj(comparator);
        }
        przeprowadźTranzakcje();
    }

    private void przeprowadźTranzakcje() {
        for (String s : akcje) {
            while (zlecenia.get(s).możliweOperacje()) {
                Zlecenie kupno = zlecenia.get(s).getZlecenieKupna();
                Zlecenie sprzedaż = zlecenia.get(s).getZlecenieSprzedaży();
                if (czyMożliwaSprzedaż(sprzedaż)) {

                    int cena = obliczCenę(kupno, sprzedaż);

                    if (czyMożliweKupno(kupno, cena)){

                        realizujTranzakcję(kupno, sprzedaż, cena);

                        if (kupno.czyZrealizowane())
                            zlecenia.get(s).usuńZlecenie(kupno);

                        if (sprzedaż.czyZrealizowane())
                            zlecenia.get(s).usuńZlecenie(sprzedaż);

                        // aktualizacja ceny po tranzakcji
                        cenyAkcji.put(s, cena);
                    }
                    else zlecenia.get(s).usuńZlecenie(kupno);
                }
                else zlecenia.get(s).usuńZlecenie(sprzedaż);
            }
            zlecenia.get(s).usuńPrzestarzałeZlecenia(tura);
        }
    }


    private void realizujTranzakcję(Zlecenie kupno, Zlecenie sprzedaż, int cena) {
        int ilość = sprzedaż.getIlość();
        if (kupno.getIlość() < ilość) ilość = kupno.getIlość();

        kupno.aktualizuj(ilość);
        sprzedaż.aktualizuj(ilość);

        Inwestor kupujący = kupno.getInwestor();
        Inwestor sprzedający = sprzedaż.getInwestor();
        kupujący.kup(kupno.getIdentyfikatorAkcji(), ilość, cena);
        sprzedający.sprzedaj(sprzedaż.getIdentyfikatorAkcji(), ilość, cena);
    }



    private int obliczCenę(Zlecenie kupno, Zlecenie sprzedaż) {
        if (kupno.getTura() == sprzedaż.getTura()) {
            if (kupno.getNrWTurze() < sprzedaż.getNrWTurze()) {
                return kupno.getLimitCeny();
            }
            return sprzedaż.getLimitCeny();
        }
        if (kupno.getTura() < sprzedaż.getTura()) return kupno.getLimitCeny();
        return sprzedaż.getLimitCeny();
    }

    private boolean czyMożliwaSprzedaż(Zlecenie sprzedaż) {
        Inwestor inwestor = sprzedaż.getInwestor();
        String akcja = sprzedaż.getIdentyfikatorAkcji();
        int ile = sprzedaż.getIlość();
        if (inwestor.czyPosiada(akcja, ile)) {
            if (czyZlecenieWA(sprzedaż)) {
                return sprawdźWarunkiWA(sprzedaż, 0, 0);
            }
        }
        return inwestor.czyPosiada(akcja, ile);
    }

    private boolean czyMożliweKupno(Zlecenie kupno, int cena) {
        Inwestor inwestor = kupno.getInwestor();
        int ilość = kupno.getIlość();
        if (inwestor.czyPosiadaGotówkę(ilość * cena)) {
            if (czyZlecenieWA(kupno)) {
                return sprawdźWarunkiWA(kupno, 0, 0);
            }
        }
        return inwestor.czyPosiadaGotówkę(ilość * cena);
    }

    // obsługa zleceń Wykonaj lub Anuluj - WA

    // najpierw weryfikujemy czy możliwe jest spełnienie całej transakcji kupna
    // czy sprzedaży po stronie samego inwestora, ale potem też patrzymy
    // na koleje zlecenia w arkuszu zleceń danej akcji aby zobaczyć czy możliwe
    // jest spełnienie całej tranzakcji

    // za każdym razem gdy patrzymy na transakcje kompatybilne z naszą,
    // weryfikujemy czy one mogą być spełnione oraz czy są one
    // zleceniami WA - jeśli tak to sprawdzamy ich wykonalność

    private boolean czyZlecenieWA (Zlecenie zlecenie) {
        if (zlecenie.getClass() == WykonajLubAnuluj.class ) return true;
        return false;
    }

    // który wskazuje nam jak głęboko doszliśmy w odpytywniu i parowaniu
    // naszego zlecenia ze zleceniami z arkusza

    private boolean sprawdźWarunkiWA(Zlecenie zlecenie, int któryK, int któryS) {
        if (zlecenie.jestZleceniemKupna())
            return WAkupno(zlecenie, któryK, któryS);
        return WAsprzedaż(zlecenie, któryK, któryS);
    }

    private boolean WAkupno(Zlecenie kupna, int któryK, int któryS) {
        int który = któryS; // bo dotyczy arkusza S Sprzedaży
        ArkuszZleceń sprzedaże = zlecenia.get(kupna.getIdentyfikatorAkcji());
        int ilość = kupna.getIlość();
        int limitCeny = kupna.getLimitCeny();
        Zlecenie sprzedaż = sprzedaże.get(Typ.sprzedaż, który);
        while (ilość > 0) {
            if (sprzedaż == null) return false;
            if (sprzedaż.getLimitCeny() < limitCeny) return false;

            // zwiększamy któryK aby nie patrzeć na obecne zlecenie kupna
            if (czyZlecenieWA(sprzedaż)){
                if (!sprawdźWarunkiWA(sprzedaż, któryK + 1, który)) return false;
            }
            int cena = obliczCenę(kupna, sprzedaż);
            if (!czyJeszczeKupi(kupna, cena, ilość)) return false;
            if(!czyMożeSprzedaćPom(sprzedaż)) return false;
            ilość -= sprzedaż.getIlość();
            który++;
            sprzedaż = sprzedaże.get(Typ.sprzedaż, który);
        }
        return true;
    }

    private boolean WAsprzedaż(Zlecenie sprzedaż, int któryK, int któryS) {
        int który = któryK;
        ArkuszZleceń kupna = zlecenia.get(sprzedaż.getIdentyfikatorAkcji());
        int ilość = sprzedaż.getIlość();
        int limitCeny = sprzedaż.getLimitCeny();
        Zlecenie kupno = kupna.get(Typ.kupno, który);
        while (ilość > 0) {
            if (kupno == null) return false;
            if (kupno.getLimitCeny() > limitCeny) return false;
            if (czyZlecenieWA(kupno))
                if(!sprawdźWarunkiWA(kupno, który, któryS + 1)) return false;

            int cena = obliczCenę(kupno, sprzedaż);
            if (!czyMożeKupićPom(kupno, cena)) return false;
            ilość -= kupno.getIlość();
            który++;
            kupno = kupna.get(Typ.kupno, który);
        }
        return true;
    }

    private boolean czyMożeSprzedaćPom(Zlecenie sprzedaż) {
        Inwestor inwestor = sprzedaż.getInwestor();
        String akcja = sprzedaż.getIdentyfikatorAkcji();
        int ile = sprzedaż.getIlość();
        return inwestor.czyPosiada(akcja, ile);
    }

    private boolean czyMożeKupićPom(Zlecenie kupno, int cena) {
        Inwestor inwestor = kupno.getInwestor();
        int ilość = kupno.getIlość();
        return inwestor.czyPosiadaGotówkę(ilość * cena);
    }

    private boolean czyJeszczeKupi(Zlecenie kupno, int cena, int ilość) {
        Inwestor inwestor = kupno.getInwestor();
        return inwestor.czyPosiadaGotówkę(ilość * cena);
    }

    public int getIleRóżnychAkcji() {
        return akcje.size();
    }

    public String getAkcja(int i) {
        return akcje.get(i);
    }

    public int getCenaAkcji(String akcja) {
        return cenyAkcji.get(akcja);
    }

}
