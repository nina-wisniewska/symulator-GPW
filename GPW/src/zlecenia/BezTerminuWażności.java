package zlecenia;

public class BezTerminuWażności extends Zlecenie{

    @Override
    public boolean czyAktualne(int tura) {
        return true;
    }

    @Override
    public void setTuraZłożenia(int turaZłożenia, int ostatnia) {
        this.tura = turaZłożenia;
    }
}
