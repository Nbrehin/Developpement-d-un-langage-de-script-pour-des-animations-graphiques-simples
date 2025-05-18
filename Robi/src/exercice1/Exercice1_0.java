package exercice1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

import graphicLayer.GRect;
import graphicLayer.GSpace;

/**
 * Classe Exercice1_0
 * 
 * Cette classe crée une fenêtre graphique et fait bouger un rectangle (robi)
 * le long des bords de cette fenêtre. Pendant son déplacement, le rectangle
 * change de couleur de manière aléatoire.
 * 
 * Variables d'instance :
 * - `space` : Fenêtre graphique dans laquelle le rectangle se déplace.
 * - `robi` : Rectangle graphique qui se déplace dans la fenêtre.
 * 
 * Dépendances :
 * - Utilise les classes `GSpace` et `GRect` de la bibliothèque `graphicLayer`.
 */
public class Exercice1_0 {
    // Fenêtre graphique
    GSpace space = new GSpace("Exercice 1", new Dimension(200, 150));
    // Rectangle graphique
    GRect robi = new GRect();

    /**
     * Constructeur de la classe Exercice1_0
     * 
     * Initialise la fenêtre graphique, ajoute le rectangle à la fenêtre,
     * et lance une boucle infinie pour déplacer le rectangle le long des
     * bords de la fenêtre tout en changeant sa couleur.
     */
    public Exercice1_0() {
        space.addElement(robi); // Ajout du rectangle à la fenêtre
        space.open(); // Ouverture de la fenêtre graphique

        Point p = new Point(robi.getX(), robi.getY()); // Position initiale du rectangle
        int width = space.getSize().width - robi.getWidth(); // Limite horizontale
        int height = space.getSize().height - robi.getHeight(); // Limite verticale
        Random r = new Random(); // Générateur de couleurs aléatoires

        // Boucle principale : déplacement du rectangle
        while (true) {
            // Déplacement vers la droite
            while (robi.getX() < width) {
                width = space.getSize().width - robi.getWidth(); // Mise à jour des limites
                robi.setPosition(p);
                p.translate(1, 0); // Déplacement horizontal
                robi.setColor(new Color(r.nextFloat(), r.nextFloat(), r.nextFloat())); // Changement de couleur
                pause(5); // Pause pour ralentir le déplacement
            }
            // Déplacement vers le bas
            while (robi.getY() < height) {
                height = space.getSize().height - robi.getHeight(); // Mise à jour des limites
                robi.setPosition(p);
                p.translate(0, 1); // Déplacement vertical
                robi.setColor(new Color(r.nextFloat(), r.nextFloat(), r.nextFloat()));
                pause(5);
            }
            // Déplacement vers la gauche
            while (robi.getX() > 0) {
                robi.setPosition(p);
                p.translate(-1, 0); // Déplacement horizontal inverse
                robi.setColor(new Color(r.nextFloat(), r.nextFloat(), r.nextFloat()));
                pause(5);
            }
            // Déplacement vers le haut
            while (robi.getY() > 0) {
                robi.setPosition(p);
                p.translate(0, -1); // Déplacement vertical inverse
                robi.setColor(new Color(r.nextFloat(), r.nextFloat(), r.nextFloat()));
                pause(5);
            }
        }
    }

    /**
     * Pause le programme pendant un certain temps.
     * 
     * @param millis Durée de la pause en millisecondes.
     */
    private void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Point d'entrée du programme.
     * 
     * Crée une nouvelle instance de la classe Exercice1_0 pour lancer
     * l'application graphique.
     * 
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        new Exercice1_0();
    }
}