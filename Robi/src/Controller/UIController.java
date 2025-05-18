package Controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import ihm.Message;
import ihm.Robi_client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class UIController {

	@FXML
	private TextField addresse_field;

	@FXML
	private TextField port_field;

	@FXML
	private Button connexion_btn;

	@FXML
	private Button deconnexion_btn;

	@FXML
	private TextField cmd_field;

	@FXML
	private Button envoi_cmd_btn;

	@FXML
	private Button choix_bloc_btn;

	@FXML
	private Button choix_pas_btn;

	@FXML
	private Button exec_btn;

	@FXML
	private Button clear_canva_btn;

	@FXML
	public ImageView imageView;

	private Robi_client client;

	@FXML
	private ListView<String> message_list; // Nouveau champ pour le ListView

	private ObservableList<String> messages; // Liste observable pour les messages

	@FXML
	private TextArea environment_area;

	/**
	 * Initialise les composants de l'interface utilisateur.
	 */
	@FXML
	public void initialize() {
		messages = FXCollections.observableArrayList();
		message_list.setItems(messages);
		message_list.scrollTo(messages.size() - 1);

		addresse_field.setText("localhost");
		port_field.setText("8000");

		imageView.setImage(null);

		// Initialisation de la liste observable et liaison avec le ListView
		deconnexion_btn.setDisable(true);
		cmd_field.setDisable(true);
		envoi_cmd_btn.setDisable(true);
		choix_bloc_btn.setDisable(true);
		choix_pas_btn.setDisable(true);
		message_list.setDisable(true);
		exec_btn.setDisable(true);
	}

	/**
	 * Décode une chaîne Base64 en une image.
	 * 
	 * @param received La chaîne Base64 à décoder.
	 * @return L'image décodée ou null en cas d'erreur.
	 */
	public Image decodeBase64ToImage(String received) {
		try {
			byte[] imageBytes = Base64.getDecoder().decode(received);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
			return new Image(bis);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Ajoute un message à la liste des messages affichés.
	 * 
	 * @param message Le message à ajouter.
	 */
	public void addMessage(String message) {
		messages.add(message);
		message_list.scrollTo(messages.size() - 1); // Scroll automatique vers le dernier message
	}

	/**
	 * Gère le clic sur le bouton "Connexion" pour établir une connexion au serveur.
	 */
	@FXML
	private void connexion_btn_clic() {
		try {
			// Récupération des informations de connexion
			String adresse = addresse_field.getText();
			int port = Integer.parseInt(port_field.getText());

			// Initialisation du client
			client = new Robi_client(port, adresse, this);

			String msg = "hello";
			String envoie = Message.toJson(new Message("connexion", msg));
			client.send(envoie);

			client.startListening();

			addresse_field.setDisable(true);
			port_field.setDisable(true);
			connexion_btn.setDisable(true);
			deconnexion_btn.setDisable(false);
			cmd_field.setDisable(false);
			envoi_cmd_btn.setDisable(false);
			choix_bloc_btn.setDisable(false);
			choix_pas_btn.setDisable(false);
		} catch (IOException e) {
			afficherPopupErreur("La Connexion au Serveur a échoué!");
			e.printStackTrace();
		}
	}

	/**
	 * Gère le clic sur le bouton "Déconnexion" pour fermer la connexion au serveur.
	 */
	@FXML
	private void deconnexion_btn_clic() {
		try {
			// Déconnexion du client
			if (client != null) {
				String msg = "Déconnexion";
				String envoie = Message.toJson(new Message("Déconnexion", msg));
				client.send(envoie);
				client.close();
				client = null;
				addresse_field.setDisable(false);
				port_field.setDisable(false);
				connexion_btn.setDisable(false);
				deconnexion_btn.setDisable(true);
				cmd_field.setDisable(true);
				envoi_cmd_btn.setDisable(true);
				choix_bloc_btn.setDisable(true);
				choix_pas_btn.setDisable(true);
				exec_btn.setDisable(true);
				imageView.setImage(null);

				messages.clear();
			}
		} catch (IOException e) {
			afficherPopupErreur("La Déconnexion au Serveur a échoué!");
			e.printStackTrace();
		}
	}

	/**
	 * Gère le clic sur le bouton "Envoyer commande" pour envoyer une commande au
	 * serveur.
	 */
	@FXML
	private void envoi_cmd_on_clic() {
		String commande = cmd_field.getText();
		try {
			System.out.println("Premier s ici avant covesion : " + commande);

			char[] c = commande.toCharArray(); // Convertir la chaîne en tableau de caractères
			StringBuilder result = new StringBuilder(); // StringBuilder pour reconstruire la chaîne

			boolean premier = true;
			int cpt = 0;
			for (int i = 0; i < c.length; i++) {
				if(c[i] == '(') {
					cpt += 1;
				}
				if(c[i] == ')') {
					cpt -= 1;
				}
				if(cpt == 0) {
					premier = true;
				}
				if (c[i] == '\"') {
					if (premier) {
						result.append('<');
						premier = false;
					} else {
						result.append('>');
					}
				} else {
					result.append(c[i]); // Ajouter le caractère tel quel
				}
			}

			// Convertir le StringBuilder en String
			commande = result.toString();

			System.out.println("Apres conversion :" + commande);

			String envoie = Message.toJson(new Message("typec", commande));
			client.send(envoie);
			messages.clear();
		} catch (IOException e) {
			afficherPopupErreur("Echec de l'envoi de la commande!");
			e.printStackTrace();
		}
	}

	/**
	 * Gère le clic sur le bouton "Mode par Bloc" pour activer le mode bloc.
	 */
	@FXML
	private void mode_bloc_choisi() {
		String mode = "bloc";
		try {
			String envoie = Message.toJson(new Message("Mode", mode));
			client.send(envoie);
			cmd_field.setDisable(true);
			envoi_cmd_btn.setDisable(true);
			choix_bloc_btn.setDisable(true);
			choix_pas_btn.setDisable(true);
			exec_btn.setDisable(false);
		} catch (IOException e) {
			afficherPopupErreur("Echec du choix du mode!");
			e.printStackTrace();
		}
	}

	/**
	 * Gère le clic sur le bouton "Mode par pas" pour activer le mode pas à pas.
	 */
	@FXML
	private void mode_pas_choisi() {
		String mode = "pas";
		try {
			String envoie = Message.toJson(new Message("Mode", mode));
			client.send(envoie);
			cmd_field.setDisable(true);
			envoi_cmd_btn.setDisable(true);
			choix_bloc_btn.setDisable(true);
			choix_pas_btn.setDisable(true);
			exec_btn.setDisable(false);
		} catch (IOException e) {
			afficherPopupErreur("Echec du choix du mode!");
			e.printStackTrace();
		}
	}

	/**
	 * Gère le clic sur le bouton "Exécution" pour demander l'exécution d'une
	 * commande.
	 */
	@FXML
	private void demande_exec_clic() {
		String msg = "execution";
		try {
			String envoie = Message.toJson(new Message("Execution", msg));
			client.send(envoie);
			cmd_field.setDisable(false);
			envoi_cmd_btn.setDisable(false);
			choix_bloc_btn.setDisable(false);
			choix_pas_btn.setDisable(false);
		} catch (IOException e) {
			afficherPopupErreur("Echec d'envoi de demande d'éxécution!");
			e.printStackTrace();
		}
	}

	/**
	 * Affiche une fenêtre popup avec un message et un type d'alerte donné.
	 * 
	 * @param message Le message à afficher.
	 * @param type    Le type d'alerte (erreur, information, etc.).
	 */
	private void afficherPopup(String message, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle("Erreur");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.setResizable(true);
		alert.showAndWait();
	}

	/**
	 * Affiche une fenêtre popup d'erreur avec un message donné.
	 * 
	 * @param message Le message d'erreur à afficher.
	 */
	private void afficherPopupErreur(String message) {
		this.afficherPopup(message, AlertType.ERROR);
	}

	/**
	 * Gère la sélection d'un fichier et envoie son contenu au serveur.
	 */
	@FXML
	private void select_file() {
		// Ouvrir un sélecteur de fichiers
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Sélectionner un fichier");
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			try {
				// Lire le contenu du fichier
				String fileContent = Files.readString(selectedFile.toPath());
				System.out.println("Premier s ici avant covesion : " + fileContent);

				char[] c = fileContent.toCharArray(); // Convertir la chaîne en tableau de caractères
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
					if (c[i] == '\"') {
						if (premier) {
							result.append('<');
							premier = false;
						} else {
							result.append('>');
						}
					} else {
						result.append(c[i]); // Ajouter le caractère tel quel
					}
				}

				// Convertir le StringBuilder en String
				fileContent = result.toString();

				System.out.println("Apres conversion :" + fileContent);

				String envoie = Message.toJson(new Message("fichier", fileContent));

				// Envoyer le contenu du fichier sous forme de chaîne
				if (client != null) {
					client.send(envoie);
				} else {
					afficherPopupErreur("Client non connecté. Impossible d'envoyer le fichier.");
				}
			} catch (IOException e) {
				afficherPopupErreur("Erreur lors de la lecture du fichier.");
				e.printStackTrace();
			}
		} else {
			afficherPopupErreur("Aucun fichier sélectionné.");
		}
	}

	/**
	 * Met à jour l'affichage de l'environnement dans l'interface utilisateur.
	 * 
	 * @param environment La description de l'environnement à afficher.
	 */
	public void updateEnvironment(String environment) {
		environment_area.clear();
		environment_area.setText(environment);
	}

}