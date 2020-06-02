package com.company;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Personnummer {

    private String dag; //private så man ikke kan aksessere disse direkte, kun gjennom metode. Kod kodekutyme!
    private String maaned;
    private String aar;

    private String individnummer;
    private String kontrollnummer;

    private int[] alleSiffer = new int[12]; //nyttig array å ha under sjekkeprosessen

    Boolean erGyldig;
    String aarsakTilTilstand = "";

    public Personnummer(String d, String m, String a, String i, String k, int[] alle) { //gjør string til int i annen metodE; hent personnummer fra ekstern fil eller API
        this.dag = d;
        this.maaned = m;
        this.aar = a;

        this.individnummer = i;
        this.kontrollnummer = k;

        this.alleSiffer = alle;
    }

    public void settHvorvidtGyldig(Boolean v, String aarsak) {
        this.erGyldig = v;
        this.aarsakTilTilstand = aarsak;
    }

    public String hentAarsakTilTilstand() {
        return this.aarsakTilTilstand;
    }

    public String[] hentDato() {
        return new String[]{dag, maaned, aar};
    }

    public String hentIndividnummer() {
        return this.individnummer;
    }

    public String hentKontrollnummer() {
        return this.kontrollnummer;
    }

    public int[] hentArrayUtenToSisteSifre() {

        int[] arrayUtenToSisteSifre = new int[9];

        for (int i = 0; i < arrayUtenToSisteSifre.length; i++) {
            arrayUtenToSisteSifre[i] = this.alleSiffer[i];
        }
        return arrayUtenToSisteSifre;
    }

    public int[] hentArrayMedAlleSiffer() {
        return alleSiffer;
    }

    public String hentHeleNummerIStreng() {
        return (this.dag+this.maaned+this.aar+this.individnummer+this.kontrollnummer);
    }

    public void printAlt() {
        System.out.println(this.dag);
        System.out.println(this.maaned);
        System.out.println(this.aar);
        System.out.println(this.individnummer);
        System.out.println(this.kontrollnummer);
    }

}
