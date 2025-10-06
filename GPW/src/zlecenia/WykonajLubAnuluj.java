package zlecenia;

public class WykonajLubAnuluj extends Zlecenie{

    @Override
    public boolean czyAktualne(int tura) {
        return false;
    }

    @Override
    public void setTuraZłożenia(int turaZłożenia, int ostatnia) {
        this.tura = turaZłożenia;
    }
}
