package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class TestProgram {

    int antallStrengerAvvist = 0;
    int antallGyldigeFoedselsnumre = 0;
    int antallIkkeGyldigeFoedselsnumre = 0;

    Boolean testSuccessful = null;

    public TestProgram() {
    }

    public void run() {

        CustomMonitor testMonitor = new CustomMonitor();

        PersonnummerArkiv vaartArkiv = new PersonnummerArkiv(testMonitor);


        ArrayList<String> testList = new ArrayList<String>();
        testList.add("08113405733"); //disse ti foedselsnumrene er tilfeldig valgte fra: http://www.fnrinfo.no/verktoy/finnlovlige_tilfeldig.aspx
        testList.add("29103031646");
        testList.add("15116545168");
        testList.add("21016812223");
        testList.add("10024203749");
        testList.add("05068037966");
        testList.add("28129718988");
        testList.add("06040153924");
        testList.add("26113805519");
        testList.add("03016145016");

        testList.add("3333A444442"); //fem av disse er strenger uten noen som helst mening ("nonsense"-strenger)
        testList.add("3333344-442");
        testList.add("33333444442");
        testList.add("53333444442");
        testList.add("63333444442");
        testList.add("93334444421");
        testList.add("23333444442");
        testList.add("3444442");
        testList.add("3333344444112322");
        testList.add("");

        //resultatet, om min kode er korrekt, bør være at 5 av "nonsense"-strengene blir returnert som "null"
        //Slik blir int antallStrengerAvvist til slutt til 5.

        //Videre gaar jeg til slutt gjennom antall gyldige personnumre i objektet "vaartArkiv": her skal det være 5 ikke-gyldige
        //foedselsnumre, og 10 gyldige foedselsnumre.
        //
        //Min definisjon av ikke-gyldig foedselsnumre: string med symboler som representerer
        //heltall, og med riktig lengde (11 tegn).

        //Testen settes i gang:

        for (String s: testList) {

            Personnummer placeholder = vaartArkiv.lagPersonnummer(s);
            if (placeholder == null) {
                this.antallStrengerAvvist++;
            }
        }

        vaartArkiv.sjekkAlle();

        this.antallIkkeGyldigeFoedselsnumre = vaartArkiv.hentIkkeGyldigeFoedselsnumre().size();
        this.antallGyldigeFoedselsnumre = vaartArkiv.hentGyldigeFoedselsnumre().size();

        for (Personnummer p: vaartArkiv.hentGyldigeFoedselsnumre()) {

        }

        if (antallStrengerAvvist == 5 && antallGyldigeFoedselsnumre == 10 && antallIkkeGyldigeFoedselsnumre == 5) {
            this.testSuccessful = true;
            System.out.println("Testen var en suksess!");
        }

        else {
            this.testSuccessful = false;
            System.out.println("Testen feilet.\n");
        }

        Scanner reader = new Scanner(System.in);
        String emptyLineBuffer = "";

        System.out.println("Resultat:\n" +
                            "Antall strenger avvist (oensket resultat: 5): " + this.antallStrengerAvvist +
                            "\nAntall ikke-gyldige foedselsnumre (oensket resultat: 5): "+ this.antallIkkeGyldigeFoedselsnumre +
                            "\nAntall gyldige foedselsnumre (oensket resultat: 10): " + this.antallGyldigeFoedselsnumre);



        System.out.println("\nResultatet av testen vises over.\n" +
                                "Trykk hvilken som helst tast for aa avslutte");
        reader.nextLine();


    }
}
