package exercice2;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import graphicLayer.GRect;
import graphicLayer.GSpace;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;

/**
 * Classe Exercice2_1_0
 * 
 * Cette classe crée une fenêtre graphique et exécute un script pour changer
 * la couleur de la fenêtre et du rectangle, puis déplacer le rectangle
 * selon un trajet carré.
 * 
 * Variables d'instance :
 * - `space` : Fenêtre graphique.
 * - `robi` : Rectangle graphique.
 * - `script` : Liste de commandes prédéfinies pour modifier les couleurs et
 *   déplacer le rectangle.
 * 
 * Dépendances :
 * - Utilise les classes `GSpace` et `GRect` pour la gestion graphique.
 * - Utilise `SParser` et `SNode` pour analyser et exécuter le script.
 */
public class Exercice2_1_0 {
    GSpace space = new GSpace("Exercice 2_1", new Dimension(200, 100));
    GRect robi = new GRect();
    String script = "(space setColor black) (robi setColor yellow) (space color white) "
            + "(robi color red) (robi translate 50 0) (space sleep 100) "
            + "(robi translate 0 50) (space sleep 100) (robi translate -50 0) "
            + "(space sleep 100) (robi translate 0 -50)";

    /**
     * Constructeur de la classe Exercice2_1_0
     * 
     * Ajoute le rectangle à la fenêtre graphique, ouvre la fenêtre et
     * exécute le script.
     */
    public Exercice2_1_0() {
        space.addElement(robi);
        space.open();
        this.runScript();
    }

    /**
     * Exécute le script en analysant les commandes et en les exécutant
     * séquentiellement.
     */
    private void runScript() {
        SParser<SNode> parser = new SParser<>();
        List<SNode> rootNodes = null;
        try {
            rootNodes = parser.parse(script);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Iterator<SNode> itor = rootNodes.iterator();
        while (itor.hasNext()) {
            pause(500); // Pause entre chaque commande
            this.run(itor.next());
        }
    }

    /**
     * Exécute une commande spécifique en fonction de son contenu.
     * 
     * @param expr Une commande découpée en nœuds pour faciliter son traitement.
     */
    private void run(SNode expr) {
        List<SNode> c = expr.children();
        Iterator<SNode> itor = c.iterator();
        SNode[] n = new SNode[5];
        int i = 0;
        while (itor.hasNext()) {
            n[i] = itor.next();
            i++;
        }
        if (n[0].contents().equals("space")) {
            if (n[1].contents().equals("setColor") || n[1].contents().equals("color")) {
                space.setColor(Tools.getColorByName(n[2].contents()));
            } else if (n[1].contents().equals("sleep")) {
                pause(Integer.parseInt(n[2].contents()));
            }
        } else if (n[1].contents().equals("setColor") || n[1].contents().equals("color")) {
            robi.setColor(Tools.getColorByName(n[2].contents()));
        } else if (n[1].contents().equals("translate")) {
            Point p = new Point(Integer.parseInt(n[2].contents()), Integer.parseInt(n[3].contents()));
            robi.translate(p);
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
     * Crée une nouvelle instance de la classe Exercice2_1_0.
     * 
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        new Exercice2_1_0();
    }
}