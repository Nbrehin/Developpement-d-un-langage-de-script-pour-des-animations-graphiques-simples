package ihm5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stree.parser.SNode;

/**
 * Classe Reference
 * 
 * Cette classe représente une référence à un objet manipulable dans
 * l'environnement. Elle permet de stocker des commandes associées à cet objet,
 * ainsi que des références enfants.
 * 
 * Variables d'instance : - receiver : L'objet référencé (par exemple, un
 * élément graphique ou un conteneur). - primitives : Une map associant des noms
 * de commandes à leurs implémentations (`Command`). - children : Une map des
 * références enfants associées à cette référence. - refs : Une liste des noms
 * des références enfants, utilisée pour accéder aux enfants par position. - nom
 * : Le nom de la référence.
 * 
 * Dépendances : - Utilise l'interface `Command` pour exécuter des commandes sur
 * l'objet référencé. - Utilise la classe `SNode` pour représenter les commandes
 * sous forme de nœuds syntaxiques.
 */
public class Reference {
	Object receiver;
	Map<String, Command> primitives;
	Map<String, Reference> children;
	List<String> refs;
	String nom;

	/**
	 * Constructeur de la classe Reference.
	 * 
	 * @param receiver L'objet référencé.
	 * @param nom      Le nom de la référence.
	 */
	public Reference(Object receiver, String nom) {
		this.receiver = receiver;
		primitives = new HashMap<>();
		this.children = new HashMap<>();
		this.refs = new ArrayList<>();
		this.nom = nom;
	}

	/**
	 * Récupère une commande associée à un nom.
	 * 
	 * @param selector Le nom de la commande.
	 * @return La commande associée ou null si elle n'existe pas.
	 */
	private Command getCommandByName(String selector) {
		return this.primitives.get(selector);
	}

	/**
	 * Ajoute une commande à la référence.
	 * 
	 * @param selector  Le nom de la commande.
	 * @param primitive L'implémentation de la commande.
	 */
	public void addCommand(String selector, Command primitive) {
		this.primitives.put(selector, primitive);
	}

	/**
	 * Récupère l'objet référencé.
	 * 
	 * @return L'objet référencé.
	 */
	public Object getReceiver() {
		return this.receiver;
	}

	/**
	 * Ajoute une référence enfant à cette référence.
	 * 
	 * @param nom       Le nom de l'enfant.
	 * @param reference La référence enfant.
	 */
	public void addChild(String nom, Reference reference) {
		this.children.put(nom, reference);
		this.refs.add(nom);
	}

	/**
	 * Supprime une référence enfant.
	 * 
	 * @param nom Le nom de l'enfant à supprimer.
	 */
	public void removeChild(String nom) {
		this.children.remove(nom);
		this.refs.remove(nom);
	}

	/**
	 * Supprime tous les enfants de cette référence.
	 */
	public void clearChildren() {
		this.children.clear();
	}

	/**
	 * Récupère les enfants de cette référence.
	 * 
	 * @return Une map contenant les enfants.
	 */
	public Map<String, Reference> getChildren() {
		return this.children;
	}

	/**
	 * Retourne le nombre d'enfants de cette référence.
	 * 
	 * @return Le nombre d'enfants.
	 */
	public int sizeChildren() {
		return this.children.size();
	}

	/**
	 * Exécute une commande sur l'objet référencé.
	 * 
	 * @param method Le nœud syntaxique représentant la commande.
	 * @return La référence résultante de l'exécution de la commande.
	 */
	Reference run(SNode method) {
		Command cmd = this.getCommandByName(method.get(1).contents());
		return cmd.run(this, method);
	}

	/**
	 * Récupère une référence enfant par sa position dans la liste des enfants.
	 * 
	 * @param i La position de l'enfant.
	 * @return La référence enfant.
	 */
	public Reference getRefByPos(int i) {
		return this.children.get(this.refs.get(i));
	}

	// Getters et setters pour les variables d'instance

	public Map<String, Command> getPrimitives() {
		return primitives;
	}

	public void setPrimitives(Map<String, Command> primitives) {
		this.primitives = primitives;
	}

	public List<String> getRefs() {
		return refs;
	}

	public void setRefs(List<String> refs) {
		this.refs = refs;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setReceiver(Element receiver) {
		this.receiver = receiver;
	}

	public void setChildren(Map<String, Reference> children) {
		this.children = children;
	}
}