import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;

public class Main {
    private static int iloscPrzetrwancow;
    private static int iloscMiejsc;
    private static Schronienie schronienie;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Podaj ilosc przetrwancow: ");
        iloscPrzetrwancow = scanner.nextInt();

        System.out.print("Podaj ilosc miejsc w schronie: ");
        iloscMiejsc = scanner.nextInt();

        schronienie = new Schronienie(iloscMiejsc, iloscPrzetrwancow);

        for (int i = 0; i < iloscPrzetrwancow; i++) {
            new Przetrwaniec(i, schronienie).start();
        }
    }
}


class Przetrwaniec extends Thread {
    // stany przetrwanca
    static int SCHRONIENIE = 1;
    static int POSZUKIWANIA = 2;
    static int SZUKANIE_SCHRONIENIA = 3;
    static int SMIERC = 4;

    private int stan;
    private int numer;
    private int energia = 100;
    private int glod = 100;
    Schronienie schronisko;
    private Random rand;
    boolean poszukiwaniecUmarl = false;

    public Przetrwaniec(int numer, Schronienie schronisko) {
        stan = POSZUKIWANIA;
        this.numer = numer;
        this.schronisko = schronisko;
        rand = new Random();
    }

    public void run() {
        while (true) {
            if (stan == SCHRONIENIE) {
                // poszukiwacz odpoczywa i sie odzywia
                try {
                    glod = 100;
                    sleep(5000);
                    energia = 100;
                } catch (InterruptedException e) { System.out.println("Error message: " + e.getMessage()); }

                if (rand.nextInt(2) == 0) {
                    stan = POSZUKIWANIA;
                    stan = schronisko.ruszajNaPoszukiwania(numer);
                } else {
                    System.out.println("Przetrwaniec " + numer + " chce jeszcze posiedziec w schronieniu");
                }
            } else if (stan == POSZUKIWANIA) {
                System.out.println("Przetrwaniec " + numer + " poszukuje nowych rzeczy");

                energia -= ThreadLocalRandom.current().nextInt(10, 20 + 1);
                glod -= ThreadLocalRandom.current().nextInt(10, 20 + 1);

                if (glod <= 20 || energia <= 20) {
                    stan = SZUKANIE_SCHRONIENIA;
                } else try {
                    sleep(rand.nextInt(1000));
                } catch (InterruptedException e) {}
            } else if (stan == SZUKANIE_SCHRONIENIA) {
                System.out.println("Przetrwaniec " + numer + " puka do drzwi schronienia");
                stan = schronisko.wejdzDoSchronienia();

                if (stan == POSZUKIWANIA) {
                    System.out.println("Przetrwaniec " + numer + " nie znalazl miejsca w schronieniu, wiec powraca na poszukiwania");
                    glod -= ThreadLocalRandom.current().nextInt(5, 15);

                    if (glod <= 0) {
                        stan = SMIERC;
                    }
                } else {
                    System.out.println("Poszukiwacz " + numer + " wszedl do schronienia");
                }
            } else if (stan == SMIERC) {
                if (!poszukiwaniecUmarl) {
                    System.out.println("Przetrwaniec nr " + numer + " umarl na poszukiwaniach");
                    poszukiwaniecUmarl = true;
                    schronisko.zmniejsz();
                }

            }

        }
    }
}

class Schronienie {
    static int SCHRONIENIE = 1;
    static int POSZUKIWANIA = 2;

    private int iloscMiejsc;
    private int iloscZajetychMiejsc;
    int iloscPrzetrwancow;

    public Schronienie(int iloscMiejsc, int iloscPrzetrwancow) {
        this.iloscMiejsc = iloscMiejsc;
        this.iloscPrzetrwancow = iloscPrzetrwancow;
        this.iloscZajetychMiejsc = 0;
    }

    synchronized int ruszajNaPoszukiwania(int numer) {
        iloscZajetychMiejsc--;
        System.out.println("Poszukiwacz nr " + numer + " wyruszyl na poszukiwania ze schroniska");
        return POSZUKIWANIA;
    }

    synchronized int wejdzDoSchronienia() {
        try {
            sleep(1000);
        } catch (InterruptedException e) {}

        if (iloscZajetychMiejsc < iloscMiejsc) {
            iloscZajetychMiejsc++;
            System.out.println("Przetrwaniec znalazl miejsce w schronieniu");
            System.out.println("Pozostalo " + (iloscMiejsc - iloscZajetychMiejsc) + " wolnych miejsc. Ilosc zajetych miejsc wynosi: " + iloscZajetychMiejsc);
            return SCHRONIENIE;
        } else {
            return POSZUKIWANIA;
        }
    }

    synchronized void zmniejsz() {
        iloscPrzetrwancow--;
        System.out.println("Zmniejszono ilosc przetrwancow. Ilosc zywych przetrwancow: " + iloscPrzetrwancow);
    }

}
