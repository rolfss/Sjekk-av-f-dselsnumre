//package com.company;

import java.io.*;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PersonnummerArkiv { //et imaginært samlepunkt, som gjør main mer oversiktlig, og gir en mer konkret beholder
                                    // til alle personnumre

    private ArrayList<Personnummer> allePersonnumre = new ArrayList<Personnummer>();
    private ArrayList<Personnummer> gyldigePersonnumre = new ArrayList<Personnummer>();
    private ArrayList<Personnummer> ikkeGyldigePersonnumre = new ArrayList<Personnummer>(); //sier: ikke-gyldige, ikke ugyldige, så de to listene ikke skal forveksles så lett i koden
    private int antallNullReturverdier = 0;

    private CustomMonitor monitor = new CustomMonitor();

    public PersonnummerArkiv(CustomMonitor mon) {
        this.monitor = mon;
    }

    public Personnummer lagPersonnummer(String nr) {

        if (nr.length() != 11) {
            System.out.println(nr + " har ugyldig lengde.");
            antallNullReturverdier++;
            return null; //returnerer null hvis "tråden" som blir gitt som parameter, er "intelligibelt"
        }

        for (int u = 0; u < nr.length(); u++) { //sjekker om stringen består av characters som kan oversettes til tall
            if (Character.isDigit(nr.charAt(u)) == false) {
                System.out.println(nr + " har ugyldig innhold (inneholder annet enn heltall).");
                antallNullReturverdier++;
                return null;
            }
        }

        String dagString = (String.valueOf(nr.charAt(0)) + String.valueOf(nr.charAt(1)));
        String maanedString = (String.valueOf(nr.charAt(2)) + String.valueOf(nr.charAt(3)));
        String aarString = (String.valueOf(nr.charAt(4)) + String.valueOf(nr.charAt(5)));
        String individsifferString = (String.valueOf(nr.charAt(6)) + String.valueOf(nr.charAt(7)) + String.valueOf(nr.charAt(8)));
        String kontrollsifferString = String.valueOf(nr.charAt(9) + String.valueOf(nr.charAt(10)));

        String dag = dagString;
        String maaned = maanedString;
        String aar = aarString;

        String individsiffer = individsifferString;
        String kontrollsiffer = kontrollsifferString;

        int[] allInfo = new int[nr.length()]; //denne er 11 i lengde uansett. Men bruker .length() for det illustrerer hva som skjer.

        for (int e = 0; e < nr.length(); e++) {
            allInfo[e] = Character.getNumericValue(nr.charAt(e));
        }

        Personnummer ny = new Personnummer(dag, maaned, aar, individsiffer, kontrollsiffer, allInfo);
        allePersonnumre.add(ny);
        return ny;
    }

    public void sjekkPersonnummer(Personnummer nr) { //jeg har valgt å ikke regne med skuddår, da jeg ikke tror offentlige systemer gjør det heller (i det minste i Norge)
//denne funksjonen er "morsfunksjon" til alle øvrige sjekk-funksjoner.
        if (sjekkKontrollSifre(nr) == true) { //foerst sjekker vi om kontrollsifrene er i orden; jeg
                                                // vurderte det dithen at man likegodt kunne begynne her

            ArrayList<Date> muligGyldigeDatoer = sjekkOmDatoErGyldig(nr); //deretter finner man muligGyldigeDatoer
            ArrayList<String> muligeAar = finnMuligeAar(nr); //deretter muligeAarsspenn

            if (muligGyldigeDatoer.size() > 0 || muligeAar.size() > 0)  {

                if (sjekkOmOverlapp(muligGyldigeDatoer, muligeAar) == true) { //siste kriterie foer noe vurderes til aa vaere gyldig foedselsnummer
                    System.out.println(nr.hentHeleNummerIStreng() + " er et gyldig foedselsnummer.\n");

                    gyldigePersonnumre.add(nr);
                    nr.settHvorvidtGyldig(true, "Gjennomgaatt sjekk.");

                }

                else {
                    System.out.println("Ingen mulig gyldige datoer. Personnummer " + nr.hentHeleNummerIStreng() + " er ugyldig\n");
                    nr.settHvorvidtGyldig(false, "Ugyldig; fordi det ikke peker paa mulige, gyldige datoer.");
                    ikkeGyldigePersonnumre.add(nr);
                }
            }

            else {

                System.out.println("Ingen mulig gyldige datoer. Personnummer " + nr.hentHeleNummerIStreng() + " er ugyldig\n");
                nr.settHvorvidtGyldig(false, "Ugyldig; fordi det ikke peker paa mulige, gyldige datoer.");
                ikkeGyldigePersonnumre.add(nr);

            }

        }

        else {
            System.out.println("Kontrollsiffer til personnummer " + nr.hentHeleNummerIStreng() + " er ugyldig.\n");
            nr.settHvorvidtGyldig(false, "Ugyldig; fordi dets kontrollsiffer er ugyldig.");
            ikkeGyldigePersonnumre.add(nr);
        }
    }

    public Boolean sjekkKontrollSifre(Personnummer nr) {

        int[] foersteTallrekke = new int[]{3, 7, 6, 1, 8, 9, 4, 5, 2}; //før-bestemte tallrekker, en del av personnummer-algoritmen
        int[] andreTallrekke = new int[]{5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

        int[] personnummerUtenKontrollnummer = nr.hentArrayUtenToSisteSifre();


        int[] personnummerMedFoersteKontrollnummer = new int[10]; //initialiseres nå, men brukes ikke før vi leter etter
        //andre kontrollnummer.

        /*for (int k: andreKontrollnumre) {
            System.out.println(k);
        }*/

        int k1 = 0; //første kontrollsiffer
        int k2 = 0; //andre kontrollsffer

        for (int s = 0; s < foersteTallrekke.length; s++) { //s for siffer/tall
            k1 += personnummerUtenKontrollnummer[s] * foersteTallrekke[s];
        }

        k1 = (11-(k1%11));

        if (k1 == 11) {
            k1 = 0;
        }


        if (k1 != Character.getNumericValue(nr.hentKontrollnummer().charAt(0))) { //hvis man ikke lander på riktig kontrollnummer, bryt.

            nr.settHvorvidtGyldig(false, "Beregningen landet ikke paa det oppgitte, foerste kontrollnummer til " + nr.hentHeleNummerIStreng()+"\n");
            System.out.println("Beregningen landet ikke paa det oppgitte, foerste kontrollnummer til " + nr.hentHeleNummerIStreng()+"\n");
            return false;
        }

        if (k1 == 10) {
            nr.settHvorvidtGyldig(false, "Foerste kontrollnummer til " + nr.hentHeleNummerIStreng() + "er 10, derfor ugyldig.\n");
            System.out.println("Foerste kontrollnummer til " + nr.hentHeleNummerIStreng() + "er 10, derfor ugyldig.\n");
            return false;
        }

        for (int a = 0; a < personnummerUtenKontrollnummer.length; a++) {
            personnummerMedFoersteKontrollnummer[a] = personnummerUtenKontrollnummer[a];
        }

        personnummerMedFoersteKontrollnummer[andreTallrekke.length-1] = k1; //legger til første kontrollnummer, for sjekk av andre kontrollnummer


        for (int t = 0; t < andreTallrekke.length; t++) { //t for tall/siffer
            k2 += personnummerMedFoersteKontrollnummer[t] * andreTallrekke[t];
        }

        k2 = (11-(k2%11));


        if (k2 == 11) {
            k2 = 0;
        }


        if (k2 != Character.getNumericValue(nr.hentKontrollnummer().charAt(1))) { //hvis man ikke lander på riktig kontrollnummer, bryt.
            nr.settHvorvidtGyldig(false, "Beregningen landet ikke paa det oppgitte, foerste kontrollnummer til "
                    + nr.hentHeleNummerIStreng() + ". Nummeret er derfor ugyldig.");
            System.out.println("Beregningen landet ikke paa det oppgitte, foerste kontrollnummer til "
                    + nr.hentHeleNummerIStreng() + ". Nummeret er derfor ugyldig.");
            return false;
        }

        if (k2 == 10) {
            nr.settHvorvidtGyldig(false, "Andre kontrollnummer til " + nr.hentHeleNummerIStreng() + "er 10. Nummeret er derfor ugyldig.");
            System.out.println("Andre kontrollnummer til " + nr.hentHeleNummerIStreng() + "er 10. Nummeret er derfor ugyldig.");
            return false;
        }

        else {
            nr.settHvorvidtGyldig(true,"Kontrollsiffer til foedselsnummer " + nr.hentHeleNummerIStreng() + "er gyldig.\n");
            return true;
        }

    }

    public ArrayList<String> finnMuligeAar(Personnummer nr) {

        ArrayList<String> muligeAar = new ArrayList<String>();

        int foersteSiffer = Integer.parseInt(String.valueOf(nr.hentIndividnummer().charAt(0)));

        if (foersteSiffer <= 4) { //000–499 omfatter personer født i perioden 1900–1999.
            muligeAar.add("1900-1999");
        }

        else if (Integer.parseInt(nr.hentIndividnummer()) >= 500 || Integer.parseInt(nr.hentIndividnummer()) <= 749) { //hvis verdien av individnummeret er mellom 750-999, er det også gyldig.
            muligeAar.add("2000-2039");
            muligeAar.add("1854-1899");
        }

        else if (Integer.parseInt(nr.hentIndividnummer()) >= 900) {
            muligeAar.add("1940-1999");

            if (muligeAar.size() > 0) { //unngaa nullpointerexception
                for (String s: muligeAar) { //fjerne det eneste mulige duplikat fra listen
                    if (s.equals("2000-2039")) {
                        muligeAar.remove(s);
                    }
                }
            }

            muligeAar.add("2000-2039");

        }

        return muligeAar;

    }

    public ArrayList<Date> sjekkOmDatoErGyldig(Personnummer nr) {
        //denne metoden legger til enten 19 eller 20, foran "årstallet" i fødselsnummeret.
        //Deretter sjekker man begge datoer, hvorvidt disse er gyldige, via innebygd "Dato-funksjonalitet" i java.
        //returneres én, eller to gyldige datoer (blir lagt inn i arrayList med Date-opbjekter),
        // vet vi at de seks dato-strengene potensielt oppgir en gyldig fødselsdato,
        // hvorpå vi går videre til neste funksjon.
        //Begrenser meg til nåværende og forrige århundre, fordi det ikke lever noen fra 1900-tallet den dag i dag.

        String[] dato = nr.hentDato();

        //dagens dato er for å sjekke at fødselsnummeret ikke opererer med fremtidige datoer.

        ArrayList<Date> muligGyldigeDatoer = new ArrayList<Date>();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        df.setLenient(false);

        Date d = new Date();
        String date = df.format(d);

        Date currentDate = null;
        Date tempDate = null;

        try {
            currentDate = df.parse(date);
            tempDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String possibleDateOne = dato[0] + "-" + dato[1] + "-" + "19" + dato[2];
        String possibleDateTwo = dato[0] + "-" + dato[1] + "-" + "20" + dato[2];


        try {
            tempDate = df.parse(possibleDateOne); //hopper ut av try blokken om dette ikke passer inn i formatet,
            //altså er gyldig

            Date foersteMuligeDato = df.parse(possibleDateOne);


            if (foersteMuligeDato.compareTo(currentDate) <= 0) {
                muligGyldigeDatoer.add(foersteMuligeDato); //legges til i liste, hvis dette er en reell dato
                //i en moderne kalender, eller rent oppspinn; og hvis det er en dato som er i dag, eller tidligere.
            }
        }

        catch (Exception e) {
            System.out.println("1. mulige dato for " + nr.hentHeleNummerIStreng() + "er ugyldig.");
        }

        try {

            tempDate = df.parse(possibleDateTwo); //hopper ut av try blokken om dette ikke passer inn i formatet,
            //altså er gyldig

            Date andreMuligeDato = df.parse(possibleDateTwo);


            if (andreMuligeDato.compareTo(currentDate) <= 0) {
                muligGyldigeDatoer.add(andreMuligeDato); //legges til i liste, hvis dette er en reell dato
                //i en moderne kalender, eller rent oppspinn; og hvis det er en dato som er i dag, eller tidligere.
            }
        }


        catch (Exception e) {
            System.out.println("2. mulige dato for " + nr.hentHeleNummerIStreng() + "er ugyldig.");
        }

        return muligGyldigeDatoer;
    }

    public Boolean sjekkOmOverlapp(ArrayList<Date> muligeDatoer, ArrayList<String> muligeAar) {

        //denne funksjonen sjekker om det er overlapp på mulige årstall (to) i ethvert foedselsnummer
        //og kontrollnumrene, som man kan utlede en tidsepoke på (f.eks 2000-2039).

        Boolean harFunnetOverlapp = false;

        if (muligeAar.size() == 0 || muligeDatoer.size() == 0) {
            return harFunnetOverlapp;
        }


        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        int[] konkreteDatoersAarstall = new int[muligeDatoer.size()];

        for (int k = 0; k < muligeDatoer.size(); k++) { //splitter all info om konkrete datoer til kun aarstall
            String tempString = formatter.format(muligeDatoer.get(k));
            String[] tempArray = tempString.split("-");

            konkreteDatoersAarstall[k] = Integer.parseInt(tempArray[2]);
        }


        int[] muligeAarsspenn = new int[muligeAar.size()*2]; //er to aarstall lagret i streng-objektene i "muligeAar
                                                            //maa derfor ha dobbel plass.


        for (int teller = 0; teller < muligeAar.size(); teller++) {

            String[] temp2 = muligeAar.get(teller).split("-");
            muligeAarsspenn[teller+teller] = Integer.parseInt(temp2[0]); //denne koden sørger for at mulige år (maks 2 par), finner sin rette plass i int-arrayen muligeAarsspenn
            muligeAarsspenn[teller+teller+1] = Integer.parseInt(temp2[1]);
        }



        for (int e = 0; e < konkreteDatoersAarstall.length; e++) { //denne kodesnutten gjør dette: hvis et mulig aarstall
                                                            //finnes i ett "mulig" aarsgap, er "harFunnetOverlapp" true,
                                                            //og foedselsnummeret er derfor valid.

            for (int i = 0; i < muligeAarsspenn.length; i++) {

                if (konkreteDatoersAarstall[e] >= muligeAarsspenn[i]) {
                    if (i < 3) { //if-sjekk for å unngå IndexOutOfBoundsException
                        i = i + 2; //så man sørger for å behandle integer-ene i muligeAarsspenn[] som par; hvilket de er.
                    }


                    if (konkreteDatoersAarstall[e] <= muligeAarsspenn[i - 1]) {

                        harFunnetOverlapp = true;
                        return harFunnetOverlapp;
                    }
                }
            }
        }

        return harFunnetOverlapp;

    }

    public ArrayList<Personnummer> hentListe() {
        return this.allePersonnumre;
    }

    public void sjekkAlle() {
        for (Personnummer p: this.allePersonnumre) {
            sjekkPersonnummer(p);
        }
    }

    public ArrayList<Personnummer> hentIkkeGyldigeFoedselsnumre() {
        return this.ikkeGyldigePersonnumre;
    }

    public ArrayList<Personnummer> hentGyldigeFoedselsnumre() {
        return this.gyldigePersonnumre;
    }

    public int hentAntallNullreturverdier() {
        return this.antallNullReturverdier;
    }
}
