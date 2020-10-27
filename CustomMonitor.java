//package com.company;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class CustomMonitor {

    //klassen gjennom hvem alt skjer
    static final Scanner reader = new Scanner(System.in);
    String response = "";
    String emptyLineBuffer = "";
    PersonnummerArkiv arkivet;

    public CustomMonitor() {
    }

    public void startMeny() throws FileNotFoundException {

        arkivet = new PersonnummerArkiv(this);

        System.out.println("\n*****************************************************\n" +
                "Velkommen til registrering og sjekk av personnummer.\n" +
                "\n" + "Alle kommandoer maa du trykke 'enter' etter, for at de skal tre i kraft. " +
                "\nFor aa velge en fil med personnumre du vil validere, trykk 'f'." +
                "\nAlternativt, tast 'm' for manuell," +
                "foer du deretter skriver inn egne numre\n" +
                "du oensker aa validere, direkte i selve terminalen.\n\n"+
                "Trykk 'n' for aa avslutte." +
                "\n*****************************************************\n");

        response = reader.nextLine();

        if (response.equals("m") || response.equals("m")) {
            skrivInnManuelt();
        }

        if (response.equals("n") || (response.equals("N"))) {
            System.out.println("Programmet avsluttes om 3 sekunder.");
            try {
                TimeUnit.SECONDS.sleep(3);
                System.exit(1);
            }

            catch (Exception e) {
                System.exit(1);
            }
        }

        if (response.equals("b") || response.equals("B")) {
            System.out.println("Du valgte aa gaa tilbake til hovedmenyen. Vennligst gjoer et nytt valg.");
            startMeny();
        }

        if ((response.equals("f") || response.equals("F"))) {
            lesFraFil();

        }

        else {
            System.out.println("Du skreiv inn noe programmet ikke forstaar. Du blir naa sendt tilbake til startmenyen.");
            startMeny();
        }
    }

    public void skrivInnManuelt() throws FileNotFoundException {

        System.out.println("Du har valgt aa skrive inn foedselsnumre manuelt." +
                "Systemet avsluttes hvis du skriver inn 'n' i stedet" +
                ";'b' sender deg tilbake til startmeny\n\n" +
                "Skriv inn foedselsnummer du oensker aa sjekke, 11 siffer:\n");

        response = reader.nextLine();

        if (!response.equals("n") && !response.equals("N") && !response.equals("b") && !response.equals("B")) {


            Personnummer temp = arkivet.lagPersonnummer(response);

            if (arkivet.hentListe().contains(temp)) {
                for (Personnummer n: arkivet.hentListe()) {
                    if (n.hentHeleNummerIStreng().equals(temp.hentHeleNummerIStreng())) {
                        arkivet.sjekkPersonnummer(n);
                    }
                }
            }
            System.out.println("Trykk 'b' eller 'n' hvis du er ferdig. Skriv inn 'm' hvis du vil fortsette " +
                    "aa teste foedselsnumre manuelt i terminal.");
            response = reader.nextLine();

            if (response.equals("m") || response.equals("M")) {
                skrivInnManuelt();
            }
            else {
                cannotRead();
            }
        }

        else {
            cannotRead();
        }
    }

    public void lesFraFil() throws FileNotFoundException {
        System.out.println("Du har valgt aa skrive inn path til en fil med personnumre. Skriv inn med foelgende" +
                "format: C:\\Users\\brukernavn\\Mappe\\personnummer.txt." +
                "Filen maa ha kun ett personnummer paa hver linje." +
                "Vaer veldig noeye med at hver karakter i det du skriver," +
                "stemmer overens med filens faktiske pathbeskrivelse.");

        response = reader.nextLine();

        try {

            File file = new File(String.valueOf(response));

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {

                try {

                    Personnummer temp = arkivet.lagPersonnummer(sc.nextLine());
                }

                catch (Exception e) {
                    System.out.println("Feil under avlesning av personnummer." +
                                        "Soerg for at filen er i formatet: 'ddmmyyiiikk'." +
                                        "For informasjon om personnumre, se: https://no.wikipedia.org/wiki/F%C3%B8dselsnummer");
                    startMeny();
                }
            }
            arkivet.sjekkAlle();

            if (arkivet.hentGyldigeFoedselsnumre().size() > 0) {

                System.out.println("Her er alle dine gyldige foedselsnumre: ");

                for (Personnummer p : arkivet.hentGyldigeFoedselsnumre()) {
                    System.out.println(p.hentHeleNummerIStreng() + " er gyldig.");
                }

                System.out.println("Totalt antall gyldige i din fil: " + arkivet.hentGyldigeFoedselsnumre().size());
                System.out.println("\nTotalt ikke-gyldige foedselsnumre (heltall av serie paa 11) i din fil: " + arkivet.hentIkkeGyldigeFoedselsnumre().size());
                System.out.println("\nTotalt antall ikke-lesbare strenger (av andre tegn en heltall, eller av annen lengde enn 11) i din fil: " + arkivet.hentAntallNullreturverdier() + "\n");

            }
            startMeny();
        }

        catch (FileNotFoundException e) {
            System.out.println("Finner ikke fil med oppgitt navn. Du blir naa sendt tilbake til startmenyen.\n\n\n");
            startMeny();
        }
    }

    public void cannotRead() throws FileNotFoundException {
        System.out.println("Du skreiv inn noe programmet ikke forstaar. Du blir naa sendt tilbake til startmenyen.");
        startMeny();
    }


}