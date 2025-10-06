package pomocnicze;

import zlecenia.Typ;
import zlecenia.Zlecenie;
import java.util.Comparator;

public class ZleceniaComparator implements Comparator<Zlecenie> {
    @Override
    public int compare(Zlecenie z1, Zlecenie z2) {
        if (z1.getLimitCeny() == z2.getLimitCeny()) {
            if (z1.getTura() == z2.getTura()) {
                return z1.getNrWTurze() - z2.getNrWTurze();
            }
            return  z1.getTura() - z2.getTura();
        }
        else {
            // porównujemy tylko zlecenia o tym samym typie
            if (z1.getTypZlecenia() == Typ.kupno) {

                // chcemy większą cenę na początek, czyli na odwrót
                return z2.getLimitCeny() - z1.getLimitCeny();
            }

            // zwyczajnie przy sprzedaży gdzie sortujemy cenami malejąco
            return z1.getLimitCeny() - z2.getLimitCeny();
        }
    }
}
