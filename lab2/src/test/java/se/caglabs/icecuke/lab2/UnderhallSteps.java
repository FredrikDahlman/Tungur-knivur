package se.caglabs.icecuke.lab2;

import cucumber.api.java.Before;
import cucumber.api.java.sv.Givet;
import cucumber.api.java.sv.När;
import cucumber.api.java.sv.Så;
import org.junit.Assert;
import se.caglabs.radbankir.Billbox;
import se.caglabs.radbankir.impl.RadbankirMaintenancur;
import se.caglabs.radbankir.Valuesur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnderhallSteps {

    private Map<Valuesur, Integer> antalReturnerade;
    private RadbankirMaintenancur radbankirMaintenancur;
    private Billbox billbox;

    @Before
    public void setup() {
        antalReturnerade = new HashMap<>();
        billbox = BankomatInstans.getInstans().getBillbox();
        radbankirMaintenancur = BankomatInstans.getInstans().getRadbankirMaintenancur();
        billbox.empty();
    }

    @Givet("^att det finns (\\d+) sedlar i facket för (\\d+)-kronorssedlar$")
    public void attDetFinnsSedlarIFacketFörKronorssedlar(int antal, int valor) throws Throwable {
        Valuesur valuesur = Valuesur.from(valor);
        List<Valuesur> valuesurs = new ArrayList<>();
        for(int i = 0; i < antal; i++ ) {
            valuesurs.add(valuesur);
        }
        billbox.deposit(valuesurs);
    }

    @När("^(?:att |)tekniker(?:n|) fyll(?:er|t) på med ([-]*\\d+) (\\d+)-kronorssedlar$")
    public void teknikernFyllerMedKronorssedlar(int antal, int valor) throws Throwable {
        Valuesur valuesur = Valuesur.from(valor);
        List<Valuesur> returneradeSedlar = radbankirMaintenancur.loadBills(valuesur, antal);

        this.antalReturnerade.putIfAbsent(valuesur, 0);
        this.antalReturnerade.put(valuesur, returneradeSedlar.size() + this.antalReturnerade.get(valuesur));
    }

    @Så("^matar bankomaten ut (\\d+) (\\d+)-kronorssedlar$")
    public void matarBankomatenUtKronorssedlar(int antal, int valor) throws Throwable {
        Valuesur valuesur = Valuesur.from(valor);

        antalReturnerade.putIfAbsent(valuesur, 0);
        Assert.assertEquals(Integer.valueOf(antal), antalReturnerade.get(valuesur));
        antalReturnerade.remove(valuesur);
    }

    @Så("^innehåller bankomaten (\\d+) (\\d+)-kronorssedlar$")
    public void innehållerBankomatenKronorssedlar(int antal, int valor) throws Throwable {
        Valuesur valuesur = Valuesur.from(valor);
        int antalSedlarIBankomat = radbankirMaintenancur.showMeTheMoney().get(valuesur);
        Assert.assertEquals(antal, antalSedlarIBankomat);
    }

    @Så("^finns det (\\d+) (\\d+)-kronorssedlar i bankomaten$")
    public void finns_det_kronorssedlar_i_bankomaten(int antal, int valor) throws Throwable {
        Assert.assertEquals(Integer.valueOf(billbox.getBills().get(Valuesur.from(valor))), Integer.valueOf(antal));
    }
}
