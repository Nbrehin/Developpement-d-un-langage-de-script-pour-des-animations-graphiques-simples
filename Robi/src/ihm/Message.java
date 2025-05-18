package ihm;

/**
 * Classe Message
 * 
 * Cette classe représente un message échangé entre le client et le serveur.
 * Elle permet de structurer les données sous forme de type et de contenu
 * (message), et fournit des méthodes pour convertir un message en JSON ou pour
 * créer un message à partir d'une chaîne JSON.
 * 
 * Variables d'instance : - type : Le type du message (par exemple, "connexion",
 * "commande", "erreur"). - mess : Le contenu du message (par exemple, une
 * commande ou une réponse).
 * 
 * Dépendances : - Cette classe est utilisée par les classes `Robi_client` et
 * `Robi_serveur` pour formater les données échangées.
 */
public class Message {

	// Type du message (exemple : "connexion", "commande", "erreur")
	public String type = null;
	// Contenu du message (exemple : une commande ou une réponse)
	public String mess = null;

	/**
	 * Constructeur par défaut. Initialise un message vide.
	 */
	public Message() {
	}

	/**
	 * Constructeur avec paramètres.
	 * 
	 * @param type Le type du message.
	 * @param mess Le contenu du message.
	 */
	public Message(String type, String mess) {
		this.type = type;
		this.mess = mess;
	}

	/**
	 * Convertit un objet Message en une chaîne JSON.
	 * 
	 * @param m L'objet Message à convertir.
	 * @return Une chaîne JSON représentant le message.
	 */
	public static String toJson(Message m) {
		return "{\"type\":\"" + m.type + "\",\"mess\":\"" + m.mess + "\"}";
	}

	/**
	 * Crée un objet Message à partir d'une chaîne JSON.
	 * 
	 * @param s La chaîne JSON représentant un message.
	 * @return Un objet Message correspondant à la chaîne JSON, ou un message
	 *         d'erreur en cas d'échec.
	 * 
	 *         Algorithme : 1. Recherche les positions des champs "type" et "mess"
	 *         dans la chaîne JSON. 2. Extrait les valeurs associées à ces champs.
	 *         3. Retourne un nouvel objet Message avec les valeurs extraites. 4. En
	 *         cas d'erreur (par exemple, format incorrect), retourne un message
	 *         avec le type "error" et un contenu "-1".
	 */
	public static Message fromJson(String s) {
		try {
			// Recherche des positions des champs "type" et "mess"
			String re1 = "type\":\"";
			int pos1 = s.indexOf(re1);
			int pos2 = s.indexOf("\"", pos1 + re1.length());

			String re2 = "mess\":\"";
			int pos3 = s.indexOf(re2);
			int pos4 = s.indexOf("\"", pos3 + re2.length());

			// Extraction des valeurs des champs
			String s1 = s.substring(pos1 + re1.length(), pos2);
			String s2 = s.substring(pos3 + re2.length(), pos4);

			System.out.println("fromJson " + s1 + " " + s2);
			return new Message(s1, s2);
		} catch (Exception e) {
			// En cas d'erreur, retourne un message d'erreur
			return new Message("error", "-1");
		}
	}

	// Getters et setters pour les variables d'instance

	/**
	 * Récupère le type du message.
	 * 
	 * @return Le type du message.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Définit le type du message.
	 * 
	 * @param type Le type du message.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Récupère le contenu du message.
	 * 
	 * @return Le contenu du message.
	 */
	public String getMess() {
		return mess;
	}

	/**
	 * Définit le contenu du message.
	 * 
	 * @param mess Le contenu du message.
	 */
	public void setMess(String mess) {
		this.mess = mess;
	}
}
