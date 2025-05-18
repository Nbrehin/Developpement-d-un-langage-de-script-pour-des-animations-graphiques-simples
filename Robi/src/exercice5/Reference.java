package exercice5;

import java.util.HashMap;
import java.util.Map;

import stree.parser.SNode;

/**
 * Classe Reference
 * 
 * Cette classe représente une référence à un objet manipulable dans
 * l'environnement. Elle permet de stocker des commandes associées à cet objet,
 * ainsi que des références enfants. 
 * 
 * Variables d'instance : - receiver : L'objet
 * référencé (par exemple, un élément graphique ou un conteneur). - primitives :
 * Une map associant des noms de commandes à leurs implémentations (Command). -
 * children : Une map des références enfants associées à cette référence. - refs
 * : Une liste des noms des références enfants, utilisée pour accéder aux
 * enfants par position. - nom : Le nom de la référence. 
 * 
 * Dépendances : - Utilise l'interface Command pour exécuter des commandes sur l'objet référencé. -
 * Utilise la classe SNode pour représenter les commandes sous forme de nœuds
 * syntaxiques.
 */
public class Reference {
	Object receiver;
	Map<String, Command> primitives;
	Map<String, Reference> children;
	Map<String, SNode> scripts;

	public Reference(Object receiver) {
		this.receiver = receiver;
		primitives = new HashMap<String, Command>();
		this.children = new HashMap<String, Reference>();
		this.scripts = new HashMap<String, SNode>();
	}

	private Command getCommandByName(String selector) {
		return this.primitives.get(selector);
	}

	public void addCommand(String selector, Command primitive) {
		this.primitives.put(selector, primitive);
	}

	public Object getReceiver() {
		return this.receiver;
	}

	public SNode getScriptByName(String nom) {
		return scripts.get(nom);
	}

	public void addChild(String nom, Reference reference) {
		this.children.put(nom, reference);
	}

	public void removeChild(String nom) {
		this.children.remove(nom);
	}

	public void clearChildren() {
		this.children.clear();
	}

	public Map<String, Reference> getChildren() {
		return this.children;
	}

	public void addScript(String str, SNode s) {
		this.scripts.put(str, s);
	}

	public Reference run(SNode method) {
		Command cmd = this.getCommandByName(method.get(1).contents());
		Reference r = cmd.run(this, method);
		return r;
	}
}