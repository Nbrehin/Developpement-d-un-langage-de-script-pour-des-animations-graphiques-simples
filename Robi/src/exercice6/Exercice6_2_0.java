package exercice6;

import java.awt.Color;

/*
	(space setColor black)  
	(robi setColor yellow) 
	(space sleep 2000) 
	(space setColor white)  
	(space sleep 1000) 	
	(space add robi (GRect new))
	(robi setColor green)
	(robi translate 100 50)
	(space del robi)
	(robi setColor red)		  
	(space sleep 1000)
	(robi translate 100 0)
	(space sleep 1000)
	(robi translate 0 50)
	(space sleep 1000)
	(robi translate -100 0)
	(space sleep 1000)
	(robi translate 0 -40) ) 
	
	
(space add robi (rect.class new))
(robi translate 130 50)
(robi setColor yellow)
(space add momo (oval.class new))
(momo setColor red)
(momo translate 80 80)
(space add pif (image.class new alien.gif))
(pif translate 100 0)
(space add hello (label.class new "Hello world"))
(hello translate 10 10)
(hello setColor black)
(space del robi)

*/
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import graphicLayer.GBounded;
import graphicLayer.GContainer;
import graphicLayer.GElement;
import graphicLayer.GImage;
import graphicLayer.GOval;
import graphicLayer.GRect;
import graphicLayer.GSpace;
import graphicLayer.GString;
import stree.parser.SNode;
import stree.parser.SParser;
import tools.Tools;

/**
 * La classe Clear permet de supprimer l'entièreté des objects de type GElement
 * contenus dans l'environnement
 */
class Clear implements Command {
	Environment env;

	public Clear(Environment environment) {
		this.env = environment;
	}

	/**
	 * Fonction récursive qui permet de supprimer les enfants de l'object que l'on
	 * veut supprimer
	 * 
	 * @param nom
	 */
	void recurs(String nom) {
		Reference ref = env.getReferenceByName(nom);
		// Vérifie d s'il y a des enfants
		if (!ref.getChildren().isEmpty()) {
			Map<String, Reference> children = ref.getChildren();

			// Appelle récursivement sur chaque enfant
			children.forEach((key, value) -> {
				recurs(key);
				// supprime la référence de l'enfant dans l'objet GContainer
				((GContainer) ref.getReceiver()).removeElement((GElement) value.getReceiver());
			});
		}
		// Supprime la référence dans l'environment
		if (env.variables.containsKey(nom) && nom.contains("space.")) {
			this.env.removeReference(nom);
		}
	}

	public Reference run(Reference reference, SNode method) {
		recurs(method.get(0).contents());
		reference.clearChildren();
		((GSpace) reference.getReceiver()).repaint();
		return null;
	}
}

/**
 * La classe NewElement contient la commande qui initialise un objet de type
 * GElement (soit un oval, soit un rectangle)
 */
class NewElement implements Command {
	@SuppressWarnings("unchecked")
	public Reference run(Reference reference, SNode method) {
		GElement e;
		try {
			e = ((Class<GElement>) reference.getReceiver()).getDeclaredConstructor().newInstance();
			Reference ref = new Reference(e);
			ref.addCommand("setColor", new SetColor());
			ref.addCommand("translate", new Translate());
			ref.addCommand("setDim", new SetDim());
			ref.addCommand("addScript", new AddScript(null));
			return ref;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}

/**
 * La classe Lecture contient la commande contenue dans chaque script. Elle
 * permet de lire et de faire exécuter les scripts
 */
class Lecture implements Command {
	Environment environment;

	public Lecture(Environment env) {
		environment = env;
	}

	public Reference run(Reference receiver, SNode method) {
		String[] param;
		// chercher le script correspondant
		String arg2 = method.get(1).contents();
		SNode script = receiver.getScriptByName(arg2);

		// remplacer les paramètres
		HashMap<String, String> parametres = new HashMap<String, String>();
		parametres.put(script.get(0).get(0).contents(), method.get(0).contents());
		for (int i = 2; i < method.size(); i++) {
			parametres.put(script.get(0).get(i - 1).contents(), method.get(i).contents());
		}
		int cond = 1;
		while (cond < script.size()) {
			SNode cmd = script.get(cond);
			String new_cmd = "( ";

			// parcours du SNode de cmd
			for (int i = 0; i < cmd.size(); i++) {
				// traitement des différents cas
				if (cmd.get(i).hasChildren()) {
					new_cmd += "( ";
					for (int j = 0; j < cmd.get(i).size(); j++) {
						if (parametres.containsKey(cmd.get(i).get(j).contents()))
							new_cmd += parametres.get(cmd.get(i).get(j).contents()) + " ";
						else
							new_cmd += cmd.get(i).get(j).contents() + " ";
					}
					new_cmd += ")";
				} else {
					if (cmd.get(i).contents().contains(".")) {
						String nom_point = " ";
						param = cmd.get(i).contents().split("\\.");
						for (int k = 0; k < param.length; k++) {
							String param1 = param[k];
							nom_point += parametres.get(param1) + ".";
						}
						new_cmd += nom_point.substring(0, nom_point.length() - 1) + " ";
					} else {
						if (parametres.containsKey(cmd.get(i).contents()))
							new_cmd += parametres.get(cmd.get(i).contents()) + " ";
						else
							new_cmd += cmd.get(i).contents() + " ";
					}
				}
			}
			new_cmd += ")";
			SParser<SNode> parser = new SParser<>();

			// la liste de SNodes et remplies avec les expressions parsées
			List<SNode> compiled = null;
			try {
				compiled = parser.parse(new_cmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// execution des s-expressions (sous forme de SNode maintenant) se trouvant dans
			// la liste de SNode à executer
			Iterator<SNode> itor = compiled.iterator();
			while (itor.hasNext()) {
				new Interpreter().compute(environment, itor.next());
			}

			cond++;

		}
		return null;
	}
}

/**
 * La classe AddScript contient la commande pour ajouter un script à un objet
 * Reference.
 */
class AddScript implements Command {
	Environment environment;

	public AddScript(Environment env) {
		environment = env;
	}

	public Reference run(Reference receiver, SNode method) {

		String arg3 = method.get(2).contents();
		SNode arg4 = method.get(3);

		// ajouter la nouvelle commande
		receiver.addCommand(arg3, new Lecture(environment));

		// ajout du script pour le stocker
		receiver.addScript(arg3, arg4);

		return null;
	}
}

/**
 * La classe SetColor contient la commande pour modifier la couleur d'un
 * GElement ou de space
 */
class SetColor implements Command {
	public Reference run(Reference reference, SNode method) {
		Color c = Tools.getColorByName(method.get(2).contents());
		((GElement) reference.getReceiver()).setColor(c);
		return (Reference) reference;
	}
}

/**
 * La classe Translate contient la commande pour déplacer un objet de type
 * GElement.
 */
class Translate implements Command {
	public Reference run(Reference reference, SNode method) {
		Point p = new Point(Integer.parseInt(method.get(2).contents()), Integer.parseInt(method.get(3).contents()));

		((GElement) reference.getReceiver()).translate(p);
		return (Reference) reference;
	}
}

/**
 * La classe SetDim contient la commande pour modifier la taille d'un objet de
 * type GBounded ou GSpace
 */
class SetDim implements Command {
	public Reference run(Reference reference, SNode method) {
		Dimension d = new Dimension(Integer.parseInt(method.get(2).contents()),
				Integer.parseInt(method.get(3).contents()));
		if (method.get(0).contents().equals("space")) {
			((GSpace) reference.getReceiver()).changeWindowSize(d);
		} else {
			((GBounded) reference.getReceiver()).setDimension(d);
		}
		return (Reference) reference;
	}
}

/**
 * La classe Sleep contient la commande pour arrêter les éxécutions un temps
 * donné.
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
 * La classe AddElement contient la commande pour ajouter un objet (GElement,
 * GString ou GImage) dans un GContainer.
 */
class AddElement implements Command {
	Environment env;

	public AddElement(Environment environment) {
		this.env = environment;
	}

	public Reference run(Reference reference, SNode method) {
		// création de l'élément
		Reference ref = env.getReferenceByName(method.get(3).get(0).contents().toLowerCase());
		Reference r = ref.run(method.get(3));

		env.addReference(method.get(0).contents() + "." + method.get(2).contents(), r);

		r.addCommand("add", new AddElement(env));
		r.addCommand("del", new DelElement(env));
		r.addCommand("addScript", new AddScript(env));

		// ajoute l'élément crée dans celui qui le contiens
		((GContainer) reference.getReceiver()).addElement((GElement) r.getReceiver());

		reference.addChild(method.get(0).contents() + "." + method.get(2).contents(), r);
		((GContainer) reference.getReceiver()).repaint();
		return r;
	}
}

/**
 * La classe DelElement contient la commande pour retirer un objet (GElement,
 * GString ou GImage) d'un GContainer.
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
 * La classe NewImage contient la commande pour ajouter un objet GImage à un
 * GContainer
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
 * La classe NewString contient la commande pour ajouter un objet GString à un
 * objet de type GContainer
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

/**
 * Classe Exercice6_2_0 instancie toutes les commandes accessibles pour
 * l'utilisateur dans son constructeur, et lui laisse l'opportunité d'utiliser
 * celles qui lui plaisent.
 */
public class Exercice6_2_0 {
	// Une seule variable d'instance
	Environment environment = new Environment();

	/**
	 * Exercice6_2_0 est la seule fonction appelée par le main, elle instancie
	 * toutes les classes.
	 */
	public Exercice6_2_0() {
		GSpace space = new GSpace("Exercice 6", new Dimension(500, 500));
		space.open();

		Reference spaceRef = new Reference(space);
		Reference rectClassRef = new Reference(GRect.class);
		Reference ovalClassRef = new Reference(GOval.class);
		Reference imageClassRef = new Reference(GImage.class);
		Reference stringClassRef = new Reference(GString.class);

		spaceRef.addCommand("setColor", new SetColor());
		spaceRef.addCommand("sleep", new Sleep());
		spaceRef.addCommand("clear", new Clear(environment));
		spaceRef.addCommand("addScript", new AddScript(environment));
		spaceRef.addCommand("add", new AddElement(environment));
		spaceRef.addCommand("del", new DelElement(environment));
		spaceRef.addCommand("setDim", new SetDim());

		rectClassRef.addCommand("new", new NewElement());
		ovalClassRef.addCommand("new", new NewElement());
		imageClassRef.addCommand("new", new NewImage());
		stringClassRef.addCommand("new", new NewString());

		environment.addReference("space", spaceRef);
		environment.addReference("rect.class", rectClassRef);
		environment.addReference("oval.class", ovalClassRef);
		environment.addReference("image.class", imageClassRef);
		environment.addReference("label.class", stringClassRef);

		this.mainLoop();
	}

	/**
	 * mainLoop est la fonction qui détecte les commande entrées dans la console
	 * pour les interpréter.
	 */
	private void mainLoop() {
		while (true) {
			// prompt
			System.out.print("> ");
			// lecture d'une serie de s-expressions au clavier (return = fin de la serie)
			String input = Tools.readKeyboard();
			// creation du parser
			SParser<SNode> parser = new SParser<>();
			// création de la liste des SNodes à compiler
			List<SNode> compiled = null;
			try {
				compiled = parser.parse(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// execution des s-expressions (sous forme de SNode maintenant) se trouvant dans
			// la liste de SNode à executer
			Iterator<SNode> itor = compiled.iterator();
			while (itor.hasNext()) {
				new Interpreter().compute(environment, itor.next());
			}
		}
	}

	/**
	 * Fonction Main, crée une nouvelle instance de la classe Exercice6_2_0.
	 */
	public static void main(String[] args) {
		new Exercice6_2_0();
	}

}