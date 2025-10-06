package inwestorzy;

import pomocnicze.Losowanie;
import systemTransakcji.SystemTransakcyjny;
import zlecenia.*;


public class InwestorRandom extends Inwestor {

    private String akcjaWylosowana;
    private int cenaWylosowanej; // cena akcji o którą zapyta

    public InwestorRandom() {
    }


    // inwestor Random losuje o jaką akcję zapyta. Następnie losuje
    // czy będzie chciał kupić czy sprzedać akcje tej spółki oraz
    // z jakim limitem stworzy zlecenie

    @Override
    public void odpytaj(SystemTransakcyjny giełda) {

        akcjaWylosowana = wylosujAkcję(giełda);
        cenaWylosowanej = giełda.getCenaAkcji(akcjaWylosowana);

    }

    @Override
    public Zlecenie złóżZlecenie(int tura, int ostatnia) {
        Typ typ = losujTypZlecenia();
        int ileAkcji = ileAkcji(typ, akcjaWylosowana, cenaWylosowanej);
        int limitCeny = Losowanie.losuj
                (cenaWylosowanej - 10, cenaWylosowanej + 10);
        if (limitCeny < 1) limitCeny = 1;
        if (ileAkcji == 0) return null;
        FabrykaZleceń fabryka =  new FabrykaZleceń();
        Zlecenie nowe = null;
        try {
            nowe = fabryka.stwórzLosoweZlecenie();
            nowe.setIdentyfikatorAkcji(akcjaWylosowana);
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

    @Override
    public void wypiszInwestora() {
        System.out.println("Inwestor Random " + stanPortfela());
    }


    private String wylosujAkcję(SystemTransakcyjny giełda) {
        int górnyZakres = giełda.getIleRóżnychAkcji();
        int i = Losowanie.losuj(0, górnyZakres - 1);
        return giełda.getAkcja(i);
    }




}
