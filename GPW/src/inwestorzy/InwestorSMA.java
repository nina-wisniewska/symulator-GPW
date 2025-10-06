package inwestorzy;

import pomocnicze.Losowanie;
import pomocnicze.WskaźnikSMA;
import systemTransakcji.SystemTransakcyjny;
import zlecenia.FabrykaZleceń;
import zlecenia.Typ;
import zlecenia.Zlecenie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class InwestorSMA extends Inwestor {
    ArrayList<String> akcjeZSygnałem;

    HashMap<String, WskaźnikSMA> SMA;

    public InwestorSMA() {
        akcjeZSygnałem = new ArrayList<String>();
        SMA = new HashMap<String, WskaźnikSMA>();
    }

    @Override
    public void odpytaj(SystemTransakcyjny giełda) {
        for (int i = 0; i < giełda.getIleRóżnychAkcji(); i++) {
            String akcja = giełda.getAkcja(i);
            if (!SMA.containsKey(akcja)) {
                SMA.put(akcja, new WskaźnikSMA());
            }
            SMA.get(akcja).dodaj(giełda.getCenaAkcji(akcja));
        }
    }

    @Override
    public Zlecenie złóżZlecenie(int tura, int ostatnia) {

        sprawdźWskaźnikiSMA();
        String akcja = null;
        if (!akcjeZSygnałem.isEmpty()) {
            Collections.shuffle(akcjeZSygnałem);
            akcja = akcjeZSygnałem.get(0);
        }
        if (akcja == null) return null;

        Typ typ = Typ.kupno;
        if (SMA.get(akcja).sygnałSprzedaży()) typ = Typ.sprzedaż;

        int cenaAkcji = SMA.get(akcja).getCena();
        int ileAkcji = ileAkcji(typ, akcja, cenaAkcji);
        int limitCeny = Losowanie.losuj
                (cenaAkcji - 10, cenaAkcji + 10);

        if (limitCeny < 1) limitCeny = 1;
        if (ileAkcji == 0) return null;

        FabrykaZleceń fabryka =  new FabrykaZleceń();
        Zlecenie nowe = null;
        try {
            nowe = fabryka.stwórzLosoweZlecenie();
            nowe.setIdentyfikatorAkcji(akcja);
            nowe.setTypZlecenia(typ);
            nowe.setIlość(ileAkcji);
            nowe.setLimitCeny(limitCeny);
            nowe.setTuraZłożenia(tura, ostatnia);
            nowe.setInwestor(this);

        } catch (Exception e) {
            return null;
        }
        return nowe;
    }

    private void sprawdźWskaźnikiSMA() {
        for (String akcja : SMA.keySet()){
            if (SMA.get(akcja).sygnałKupna()) akcjeZSygnałem.add(akcja);
            else if (SMA.get(akcja).sygnałSprzedaży())
                akcjeZSygnałem.add(akcja);
        }
    }

    @Override
    public void wypiszInwestora() {
        System.out.println("Inwestor SMA    " + stanPortfela());
    }
}
