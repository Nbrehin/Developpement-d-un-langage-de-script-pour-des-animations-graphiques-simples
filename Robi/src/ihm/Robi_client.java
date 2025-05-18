package ihm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import Controller.UIController;
import javafx.scene.image.Image;

/**
 * Classe Robi_client
 * 
 * Cette classe représente un client TCP qui se connecte à un serveur pour
 * envoyer et recevoir des données. Elle gère la communication avec le serveur,
 * notamment l'envoi de commandes et la réception de réponses.
 * 
 * Variables d'instance : - adr : Adresse IP du serveur. - socket : Socket de
 * connexion avec le serveur. - output : Flux de sortie pour envoyer des objets
 * au serveur. - input : Flux d'entrée pour recevoir des objets du serveur. - ui
 * : Contrôleur de l'interface utilisateur pour mettre à jour l'affichage.
 * 
 * Dépendances : - Utilise la classe `UIController` pour interagir avec
 * l'interface utilisateur. - Utilise la classe `Message` pour formater les
 * données échangées avec le serveur.
 */
public class Robi_client implements Client {

	// Adresse IP du serveur
	private InetAddress adr;
	// Socket de connexion avec le serveur
	private Socket socket;
	// Flux objets pour la communication
	private ObjectOutputStream output;
	private ObjectInputStream input;
	// Contrôleur de l'interface utilisateur
	private UIController ui;

	/**
	 * Constructeur du client TCP. Initialise la connexion avec le serveur et
	 * configure les flux d'entrée/sortie.
	 * 
	 * @param port         Le port du serveur.
	 * @param adresse_name L'adresse IP ou le nom d'hôte du serveur.
	 * @param pannel       Le contrôleur de l'interface utilisateur.
	 * @throws IOException Si une erreur survient lors de la connexion.
	 */
	public Robi_client(int port, String adresse_name, UIController pannel) throws IOException {
		try {
			adr = InetAddress.getByName(adresse_name);
		} catch (UnknownHostException e) {
			System.err.println("Échec : Adresse inconnue.");
			e.printStackTrace();
			throw e;
		}
		this.socket = new Socket(adr, port);
		System.out.println("Connexion établie avec le serveur " + adresse_name + ":" + port);
		this.output = new ObjectOutputStream(socket.getOutputStream());
		this.input = new ObjectInputStream(socket.getInputStream());
		ui = pannel;
	}

	/**
	 * Ferme la connexion avec le serveur et libère les ressources.
	 * 
	 * @throws IOException Si une erreur survient lors de la fermeture.
	 */
	public void close() throws IOException {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
			if (output != null) {
				output.close();
			}
			if (input != null) {
				input.close();
			}
			System.out.println("Client déconnecté.");
		} catch (IOException e) {
			System.err.println("Erreur lors de la fermeture du client : " + e.getMessage());
		}
	}

	/**
	 * Envoie un message au serveur.
	 * 
	 * @param message L'objet à envoyer (généralement une commande ou un message
	 *                formaté).
	 * @throws IOException Si une erreur survient lors de l'envoi.
	 */
	public void send(Object message) throws IOException {
		try {
			System.out.println("Envoi du message : " + message);
			output.writeObject(message);
			output.flush();
			System.out.println("Message envoyé avec succès.");
		} catch (IOException e) {
			System.err.println("Erreur lors de l'envoi du message : " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Démarre un thread pour écouter les messages du serveur. Les messages reçus
	 * sont traités et affichés dans l'interface utilisateur.
	 */
	public void startListening() {
		new Thread(() -> {
			try {
				while (!socket.isClosed()) {
					try {
						if (input != null) {
							Object received = input.readObject();
							System.out.println("Reçu : " + (String) received);
							Image image;

							if (received instanceof String) {
								Message response = Message.fromJson((String) received);
								if (response.type.equals("Environment")) {
									String environment = response.mess;
									javafx.application.Platform.runLater(() -> ui.updateEnvironment(environment));
								}
								if (response.mess.startsWith("MSG:")) {
									String textM = response.mess.substring(4);
									System.out.println("Message ajouté : " + textM);
									javafx.application.Platform.runLater(() -> ui.addMessage(textM));
								} else if (response.mess.startsWith("IMG:")) {
									String textI = response.mess.substring(4);
									if ((image = ui.decodeBase64ToImage((String) textI)) != null) {
										System.out.println("Message est bien une image");
										javafx.application.Platform.runLater(() -> {
											ui.imageView.setImage(image);
											System.out.println("Affichage de l'image");
										});
										System.out.println("Image affichée");
									}
								}
							}
						} else {
							System.out.println("Erreur réception image");
						}
					} catch (ClassNotFoundException e) {
						System.err.println("Erreur de fonctionnement de classe : " + e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						System.err.println("Connexion au serveur perdue : " + e.getMessage());
						break;
					}
				}
			} finally {
				try {
					if (input != null)
						input.close();
					if (output != null)
						output.close();
					if (socket != null)
						socket.close();
					System.out.println("Ressources fermées après déconnexion");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Vérifie si la connexion avec le serveur est fermée.
	 * 
	 * @return true si la connexion est fermée, false sinon.
	 */
	public boolean isDown() {
		return socket.isInputShutdown() || socket.isOutputShutdown();
	}
}