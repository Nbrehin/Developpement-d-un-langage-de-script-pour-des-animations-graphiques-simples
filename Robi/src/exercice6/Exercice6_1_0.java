package exercice6;

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
 * Classe Exercice6_1_0
 * 
 * Cette classe permet de créer une fenêtre graphique et d'exécuter des commandes
 * dynamiques définies par l'utilisateur pour manipuler les objets graphiques
 * (comme changer leur couleur ou les déplacer).
 * 
 * Variables d'instance :
 * - `environment` : Environnement contenant les références des objets manipulables.
 * 
 * Dépendances :
 * - Utilise les classes `GSpace` et `GRect` pour la gestion graphique.
 * - Utilise `SParser` et `SNode` pour analyser et exécuter les commandes.
 */
public class Exercice6_1_0 {
    // Environnement contenant les références des objets manipulables
    Environment environment = new Environment();

    /**
     * Constructeur de la classe Exercice6_1_0
     * 
     * Initialise la fenêtre graphique et les objets graphiques, puis enregistre
     * les commandes disponibles dans l'environnement.
     */
    public Exercice6_1_0() {
        GSpace space = new GSpace("Exercice 6", new Dimension(200, 100));
        GRect robi = new GRect();

        space.addElement(robi);
        space.open();

        Reference spaceRef = new Reference(space);
        Reference robiRef = new Reference(robi);

        // Commande : Pause l'exécution pendant un temps défini
        spaceRef.addCommand("sleep", new Command() {
            @Override
            public Reference run(Reference receiver, SNode method) {
                try {
                    Thread.sleep(Integer.parseInt(method.get(2).contents()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        // Commande : Change la couleur de la fenêtre graphique
        spaceRef.addCommand("setColor", new Command() {
            @Override
            public Reference run(Reference receiver, SNode method) {
                Color c = Tools.getColorByName(method.get(2).contents());
                ((GSpace) receiver.getReceiver()).setColor(c);
                return null;
            }
        });

        // Commande : Change la couleur du rectangle graphique
        robiRef.addCommand("setColor", new Command() {
            @Override
            public Reference run(Reference receiver, SNode method) {
                Color c = Tools.getColorByName(method.get(2).contents());
                ((GRect) receiver.getReceiver()).setColor(c);
                return null;
            }
        });

        // Commande : Déplace le rectangle graphique dans la fenêtre
        robiRef.addCommand("translate", new Command() {
            @Override
            public Reference run(Reference receiver, SNode method) {
                Point p = new Point(Integer.parseInt(method.get(2).contents()),
                        Integer.parseInt(method.get(3).contents()));
                ((GRect) receiver.getReceiver()).translate(p);
                return null;
            }
        });

        // Enregistrement des références dans l'environnement
        environment.addReference("space", spaceRef);
        environment.addReference("robi", robiRef);

        this.mainLoop();
    }

    /**
     * Boucle principale
     * 
     * Algorithme :
     * - Attend les commandes de l'utilisateur via la console.
     * - Analyse les commandes saisies et les exécute.
     */
    private void mainLoop() {
        while (true) {
            System.out.print("> "); // Invite de commande
            String input = Tools.readKeyboard(); // Lecture de l'entrée utilisateur
            SParser<SNode> parser = new SParser<>();
            List<SNode> compiled = null;

            // Analyse des commandes saisies
            try {
                compiled = parser.parse(input);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Exécution des commandes
            Iterator<SNode> itor = compiled.iterator();
            while (itor.hasNext()) {
                this.run(itor.next());
            }
        }
    }

    /**
     * Exécute une commande interprétée par la boucle principale.
     * 
     * @param expr Commande sous forme de nœud syntaxique (SNode).
     */
    private void run(SNode expr) {
        String receiverName = expr.get(0).contents(); // Nom du récepteur
        Reference receiver = environment.getReferenceByName(receiverName); // Récupération de la référence
        receiver.run(expr); // Exécution de la commande
    }

    /**
     * Point d'entrée du programme.
     * 
     * Crée une nouvelle instance de la classe Exercice6_1_0.
     * 
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        new Exercice6_1_0();
    }
}