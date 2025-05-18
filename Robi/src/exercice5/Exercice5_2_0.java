package exercice5;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import graphicLayer.*;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;

/**
 * Classe Exercice5_2_0
 * 
 * Cette classe permet de créer une fenêtre graphique et d'exécuter des commandes
 * dynamiques définies par l'utilisateur pour manipuler des objets graphiques
 * (comme changer leur couleur, les déplacer, ou les ajouter/supprimer).
 * 
 * Variables d'instance :
 * - `environment` : Environnement contenant les références des objets manipulables.
 * 
 * Dépendances :
 * - Utilise les classes `GSpace`, `GRect`, `GOval`, `GImage`, et `GString` pour la gestion graphique.
 * - Utilise `SParser` et `SNode` pour analyser et exécuter les commandes.
 */
public class Exercice5_2_0 {
    Environment environment = new Environment();

    /**
     * Constructeur de la classe Exercice5_2_0
     * 
     * Initialise la fenêtre graphique et les objets graphiques, puis enregistre
     * les commandes disponibles dans l'environnement.
     */
    public Exercice5_2_0() {
        GSpace space = new GSpace("Exercice 5", new Dimension(200, 200));
        space.open();

        // Création des références pour les objets manipulables
        Reference spaceRef = new Reference(space);
        Reference rectClassRef = new Reference(GRect.class);
        Reference ovalClassRef = new Reference(GOval.class);
        Reference imageClassRef = new Reference(GImage.class);
        Reference stringClassRef = new Reference(GString.class);

        // Ajout des commandes pour la fenêtre graphique
        spaceRef.addCommand("setColor", new SetColor());
        spaceRef.addCommand("sleep", new Sleep());
        spaceRef.addCommand("add", new AddElement(environment));
        spaceRef.addCommand("del", new DelElement(environment));
        spaceRef.addCommand("setDim", new SetDim());

        // Ajout des commandes pour les classes d'éléments graphiques
        rectClassRef.addCommand("new", new NewElement());
        ovalClassRef.addCommand("new", new NewElement());
        imageClassRef.addCommand("new", new NewImage());
        stringClassRef.addCommand("new", new NewString());

        // Enregistrement des références dans l'environnement
        environment.addReference("space", spaceRef);
        environment.addReference("rect.class", rectClassRef);
        environment.addReference("oval.class", ovalClassRef);
        environment.addReference("image.class", imageClassRef);
        environment.addReference("label.class", stringClassRef);

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
                new Interpreter().compute(environment, itor.next());
            }
        }
    }

    /**
     * Point d'entrée du programme.
     * 
     * Crée une nouvelle instance de la classe Exercice5_2_0.
     * 
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        new Exercice5_2_0();
    }
}

/**
 * Classe NewElement
 * 
 * Permet de créer un nouvel élément graphique (rectangle ou ovale).
 */
class NewElement implements Command {
    @SuppressWarnings("unchecked")
    public Reference run(Reference reference, SNode method) {
        try {
            GElement e = ((Class<GElement>) reference.getReceiver()).getDeclaredConstructor().newInstance();
            Reference ref = new Reference(e);
            ref.addCommand("setColor", new SetColor());
            ref.addCommand("translate", new Translate());
            ref.addCommand("setDim", new SetDim());
            return ref;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

/**
 * Classe SetColor
 * 
 * Permet de changer la couleur d'un élément graphique ou de la fenêtre.
 */
class SetColor implements Command {
    public Reference run(Reference reference, SNode method) {
        Color c = Tools.getColorByName(method.get(2).contents());
        if (method.get(0).contents().equals("space")) {
            ((GSpace) reference.getReceiver()).setColor(c);
        } else {
            ((GElement) reference.getReceiver()).setColor(c);
        }
        return reference;
    }
}

/**
 * Classe Translate
 * 
 * Permet de déplacer un élément graphique.
 */
class Translate implements Command {
    public Reference run(Reference reference, SNode method) {
        Point p = new Point(Integer.parseInt(method.get(2).contents()), Integer.parseInt(method.get(3).contents()));
        ((GElement) reference.getReceiver()).translate(p);
        return reference;
    }
}

/**
 * Classe SetDim
 * 
 * Permet de modifier la taille d'un élément graphique ou de la fenêtre.
 */
class SetDim implements Command {
    public Reference run(Reference reference, SNode method) {
        Dimension d = new Dimension(Integer.parseInt(method.get(2).contents()), Integer.parseInt(method.get(3).contents()));
        if (method.get(0).contents().equals("space")) {
            ((GSpace) reference.getReceiver()).changeWindowSize(d);
        } else {
            ((GBounded) reference.getReceiver()).setDimension(d);
        }
        return reference;
    }
}

/**
 * Classe Sleep
 * 
 * Permet de suspendre l'exécution pendant un temps donné.
 */
class Sleep implements Command {
    public Reference run(Reference reference, SNode method) {
        try {
            Thread.sleep(Integer.parseInt(method.get(2).contents()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

/**
 * Classe AddElement
 * 
 * Permet d'ajouter un élément graphique (rectangle, ovale, image, ou texte) à un conteneur.
 */
class AddElement implements Command {
    Environment env;

    public AddElement(Environment environment) {
        this.env = environment;
    }

    public Reference run(Reference reference, SNode method) {
        Reference ref = env.getReferenceByName(method.get(3).get(0).contents().toLowerCase());
        Reference r = ref.run(method.get(3));

        env.addReference(method.get(0).contents() + "." + method.get(2).contents(), r);

        r.addCommand("add", new AddElement(env));
        r.addCommand("del", new DelElement(env));

        ((GContainer) reference.getReceiver()).addElement((GElement) r.getReceiver());
        reference.addChild(method.get(0).contents() + "." + method.get(2).contents(), r);
        ((GContainer) reference.getReceiver()).repaint();
        return r;
    }
}

/**
 * Classe DelElement
 * 
 * Permet de supprimer un élément graphique d'un conteneur.
 */
class DelElement implements Command {
    Environment env;

    public DelElement(Environment environment) {
        this.env = environment;
    }

    public Reference run(Reference reference, SNode method) {
        Reference ref = env.getReferenceByName(method.get(2).contents());
        ((GContainer) reference.getReceiver()).removeElement((GElement) ref.getReceiver());
        Map<String, Reference> children = ref.getChildren();
        children.forEach((key, value) -> {
            this.env.removeReference(key);
            ((GContainer) reference.getReceiver()).removeElement((GElement) value.getReceiver());
        });
        this.env.removeReference(method.get(2).contents());
        reference.removeChild(method.get(2).contents());
        ((GContainer) reference.getReceiver()).repaint();
        return null;
    }
}

/**
 * Classe NewImage
 * 
 * Permet de créer une nouvelle image graphique.
 */
class NewImage implements Command {
    public Reference run(Reference reference, SNode method) {
        try {
            BufferedImage img = ImageIO.read(new File(method.get(2).contents()));
            GImage i = new GImage(img);
            Reference ref = new Reference(i);
            ref.addCommand("translate", new Translate());
            ref.addCommand("setDim", new SetDim());
            return ref;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

/**
 * Classe NewString
 * 
 * Permet de créer un nouvel élément texte graphique.
 */
class NewString implements Command {
    public Reference run(Reference reference, SNode method) {
        GString s = new GString(method.get(2).contents());
        Reference ref = new Reference(s);
        ref.addCommand("translate", new Translate());
        ref.addCommand("setColor", new SetColor());
        return ref;
    }
}