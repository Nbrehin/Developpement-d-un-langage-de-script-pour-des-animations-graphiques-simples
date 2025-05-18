package ihm5;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import graphicLayer.GContainer;
import graphicLayer.GElement;
import graphicLayer.GImage;
import graphicLayer.GOval;
import graphicLayer.GRect;
import graphicLayer.GSpace;
import graphicLayer.GString;
import ihm5.NewElement;
import ihm5.NewImage;
import ihm5.NewString;
import stree.parser.SNode;
import stree.parser.SParser;

/**
 * Classe NewElement
 * 
 * Cette classe implémente la commande de création d'un nouvel élément
 * graphique. Elle permet de créer des éléments graphiques tels que des
 * rectangles, des ovales, etc.
 * 
 * Algorithme : 1. Instancie dynamiquement un nouvel élément graphique à partir
 * de la classe référencée. 2. Associe des commandes à cet élément (setColor,
 * translate, setDim). 3. Retourne une référence à l'élément créé.
 */
class NewElement implements Command {
	@SuppressWarnings("unchecked")
	public Reference run(Reference reference, SNode method) {
		GElement e;
		try {
			e = ((Class<GElement>) reference.getReceiver()).getDeclaredConstructor().newInstance();
			Reference ref = new Reference(new Element(10, 10, 100, 100, method.get(0).contents(), "blue",method.get(0).contents()),
					method.get(0).contents());
			ref.addCommand("setColor", new SetColor());
			ref.addCommand("translate", new Translate());
			ref.addCommand("setDim", new SetDim());
			return ref;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}

/**
 * Classe SetColor
 * 
 * Cette classe implémente la commande de changement de couleur d'un élément
 * graphique. Elle permet de modifier la couleur d'un espace graphique ou d'un
 * élément graphique.
 * 
 * Algorithme : 1. Récupère la couleur spécifiée dans la commande. 2. Applique
 * la couleur à l'élément ou à l'espace graphique référencé.
 */
class SetColor implements Command {
	public Reference run(Reference reference, SNode method) {
		((Element) reference.getReceiver()).setCouleur(method.get(2).contents());
		return (Reference) reference;
	}
}

/**
 * Classe Translate
 * 
 * Cette classe implémente la commande de translation d'un élément graphique.
 * Elle permet de déplacer un élément graphique selon un vecteur donné.
 * 
 * @param reference La référence de l'élément à déplacer.
 * @param method    La commande contenant les coordonnées de translation.
 */
class Translate implements Command {
	public Reference run(Reference reference, SNode method) {
		Point p = new Point(Integer.parseInt(method.get(2).contents()), Integer.parseInt(method.get(3).contents()));
		((Element) reference.getReceiver()).setX(Integer.parseInt(method.get(2).contents()));
		((Element) reference.getReceiver()).setX(Integer.parseInt(method.get(3).contents()));
		return (Reference) reference;
	}
}

/**
 * Classe SetDim
 * 
 * Cette classe implémente la commande de redimensionnement d'un élément
 * graphique. Elle permet de modifier les dimensions d'un espace graphique ou
 * d'un élément graphique.
 * 
 * @param reference La référence de l'élément à redimensionner.
 * @param method    La commande contenant les nouvelles dimensions.
 */
class SetDim implements Command {
	public Reference run(Reference reference, SNode method) {
		Dimension d = new Dimension(Integer.parseInt(method.get(2).contents()),
				Integer.parseInt(method.get(3).contents()));
		((Element) reference.getReceiver()).setW(Integer.parseInt(method.get(2).contents()));
		((Element) reference.getReceiver()).setH(Integer.parseInt(method.get(3).contents()));
		return (Reference) reference;
	}
}

/**
 * Classe Sleep
 * 
 * Cette classe implémente la commande de pause (attente) dans l'exécution. Elle
 * permet de suspendre l'exécution pendant une durée spécifiée.
 * 
 * @param reference La référence de l'élément (non utilisée ici).
 * @param method    La commande contenant la durée de la pause en millisecondes.
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
 * Cette classe implémente la commande d'ajout d'un nouvel élément graphique à
 * un conteneur. Elle permet d'ajouter dynamiquement des éléments graphiques à
 * un espace ou à un autre conteneur.
 * 
 * Variables d'instance : - env : L'environnement contenant les références des
 * éléments graphiques.
 * 
 * Algorithme : 1. Récupère la référence de l'élément à ajouter. 2. Exécute la
 * commande pour créer l'élément. 3. Ajoute l'élément au conteneur référencé. 4.
 * Met à jour l'environnement avec la nouvelle référence.
 */
class AddElement implements Command {
	Environment env;

	public AddElement(Environment environment) {
		this.env = environment;
	}

	public Reference run(Reference reference, SNode method) {
		Reference ref = env.getReferenceByName(method.get(3).get(0).contents().toLowerCase());
		Reference r = ref.run(method.get(3));
		r.setNom(method.get(0).contents() + "." + method.get(2).contents());
		((Element)r.getReceiver()).setNom(method.get(0).contents() + "." + method.get(2).contents());
		env.addReference(method.get(0).contents() + "." + method.get(2).contents(), r);
		r.addCommand("add", new AddElement(env));
		r.addCommand("del", new DelElement(env));
		return r;
	}
}

/**
 * Classe DelElement
 * 
 * Cette classe implémente la commande de suppression d'un élément graphique
 * d'un conteneur. Elle permet de retirer dynamiquement des éléments graphiques
 * d'un espace ou d'un autre conteneur.
 * 
 * Variables d'instance : - env : L'environnement contenant les références des
 * éléments graphiques.
 * 
 * Algorithme : 1. Récupère la référence de l'élément à supprimer. 2. Supprime
 * l'élément du conteneur référencé. 3. Supprime les références associées dans
 * l'environnement. 4. Met à jour l'affichage du conteneur.
 */
class DelElement implements Command {
	Environment env;

	public DelElement(Environment environment) {
		this.env = environment;
	}

	public Reference run(Reference reference, SNode method) {
		Reference ref = env.getReferenceByName(method.get(2).contents());
		((GSpace) reference.getReceiver()).removeElement((GElement) ref.getReceiver());
		Map<String, Reference> children = ref.getChildren();
		children.forEach((key, value) -> {
			this.env.removeReference(key, value);
			((GContainer) reference.getReceiver()).removeElement((GElement) value.getReceiver());
		});
		this.env.removeReference(method.get(2).contents(), ref);
		reference.clearChildren();
		((GContainer) reference.getReceiver()).repaint();
		return null;
	}
}

/**
 * Classe NewImage
 * 
 * Cette classe implémente la commande de création d'une nouvelle image
 * graphique. Elle permet de charger une image à partir d'un fichier et de
 * l'ajouter à l'espace graphique.
 * 
 * @param reference La référence de la classe GImage.
 * @param method    La commande contenant le chemin du fichier image.
 * 
 *                  Algorithme : 1. Charge l'image depuis le fichier spécifié.
 *                  2. Crée une instance de GImage avec l'image chargée. 3.
 *                  Associe des commandes à l'image (translate, setDim). 4.
 *                  Retourne une référence à l'image créée.
 */
class NewImage implements Command {
	public Reference run(Reference reference, SNode method) {
		try {
			BufferedImage img = ImageIO.read(new File(method.get(2).contents()));
			GImage i = new GImage(img);
			Reference ref = new Reference(new Element(10, 10, 100, 100, "image", null, method.get(0).contents()), method.get(0).contents());
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
 * Cette classe implémente la commande de création d'une nouvelle chaîne
 * graphique. Elle permet d'ajouter une chaîne de caractères à l'espace
 * graphique.
 * 
 * @param reference La référence de la classe GString.
 * @param method    La commande contenant le texte de la chaîne.
 * 
 *                  Algorithme : 1. Crée une instance de GString avec le texte
 *                  spécifié. 2. Associe des commandes à la chaîne (translate,
 *                  setColor). 3. Retourne une référence à la chaîne créée.
 */
class NewString implements Command {
	public Reference run(Reference reference, SNode method) {
		GString s = new GString(method.get(2).contents());
		Reference ref = new Reference(new Element(10, 10, 100, 100, "label", "blue", method.get(0).contents()), method.get(0).contents());
		ref.addCommand("translate", new Translate());
		ref.addCommand("setColor", new SetColor());
		return ref;
	}
}

/**
 * Classe Robi_serveur
 * 
 * Cette classe représente un serveur TCP qui gère des connexions clients pour
 * manipuler un espace graphique. Elle permet de recevoir des commandes, de les
 * exécuter et de renvoyer des résultats sous forme d'images ou de messages.
 * 
 * Variables d'instance : - serverSocket : Socket d'écoute pour accepter les
 * connexions clients. - clientSocket : Socket pour communiquer avec un client.
 * - input : Flux d'entrée pour recevoir des données du client. - output : Flux
 * de sortie pour envoyer des données au client. - port : Port d'écoute du
 * serveur (par défaut 8000). - cmds : Commandes reçues sous forme de chaîne. -
 * environment : Environnement contenant les références des éléments graphiques.
 * - res : Résultat temporaire utilisé pour construire des commandes.
 * 
 * Dépendances : - Utilise les classes de la bibliothèque graphique (GSpace,
 * GElement, etc.). - Dépend de la classe Environment pour gérer les références
 * des éléments. - Utilise les classes Message et Interpreter pour la
 * communication et l'exécution.
 */
public class Robi_serveur {

	/**
	 * Socket d'écoute du serveur.
	 */
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private int port = 8000;
	private String cmds;
	private Environment environment = new Environment();
	private String res;

	/**
	 * Constructeur du serveur TCP initialisant la socket d'écoute sur le port.
	 * Configure également l'environnement graphique et les commandes associées.
	 */
	public Robi_serveur() {
		// Initialisation de l'espace graphique

		// Configuration des références et des commandes
		Reference spaceRef = new Reference(new Element(10, 10, 200, 200, "rect", "white", "space"), "space");
		Reference rectClassRef = new Reference(GRect.class, "rectClassRef");
		Reference ovalClassRef = new Reference(GOval.class, "ovalClassRef");
		Reference imageClassRef = new Reference(GImage.class, "imageClassRef");
		Reference stringClassRef = new Reference(GString.class, "stringClassRef");

		spaceRef.addCommand("setColor", new SetColor());
		spaceRef.addCommand("sleep", new Sleep());
		spaceRef.addCommand("add", new AddElement(environment));
		spaceRef.addCommand("del", new DelElement(environment));
		spaceRef.addCommand("setDim", new SetDim());

		rectClassRef.addCommand("new", new NewElement());
		ovalClassRef.addCommand("new", new NewElement());
		imageClassRef.addCommand("new", new NewImage());
		stringClassRef.addCommand("new", new NewString());

		// Ajout des références à l'environnement
		environment.addReference("space", spaceRef);
		environment.addReference("rect.class", rectClassRef);
		environment.addReference("oval.class", ovalClassRef);
		environment.addReference("image.class", imageClassRef);
		environment.addReference("label.class", stringClassRef);

		// Initialisation de la socket d'écoute
		try {
			this.serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			System.err.println("Erreur serveur.");
			e.printStackTrace();
		}
	}

	/**
	 * Convertit une image en chaîne Base64.
	 * 
	 * @param image L'image à convertir.
	 * @return La chaîne Base64 représentant l'image.
	 */
	private static String imageToBase64(BufferedImage image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "PNG", baos);
			return Base64.getEncoder().encodeToString(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Génère une image Base64 représentant l'état actuel de l'espace.
	 * 
	 * @param space L'espace graphique à convertir.
	 * @return La chaîne Base64 de l'image.
	 */
	public String spaceToImageBase64(GSpace space) {
		BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		space.paint(g2d);
		g2d.dispose();
		return imageToBase64(image);
	}

	/**
	 * Exécute les commandes en mode bloc.
	 * 
	 * Algorithme : 1. Parse les commandes reçues en une liste de nœuds SNode. 2.
	 * Exécute chaque nœud en utilisant un interpréteur. 3. Génère une image de
	 * l'état final de l'espace graphique. 4. Envoie l'image et l'état de
	 * l'environnement au client.
	 */
	public void bloc() {
		System.out.println("ok1");
		SParser<SNode> parser = new SParser<>();
		List<SNode> compiled = null;

		try {
			compiled = parser.parse(this.cmds); // Compilation des commandes
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Iterator<SNode> itor = compiled.iterator();
		try {
			Object reception = this.input.readObject(); // Lecture de la commande
			if (reception instanceof String) {
				Message MBLOC = Message.fromJson((String) reception);
				if (MBLOC.mess.equalsIgnoreCase("execution")) {
					SNode next = null;
					System.out.println("Début de l'exécution en bloc.");
					String envoie = null;
					while (itor.hasNext()) {
						next = itor.next();
						new Interpreter().compute(environment, next);
						if (next.get(1).contents().equals("add")) {
							envoie = this.environment
									.getReferenceByName(next.get(0).contents() + "." + next.get(2).contents()).getReceiver()
									.toString();
						} else {
							envoie = this.environment.getReferenceByName(next.get(0).contents()).getReceiver()
									.toString();
						}
						String RBLOC = Message.toJson(new Message("mode", "CMD:" + envoie));
						this.output.writeObject(RBLOC);
					}
					System.out.println("Fin de l'exécution en bloc.");
					String environmentTree = environment.environmentToArbre();
					this.environment.printed.clear();
					String environmentMessage = Message.toJson(new Message("Environment", environmentTree));
					this.output.writeObject(environmentMessage);
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parcourt récursivement un nœud pour construire une commande.
	 * 
	 * @param s Le nœud à parcourir.
	 * @param i L'index actuel dans le nœud.
	 * 
	 *          Algorithme : 1. Si l'index atteint la taille du nœud, termine la
	 *          récursion. 2. Si le nœud courant a des enfants, ajoute une
	 *          parenthèse ouvrante, traite les enfants récursivement, puis ajoute
	 *          une parenthèse fermante. 3. Sinon, ajoute le contenu du nœud au
	 *          résultat. 4. Passe au nœud suivant et répète.
	 */
	public void methode(SNode s, int i) {
		if (i == s.size()) {
			return;
		} else {
			if (s.get(i).hasChildren()) {
				this.res += "(";
				methode(s.get(i), 0);
				this.res += ")";
			} else {
				res += s.get(i).contents() + " ";
			}
			i += 1;
			methode(s, i);
			return;
		}
	}

	/**
	 * Exécute les commandes en mode pas à pas.
	 * 
	 * Algorithme : 1. Parse les commandes reçues en une liste de nœuds SNode. 2.
	 * Pour chaque nœud, attend une confirmation du client avant de l'exécuter. 3.
	 * Après chaque exécution, génère une image de l'état actuel de l'espace
	 * graphique. 4. Envoie l'image et l'état de l'environnement au client.
	 */
	public void pas() {
		SParser<SNode> parser = new SParser<>();
		List<SNode> compiled = null;
		try {
			compiled = parser.parse(this.cmds);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Iterator<SNode> itor = compiled.iterator();
		while (itor.hasNext()) {
			Object reception;
			try {
				reception = this.input.readObject();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				return;
			}
			if (reception instanceof String) {
				Message MPAS = Message.fromJson((String) reception);
				System.out.println(MPAS.mess);
				String envoie = null;
				if (MPAS.mess.toLowerCase().equals("execution")) {
					SNode next = itor.next();
					new Interpreter().compute(environment, next);
					if (next.get(1).contents().equals("add")) {
						envoie = this.environment
								.getReferenceByName(next.get(0).contents() + "." + next.get(2).contents()).getReceiver()
								.toString();
					} else {
						envoie = this.environment.getReferenceByName(next.get(0).contents()).getReceiver()
								.toString();
					}

					try {
						String RPASI = Message.toJson(new Message("mode", "CMD:" + envoie));
						String environmentTree = environment.environmentToArbre();
						this.environment.printed.clear();
						String environmentMessage = Message.toJson(new Message("Environment", environmentTree));
						this.output.writeObject(environmentMessage);
						this.output.writeObject(RPASI);
					} catch (IOException e) {
						e.printStackTrace();
					}
					this.res = "(";
					methode(next, 0);
					this.res += ")";
					System.out.println("Commande : " + this.res);
					try {
						String RPAS = Message.toJson(new Message("mode", "MSG:" + this.res));
						this.output.writeObject(RPAS);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("Erreur réception");
				return;
			}
		}
	}

	/**
	 * Lance le serveur et gère les connexions clients.
	 * 
	 * @throws IOException En cas d'erreur réseau.
	 */
	public void startServeur() throws IOException {
		System.out.println("Démarrage du serveur.");
		while (true) {
			try {
				this.clientSocket = serverSocket.accept();
				this.input = new ObjectInputStream(this.clientSocket.getInputStream());
				this.output = new ObjectOutputStream(this.clientSocket.getOutputStream());
				System.out.println("Connexion acceptée");
				boolean connecte = true;
				while (connecte) {
					Object reception = this.input.readObject();
					if (reception instanceof String) {
						Message recu = Message.fromJson((String) reception);
						System.out.println("Réception effectuée : " + recu.mess + ", de type : " + recu.type);

						if (recu.type.equals("Déconnexion")) {
							connecte = false;
							try {
								if (this.input != null) {
									this.input.close();
								}
								if (this.output != null) {
									this.output.close();
								}
								this.clientSocket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							Reference spaceRef = new Reference(new Element(10, 10, 200, 200, "rect", "blue", "space"), "space");
							Reference rectClassRef = new Reference(GRect.class, "rectClassRef");
							Reference ovalClassRef = new Reference(GOval.class, "ovalClassRef");
							Reference imageClassRef = new Reference(GImage.class, "imageClassRef");
							Reference stringClassRef = new Reference(GString.class, "stringClassRef");

							spaceRef.addCommand("setColor", new SetColor());
							spaceRef.addCommand("sleep", new Sleep());
							spaceRef.addCommand("add", new AddElement(environment));
							spaceRef.addCommand("del", new DelElement(environment));
							spaceRef.addCommand("setDim", new SetDim());

							rectClassRef.addCommand("new", new NewElement());
							ovalClassRef.addCommand("new", new NewElement());
							imageClassRef.addCommand("new", new NewImage());
							stringClassRef.addCommand("new", new NewString());

							// Ajout des références à l'environnement
							environment.addReference("space", spaceRef);
							environment.addReference("rect.class", rectClassRef);
							environment.addReference("oval.class", ovalClassRef);
							environment.addReference("image.class", imageClassRef);
							environment.addReference("label.class", stringClassRef);
						}
						if (recu.type.equals("Mode")) {
							if (recu.mess.equals("bloc")) {
								System.out.println("ok0");
								this.bloc();
							} else {
								if (recu.mess.equals("pas")) {
									this.pas();
								}
							}
						} else {
							String script = recu.mess;
							System.out.println("Avant covesion cote serveur  : " + script);

							char[] c = script.toCharArray(); // Convertir la chaîne en tableau de caractères
							StringBuilder result = new StringBuilder(); // StringBuilder pour reconstruire la chaîne

							boolean premier = true;
							int cpt = 0;
							for (int i = 0; i < c.length; i++) {
								if (c[i] == '(') {
									cpt += 1;
								}
								if (c[i] == ')') {
									cpt -= 1;
								}
								if (cpt == 0) {
									premier = true;
								}
								if (c[i] == '<' && premier) {
									result.append('\"');
									premier = false;
								} else {
									if (c[i] == '>') {
										if (c[i + 1] == ')') {
											result.append('\"');
										} else {
											result.append(c[i]);
										}

									} else {
										result.append(c[i]);
									}
								}
							}
							// Convertir le StringBuilder en String
							script = result.toString();

							System.out.println("Apres conversion cote serveur  :" + script);
							this.cmds = script;
							System.out.println(this.cmds);
						}
					} else {
						System.out.println("Erreur réception");
						return;
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Ferme les sockets et libère les ressources.
	 */
	public void close() {
		try {
			if (this.input != null) {
				this.input.close();
			}
			if (this.output != null) {
				this.output.close();
			}
			if (this.clientSocket != null) {
				this.clientSocket.close();
			}
			if (this.serverSocket != null) {
				this.serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Point d'entrée principal du serveur.
	 * 
	 * @param args Les arguments de la ligne de commande.
	 */
	public static void main(String[] args) {
		while (true) {
			try {

				Robi_serveur serveur = new Robi_serveur();
				serveur.startServeur();
				serveur.close();
			} catch (IOException e) {
				System.err.println("Erreur serveur.");
			}
		}
	}
}
