package fr.unistra.pelican.algorithms;

/**
 * This algorithm is an example which aims to demonstrate how to create a conform class.
 * 
 * @author Lefevre, Florent Sollier
 *
 */
public class Exemple extends Algorithm {

	// D�finition des param�tres d'entr�e (obligatoires ou optionnels) et de sortie
	// sans convention impos�e de nommage mais avec une valeur par d�faut pour les options,
	// une visibilit� publique et une repr�sentation sous forme d'objets (pas des types primitifs)

	// Exemples :
	/**
	 * Here the description of the attribute exempleInput1.
	 */
	public Image exempleInput1 ;
	
	/**
	 * Here the description of the attribute exempleInput2 .
	 */
	public Integer exempleInput2 ;
	
	/**
	 * Here the description of the attribute exempleOutput1.
	 */
	public Image exempleOutput1 ;
	
	/**
	 * Here the description of the attribute exempleOutput2.
	 */
	public Integer exempleOutput2 ;
	
	/**
	 * Here the description of the attribute exempleOption1.
	 */
	public Integer exempleOption1 = 1;
	
	/**
	 * Here the description of the attribute exempleOption2.
	 */
	public Integer exempleOption2 = 0;
	

	// D�finition du constructeur par d�faut
	public Exemple() {
		// d�finition de la description d'aide
		super.help="description en une ou quelques lignes de l'algorithme";
		// d�finition des listes ordonn�es de param�tres d'entr�e, de sortie, et d'option
		// telles qu'elles seront utilis�es lors des appels � process
		super.inputs="exempleInput1,exempleInput2";
		super.outputs="exempleOutput1,exempleOutput2";
		// et seulement s'il y a des options...
		// mettre les options les plus fr�quemment utilis�es en t�te de liste
		super.options="exempleOption1,exempleOption2";
		}

	// D�finition de la m�thode de traitement (pas de changement)
	public void launch() {
		// ici on doit v�rifier la validit� du contenu des param�tres d'entr�e et des options
		// car seule une v�rification de type aura �t� faite dans la classe abstraite
		...
		// à la fin de cette m�thode, il faudra que tous les param�tres de sortie soient correctement d�finis
		exempleOutput1 = ImageApresTraitement1;
		exempleOutput2 = ImageApresTraitement1..getBDim();
		}

	/**
	 * Here the description of what does the exec method (which basically is the description of 
	 * what does the algorithm that you can copy/paste from the top).
	 * 
	 * @param exempleInput1 Here the description of the parameter exempleInput1.
	 * @param exempleInput2 Here the description of the parameter exempleInput2.
	 * @return Here the description of what returns the exec method.
	 */
	public static Image exec(Image exempleInput1, int exempleInput2) {
		return (Image) new Exemple().process(exempleInput1,exempleInput2);
		}

	}
