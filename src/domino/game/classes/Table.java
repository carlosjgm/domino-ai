package domino.game.classes;

import java.util.ArrayList;
import java.util.Random;

public class Table {

	private ArrayList<Domino> pile;
	private ArrayList<Domino> played;
	private ArrayList<Domino> outerDominoes;
	private Random rand;

	public Table(){
		this.pile = new ArrayList<Domino>(28);
		for(int i = 0; i <= 6; i++)
			for(int j = i; j <= 6; j++)
				this.pile.add(new Domino(i,j));
		this.played = new ArrayList<Domino>(28);
		this.outerDominoes = new ArrayList<Domino>();
		this.rand = new Random();
	}

	public boolean pileEmpty(){
		return this.pile.size() == 0;
	}

	public Domino draw() throws Exception{
		if(this.pileEmpty()){
			throw new Exception("Pile is empty, cannot draw from it.");
		}
		else {
			int index = this.rand.nextInt(this.pile.size());
			Domino domino = this.pile.remove(index);
			return domino;
		}
	}

	public ArrayList<Domino> getPlayed(){
		return this.played;
	}

	/**
	 * Places a valid domino on the table. Returns true if the domino is valid, false otherwise.
	 * @param domino The domino to be played.
	 * @return true if the domino is valid, false otherwise.
	 */
	public boolean play(Domino domino){
		if(outerDominoes.size() == 0) {
			this.played.add(domino);
			this.outerDominoes.add(domino);
			return true;
		}
		
		else if(outerDominoes.size() == 1) {
			Domino tableDomino = outerDominoes.get(0);
			int leftRank = tableDomino.getLeftRank();
			int rightRank = tableDomino.getRightRank();
			if(domino.getLeftRank() == rightRank) {
				this.played.add(domino);
				this.outerDominoes.add(domino);
				return true;
			}
			else if(domino.getLeftRank() == leftRank) {			
				domino.rotate();
				this.played.add(0, domino);
				this.outerDominoes.add(0, domino);
				return true;
			}
			else return false;
		}
		
		else {
			int leftRank = outerDominoes.get(0).getLeftRank();
			int rightRank = outerDominoes.get(1).getRightRank();
			if(domino.getLeftRank() == rightRank) {
				this.played.add(domino);
				this.outerDominoes.set(1, domino);
				return true;
			}
			else if(domino.getLeftRank() == leftRank) {	
				domino.rotate();
				this.played.add(0,domino);
				this.outerDominoes.set(0, domino);
				return true;
			}
			else return false;
		}	
	}

	public ArrayList<Domino> getOuterDominoes(){
		return this.outerDominoes;
	}

	/**
	 * Prints the current state of the table. 
	 */
	public void show(){
		System.out.println("Table:");
		for(Domino currDomino : this.played)
			System.out.print(currDomino.toString());
		System.out.println("\n");
	}

	/**
	 * Removes dominoes from the pile.
	 * @param dominoes list of dominoes to be removed from pile
	 */
	public void remove(ArrayList<Domino> dominoes) {
		for(Domino remDomino : dominoes) {
			int index = -1;
			for(int i = 0; i < this.pile.size(); i++) {
				Domino currDomino = this.pile.get(i);
				if(remDomino.equals(currDomino)) {
					index = i;
					break;
				}
			}
			if(index == -1)
				throw new IllegalArgumentException("Unable to remove " + remDomino.toString() + "from pile.");
			
			else {
				this.pile.remove(index);
			}

		}
	}
}
