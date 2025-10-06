package systemTransakcji;

import zlecenia.Typ;
import zlecenia.Zlecenie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class ArkuszZleceń {
    private ArrayList<Zlecenie> zleceniaKupna;
    private ArrayList<Zlecenie> zleceniaSprzedaży;
    private int nrZleceniaWTurze;


    public ArkuszZleceń() {
        zleceniaKupna = new ArrayList<Zlecenie>();
        zleceniaSprzedaży = new ArrayList<Zlecenie>();
        nrZleceniaWTurze = 0;
    }

    public void dodajZlecenie(Zlecenie nowe) {
        nowe.setNrWTurze(nrZleceniaWTurze);
        nrZleceniaWTurze++;
        if (nowe.getTypZlecenia() == Typ.kupno) {
            zleceniaKupna.add(nowe);
        }
        else {
            zleceniaSprzedaży.add(nowe);
        }
    }

    public void usuńZlecenie(Zlecenie zlecenie) {
        if (zlecenie.jestZleceniemKupna()) {
            zleceniaKupna.remove(zlecenie);
        }
        else zleceniaSprzedaży.remove(zlecenie);
    }

    public boolean możliweOperacje() {
        if (zleceniaSprzedaży.isEmpty() || zleceniaKupna.isEmpty()) {
            return false;
        }
        if (zleceniaKupna.get(0).getLimitCeny() >=
                zleceniaSprzedaży.get(0).getLimitCeny()) {
            return true;
        }
        return false;
    }

    public void usuńPrzestarzałeZlecenia(int aktualnaTura) {

        Iterator<Zlecenie> iteratorKupna = zleceniaKupna.iterator();
        while (iteratorKupna.hasNext()) {
            Zlecenie z = iteratorKupna.next();
            if (!z.czyAktualne(aktualnaTura)) {
                iteratorKupna.remove();
            }
        }

        Iterator<Zlecenie> iteratorSprzedazy = zleceniaSprzedaży.iterator();
        while (iteratorSprzedazy.hasNext()) {
            Zlecenie z = iteratorSprzedazy.next();
            if (!z.czyAktualne(aktualnaTura)) {
                iteratorSprzedazy.remove();
            }
        }
    }

    public void posortuj(Comparator comparator) {
        Collections.sort(zleceniaKupna, comparator);
        Collections.sort(zleceniaSprzedaży, comparator);
    }

    public void nowaTura() {
        nrZleceniaWTurze = 0;
    }

    public Zlecenie getZlecenieKupna() {
        return zleceniaKupna.get(0);
    }

    public Zlecenie getZlecenieSprzedaży() {
        return zleceniaSprzedaży.get(0);
    }

    public Zlecenie get(Typ typ, int i) {
        if (typ == Typ.sprzedaż) {
            if (zleceniaSprzedaży.size() <= i) return null;
            return zleceniaSprzedaży.get(i);
        }
        if (zleceniaKupna.size() <= i) return null;
        return zleceniaKupna.get(i);
    }
}
