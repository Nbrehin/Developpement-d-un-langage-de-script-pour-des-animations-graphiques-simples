package exercice3;

import java.awt.Color;
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
 * Classe Exercice3_0
 * 
 * Cette classe crée une fenêtre graphique, modifie les couleurs de la fenêtre
 * et du rectangle, puis déplace le rectangle selon un trajet rectangulaire.
 * 
 * Variables d'instance :
 * - `space` : Fenêtre graphique.
 * - `robi` : Rectangle graphique.
 * - `script` : Script contenant les commandes pour modifier les couleurs et
 *   déplacer le rectangle.
 * 
 * Dépendances :
 * - Utilise les classes `GSpace` et `GRect` pour la gestion graphique.
 * - Utilise `SParser` et `SNode` pour analyser et exécuter le script.
 */
public class Exercice3_0 {

    GSpace space = new GSpace("Exercice 3", new Dimension(200, 100));
    GRect robi = new GRect();
    String script = "(space setColor black) (robi setColor yellow) (space sleep 1000) "
            + "(space setColor white) (space sleep 1000) (robi setColor red) "
            + "(space sleep 1000) (robi translate 100 0) (space sleep 1000) "
            + "(robi translate 0 50) (space sleep 1000) (robi translate -100 0) "
            + "(space sleep 1000) (robi translate 0 -50)";

    /**
     * Constructeur de la classe Exercice3_0
     * 
     * Ajoute le rectangle à la fenêtre graphique, ouvre la fenêtre et
     * exécute le script.
     */
    public Exercice3_0() {
        space.addElement(robi);
        space.open();
        this.runScript();
    }

    /**
     * Exécute le script en analysant les commandes et en les exécutant
     * séquentiellement.
     * 
     * Algorithme :
     * - Le script est analysé par le `SParser` pour être découpé en nœuds (`SNode`).
     * - Chaque nœud est traité individuellement pour exécuter la commande correspondante.
     */
    private void runScript() {
        SParser<SNode> parser = new SParser<>();
        List<SNode> rootNodes = null;

        try {
            rootNodes = parser.parse(script); // Découpage du script en nœuds
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator<SNode> itor = rootNodes.iterator();
        while (itor.hasNext()) {
            this.run(itor.next());
        }
    }

    /**
     * Exécute une commande spécifique en fonction de son contenu.
     * 
     * @param expr Une commande découpée en nœuds pour faciliter son traitement.
     */
    private void run(SNode expr) {
        Command cmd = getCommandFromExpr(expr);
        if (cmd == null) {
            throw new Error("Impossible de trouver une commande pour : " + expr);
        }
        cmd.run();
    }

    /**
     * Interprète une expression pour retourner la commande correspondante.
     * 
     * @param expr Une commande sous forme d'expression.
     * @return Une instance de la commande correspondante.
     */
    private Command getCommandFromExpr(SNode expr) {
        List<SNode> c = expr.children();
        Iterator<SNode> itor = c.iterator();
        int i = 0;
        SNode[] n = new SNode[5];

        // Remplissage du tableau de nœuds pour décoder la commande
        while (itor.hasNext()) {
            n[i] = itor.next();
            i++;
        }

        // Traitement des commandes en fonction de leur type
        switch (n[0].contents()) {
            case "space":
                switch (n[1].contents()) {
                    case "sleep":
                        return new SpaceSleep(Integer.parseInt(n[2].contents()));
                    case "setColor":
                        return new SpaceChangeColor(Tools.getColorByName(n[2].contents()));
                }
                break;
            case "robi":
                switch (n[1].contents()) {
                    case "translate":
                        return new RobiTranslate(Integer.parseInt(n[2].contents()), Integer.parseInt(n[3].contents()));
                    case "setColor":
                        return new RobiChangeColor(Tools.getColorByName(n[2].contents()));
                }
                break;
        }
        return null;
    }

    /**
     * Point d'entrée du programme.
     * 
     * Crée une nouvelle instance de la classe Exercice3_0.
     * 
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        new Exercice3_0();
    }

    /**
     * Interface Command
     * 
     * Représente une commande pouvant être exécutée.
     */
    public interface Command {
        void run();
    }

    /**
     * Classe SpaceChangeColor
     * 
     * Modifie la couleur de la fenêtre graphique (`space`).
     */
    public class SpaceChangeColor implements Command {
        Color newColor;

        /**
         * Constructeur SpaceChangeColor
         * 
         * @param newColor La nouvelle couleur à appliquer à la fenêtre.
         */
        public SpaceChangeColor(Color newColor) {
            this.newColor = newColor;
        }

        @Override
        public void run() {
            space.setColor(newColor);
        }
    }

    /**
     * Classe SpaceSleep
     * 
     * Stoppe l'exécution pendant un temps défini.
     */
    public class SpaceSleep implements Command {
        int temps;

        /**
         * Constructeur SpaceSleep
         * 
         * @param temps Durée de la pause en millisecondes.
         */
        public SpaceSleep(int temps) {
            this.temps = temps;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(temps);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Classe RobiChangeColor
     * 
     * Change la couleur du rectangle graphique (`robi`).
     */
    public class RobiChangeColor implements Command {
        Color newColor;

        /**
         * Constructeur RobiChangeColor
         * 
         * @param newColor La nouvelle couleur à appliquer au rectangle.
         */
        public RobiChangeColor(Color newColor) {
            this.newColor = newColor;
        }

        @Override
        public void run() {
            robi.setColor(newColor);
        }
    }

    /**
     * Classe RobiTranslate
     * 
     * Déplace le rectangle graphique (`robi`) dans la fenêtre.
     */
    public class RobiTranslate implements Command {
        int X, Y;

        /**
         * Constructeur RobiTranslate
         * 
         * @param X Déplacement horizontal.
         * @param Y Déplacement vertical.
         */
        public RobiTranslate(int X, int Y) {
            this.X = X;
            this.Y = Y;
        }

        @Override
        public void run() {
            Point p = new Point(X, Y);
            robi.translate(p);
        }
    }
}