package zlecenia;

import inwestorzy.Inwestor;

public abstract class Zlecenie {
    protected Typ typZlecenia;
    protected String identyfikatorAkcji;
    protected int ilość;
    protected int limitCeny;
    private Inwestor inwestor;
    protected int tura;
    private int nrWTurze;


    public Zlecenie() {
    }

    public abstract boolean czyAktualne(int tura);

    public boolean czyZrealizowane() {
        if (ilość == 0) return true;
        return false;
    }

    public void aktualizuj(int ilośćWTranzakcji) {
        ilość -= ilośćWTranzakcji;
    }

    public void setInwestor(Inwestor inwestor) {
        this.inwestor = inwestor;
    }

    public void setLimitCeny(int limitCeny) {
        this.limitCeny = limitCeny;
    }

    public void setIlość(int ilość) {
        this.ilość = ilość;
    }

    public void setIdentyfikatorAkcji(String identyfikatorAkcji) {
        this.identyfikatorAkcji = identyfikatorAkcji;
    }

    public void setTypZlecenia(Typ typZlecenia) {
        this.typZlecenia = typZlecenia;
    }

    public abstract void setTuraZłożenia(int turaZłożenia, int ostatnia);

    public Typ getTypZlecenia() {
        return typZlecenia;
    }

    public String getIdentyfikatorAkcji() {
        return identyfikatorAkcji;
    }

    public int getIlość() {
        return ilość;
    }

    public int getLimitCeny() {
        return limitCeny;
    }

    public Inwestor getInwestor() {
        return inwestor;
    }

    public void setNrWTurze(int nrWTurze) {
        this.nrWTurze = nrWTurze;
    }

    public int getTura() {
        return tura;
    }

    public int getNrWTurze() {
        return nrWTurze;
    }

    public boolean jestZleceniemKupna() {
        if (typZlecenia == Typ.kupno) return true;
        return false;
    }
}
