package ihm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graphicLayer.GContainer;
import graphicLayer.GElement;

/**
 * Classe Environment
 * 
 * Cette classe représente l'environnement global dans lequel les références aux
 * objets manipulables (par exemple, des éléments graphiques) sont stockées et
 * gérées. Elle permet de gérer les références, de construire une représentation
 * hiérarchique de l'environnement et de manipuler les relations entre les
 * objets.
 * 
 * Variables d'instance : - variables : Une map associant des noms de références
 * à leurs objets `Reference`. - refs : Une liste des noms des références,
 * utilisée pour accéder aux références par position. - printed : Un ensemble de
 * références déjà imprimées, utilisé pour éviter les doublons lors de la
 * construction d'arbres.
 * 
 * Dépendances : - Utilise la classe `Reference` pour représenter les objets
 * manipulables. - Utilise les classes `GContainer` et `GElement` pour les
 * éléments graphiques.
 */
public class Environment {
	// Map associant des noms de références à leurs objets `Reference`
	HashMap<String, Reference> variables;
	// Liste des noms des références
	List<String> refs;
	// Ensemble des références déjà imprimées
	Set<Reference> printed = new HashSet<>();

	/**
	 * Constructeur de la classe Environment. Initialise les structures de données
	 * pour stocker les références.
	 */
	public Environment() {
		variables = new HashMap<>();
		this.refs = new ArrayList<>();
	}

	/**
	 * Ajoute une référence à l'environnement.
	 * 
	 * @param string   Le nom de la référence.
	 * @param spaceRef L'objet `Reference` à associer au nom.
	 */
	public void addReference(String string, Reference spaceRef) {
		if (string != null && spaceRef != null) {
			variables.put(string, spaceRef);
			this.refs.add(string);
		}
	}

	/**
	 * Supprime une référence de l'environnement.
	 * 
	 * @param string Le nom de la référence à supprimer.
	 * @param ref    L'objet `Reference` à dissocier.
	 */
	public void removeReference(String string, Reference ref) {
		this.variables.remove(string, ref);
		this.refs.remove(string);
	}

	/**
	 * Récupère une référence par son nom.
	 * 
	 * @param receiverName Le nom de la référence.
	 * @return L'objet `Reference` associé au nom, ou null si la référence n'existe
	 *         pas.
	 */
	public Reference getReferenceByName(String receiverName) {
		if (receiverName != null)
			return variables.get(receiverName);
		return null;
	}

	/**
	 * Récupère une référence par sa position dans la liste des références.
	 * 
	 * @param i La position de la référence.
	 * @return L'objet `Reference` à la position donnée.
	 */
	public Reference getReferenceByPos(int i) {
		return this.getReferenceByName(this.refs.get(i));
	}

	/**
	 * Construit une représentation textuelle de l'environnement sous forme d'arbre.
	 * 
	 * @return Une chaîne représentant l'arbre des références.
	 */
	public String buildEnvironmentTree() {
		StringBuilder treeBuilder = new StringBuilder();
		for (String key : variables.keySet()) {
			treeBuilder.append(key).append("\n");
		}
		return treeBuilder.toString();
	}

	/**
	 * Récupère la map des variables de l'environnement.
	 * 
	 * @return La map des variables.
	 */
	public HashMap<String, Reference> getVariables() {
		return variables;
	}

	/**
	 * Définit la map des variables de l'environnement.
	 * 
	 * @param variables La nouvelle map des variables.
	 */
	public void setVariables(HashMap<String, Reference> variables) {
		this.variables = variables;
	}

	/**
	 * Récupère la liste des noms des références.
	 * 
	 * @return La liste des noms des références.
	 */
	public List<String> getRefs() {
		return refs;
	}

	/**
	 * Définit la liste des noms des références.
	 * 
	 * @param refs La nouvelle liste des noms des références.
	 */
	public void setRefs(List<String> refs) {
		this.refs = refs;
	}

	/**
	 * Construit une représentation hiérarchique d'une référence sous forme d'arbre.
	 * 
	 * @param r      La référence à représenter.
	 * @param indent Le niveau d'indentation pour l'affichage.
	 * @return Une chaîne représentant l'arbre de la référence.
	 * 
	 *         Algorithme : 1. Vérifie si la référence a déjà été imprimée pour
	 *         éviter les doublons. 2. Ajoute une ligne avec le nom de la référence
	 *         et ses commandes associées. 3. Parcourt récursivement les enfants de
	 *         la référence pour construire l'arbre.
	 */
	public String referenceToArbre(Reference r, int indent) {
		StringBuilder res = new StringBuilder();
		if (!printed.contains(r)) {
			printed.add(r);
		}
		res.append("\n");
		for (int j = 0; j < indent; j++) {
			res.append("	");
		}
		res.append("|-").append(r.getNom());
		if (r != null && !r.primitives.isEmpty()) {
			res.append(" ").append("(");
			r.primitives.forEach((key, value) -> {
				res.append(key);
				res.append(",");
			});
			res.setLength(res.length() - 1);
			res.append(")");
		}

		for (int j = 0; j < r.sizeChildren(); j++) {
			// Appel récursif avec un niveau d'indentation +1
			if (r.getRefByPos(j) != null) {
				res.append(referenceToArbre(r.getRefByPos(j), indent + 1));
			}
		}
		return res.toString();
	}

	/**
	 * Construit une représentation hiérarchique complète de l'environnement.
	 * 
	 * @return Une chaîne représentant l'arbre complet de l'environnement.
	 * 
	 *         Algorithme : 1. Initialise une chaîne avec le titre "Environment". 2.
	 *         Parcourt toutes les références de l'environnement. 3. Ajoute chaque
	 *         référence à l'arbre en appelant `referenceToArbre`.
	 */
	public String environmentToArbre() {
		StringBuilder res = new StringBuilder("Environment");

		for (int i = 0; i < this.variables.size(); i++) {
			Reference ref = this.getReferenceByPos(i);
			if (!printed.contains(ref)) {
				res.append(referenceToArbre(ref, 1));
				printed.add(ref);
			}
		}
		return res.toString();
	}

	/**
	 * Récupère l'ensemble des références déjà imprimées.
	 * 
	 * @return L'ensemble des références imprimées.
	 */
	public Set<Reference> getPrinted() {
		return printed;
	}

	/**
	 * Définit l'ensemble des références déjà imprimées.
	 * 
	 * @param printed Le nouvel ensemble des références imprimées.
	 */
	public void setPrinted(Set<Reference> printed) {
		this.printed = printed;
	}
}