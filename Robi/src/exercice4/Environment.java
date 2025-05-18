package exercice4;

import java.util.HashMap;

/**
 * Classe Environment
 * 
 * Cette classe représente un environnement contenant des références nommées
 * à des objets manipulables. Elle permet d'ajouter, de supprimer et de récupérer
 * des références par leur nom.
 * 
 * Variables d'instance :
 * - `variables` : Une table de hachage (HashMap) qui associe un nom (String)
 *   à une référence (Reference).
 * 
 * Dépendances :
 * - Utilise la classe `Reference` pour gérer les objets manipulables.
 */
public class Environment {
    // Table de hachage contenant les références nommées
    HashMap<String, Reference> variables;

    /**
     * Constructeur Environment
     * 
     * Initialise une table de hachage vide pour stocker les références.
     */
    public Environment() {
        variables = new HashMap<>();
    }

    /**
     * Ajoute une référence à l'environnement.
     * 
     * @param name Le nom de la référence.
     * @param ref  La référence associée.
     */
    public void addReference(String name, Reference ref) {
        if (name != null && ref != null) {
            variables.put(name, ref);
        }
    }

    /**
     * Supprime une référence de l'environnement.
     * 
     * @param name Le nom de la référence à supprimer.
     */
    public void removeReference(String name) {
        variables.remove(name);
    }

    /**
     * Récupère une référence par son nom.
     * 
     * @param name Le nom de la référence recherchée.
     * @return La référence associée au nom, ou null si elle n'existe pas.
     */
    public Reference getReferenceByName(String name) {
        if (name != null) {
            return variables.get(name);
        }
        return null;
    }
}