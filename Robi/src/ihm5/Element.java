package ihm5;

public class Element{
	double x,y,w,h;
	String type;
	String couleur;
	String nom;
	
	public Element(double x, double y, double w, double h, String type, String couleur, String nom) {
		this.x=x;
		this.y=y;
		this.w=w;
		this.h=h;
		this.type=type;
		this.couleur=couleur;
		this.nom = nom;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String res = this.type;
		res += " ";
		res += this.x;
		res += " ";
		res += this.y;
		res += " ";
		res += this.w;
		res += " ";
		res += this.h;
		res += " ";
		res += this.couleur;
		res += " ";
		res += this.nom;
		return res;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCouleur() {
		return couleur;
	}

	public void setCouleur(String couleur) {
		this.couleur = couleur;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}
}