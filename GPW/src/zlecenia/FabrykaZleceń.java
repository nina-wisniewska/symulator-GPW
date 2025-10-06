package zlecenia;
import pomocnicze.Losowanie;

public class FabrykaZleceń {

    private  Class<? extends Zlecenie>[] klasy = new Class[]{
            BezTerminuWażności.class,
            Natychmiastowe.class,
            WażneDoNTury.class,
            WykonajLubAnuluj.class
    };

    public Zlecenie stwórzLosoweZlecenie() throws Exception {

        int i = Losowanie.losuj(0, klasy.length);
        return klasy[i].getDeclaredConstructor().newInstance();
    }

}
