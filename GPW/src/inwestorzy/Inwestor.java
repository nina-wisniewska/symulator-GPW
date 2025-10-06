package inwestorzy;



import pomocnicze.Losowanie;
import systemTransakcji.SystemTransakcyjny;
import zlecenia.Typ;
import zlecenia.Zlecenie;

import java.util.HashMap;
import java.util.Map;

public abstract class Inwestor {

    protected int gotówka;
    protected HashMap<String, Integer> ilośćAkcji; // posiadanych przez inwestora

    public Inwestor() {
        ilośćAkcji = new HashMap<String, Integer>();
    }

    public abstract void odpytaj(SystemTransakcyjny giełda);

    public abstract Zlecenie złóżZlecenie(int tura, int Ostatnia);

    public abstract void wypiszInwestora();

    public void kup(String akcja, int ilość, int cena) {
        int koszt = ilość * cena;
        gotówka -= koszt;
        int nowaIlość = ilośćAkcji.get(akcja) + ilość;
        ilośćAkcji.put(akcja, nowaIlość);
    }

    public void sprzedaj(String akcja, int ilość, int cena) {
        int zysk = ilość * cena;
        gotówka += zysk;
        int nowaIlość = ilośćAkcji.get(akcja) - ilość;
        ilośćAkcji.put(akcja, nowaIlość);
    }

    public boolean czyPosiada(String akcja, int ilePotrzebnych) {
        int ilePosiada = ilośćAkcji.get(akcja);
        if (ilePosiada >= ilePotrzebnych) return true;
        return false;
    }

    public boolean czyPosiadaGotówkę(int kwota) {
        if (kwota <= gotówka) return true;
        return false;
    }

    public void dodajDoPorfelu(String akcja, int liczbaAkcji) {
        if (ilośćAkcji.containsKey(akcja)) {
            int dotyczasowaIlość = ilośćAkcji.get(akcja);
            dotyczasowaIlość += liczbaAkcji;
            ilośćAkcji.put(akcja, dotyczasowaIlość);
        }
        else ilośćAkcji.put(akcja, liczbaAkcji);
    }

    public void setGotówka(int gotówka) {
        this.gotówka = gotówka;
    }

    protected String stanPortfela() {
        StringBuilder stan = new StringBuilder();
        stan.append(gotówka).append(" ");

        for (Map.Entry<String, Integer> entry : ilośćAkcji.entrySet()) {
            stan.append(entry.getKey()).append(":").append(entry.getValue()).append(" ");
        }

        return stan.toString().trim();
    }

    protected Typ losujTypZlecenia() {

        Typ[] typy = Typ.values();
        int losowyIndeks = Losowanie.losuj(0, typy.length - 1);
        return typy[losowyIndeks];
    }

    protected int ileAkcji(Typ typ, String akcja, int cena) {
        int max = 0;
        if (typ == Typ.kupno) {
            max = gotówka / cena;
        }
        else max = ilośćAkcji.get(akcja);
        return Losowanie.losuj(0, max);
    }

    public int getGotówka() {
        return gotówka;
    }

    public int getLiczbaAkcji(String akcja) {
        if (!ilośćAkcji.containsKey(akcja)) return 0;
        return ilośćAkcji.get(akcja);
    }
}
