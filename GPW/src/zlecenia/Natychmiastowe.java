package zlecenia;

public class Natychmiastowe extends Zlecenie {

    @Override
    public boolean czyAktualne(int tura) {
        return false;
    }

    @Override
    public void setTuraZłożenia(int turaZłożenia, int ostatnia) {
        this.tura = turaZłożenia;
    }
}
