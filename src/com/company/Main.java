package com.company;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;

public class Main {

    //main er alltid filen som skal kjoeres fra terminal. Valgene dukker opp i terminalen.

    static final Scanner reader = new Scanner(System.in);
    String emptyLineBuffer = "";

    public static void main(String[] args) throws FileNotFoundException {
        CustomMonitor monitor = new CustomMonitor();

        System.out.println("\n****************************************************************************************\n" +
                        "Hei, bruker! Du faar naa to valg. Trykker du '1', deretter 'enter', kjoerer hardkodede tester.\n");
        System.out.println("Trykker du '2', deretter 'enter', faar du opp en meny hvor du kan teste hvorvidt dine egne\n" +
                        "tall er gyldige foedselsnumre! Tast hvilken som helst annen tast for aa avslutte." +
                            "\n*************************************************************************************");

        String response = reader.nextLine();

        if (response.equals("1")) {
            TestProgram vaarTest = new TestProgram();
            vaarTest.run();
        }

        if (response.equals("2")) {
            monitor.startMeny(); //Dette er en måte å "dynamisk" teste alle mulige fødselsnumre: ved inntast, eller lesning
        }
        //fra fil.
        //Brukeren velger selv mellom egen utprøving eller hardkodede tester.


        else {
        System.out.println("Programmet avsluttes om 3 sekunder.");
        try {
            TimeUnit.SECONDS.sleep(3);
            System.exit(1);
        }

        catch (Exception e) {
            System.exit(1);
        }
        }
    }
}