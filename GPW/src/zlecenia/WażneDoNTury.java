package zlecenia;

import pomocnicze.Losowanie;

public class WażneDoNTury extends Zlecenie {
    private int nTura;

    @Override
    public boolean czyAktualne(int tura) {
        if (tura >= nTura) return false;
        return true;
    }

    @Override
    public void setTuraZłożenia(int turaZłożenia, int ostatnia) {
        this.tura = turaZłożenia;
        nTura = Losowanie.losuj(turaZłożenia, ostatnia);
    }
}
