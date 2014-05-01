package domino.game.classes;

import java.util.ArrayList;
import java.util.Scanner;


public class HumanPlayer implements Player {

	private ArrayList<Domino> hand;
	private int score;
	private int handScore;
	private String name;
	private boolean visible;
	private int handSize;
	private Scanner reader;

	public HumanPlayer(String name, Scanner reader){
		this.name = name;
		this.score = 0;
		this.reader = reader;
	}


	public boolean play(Table table){
		System.out.println("Dominoes in hand = " + this.handSize);
		if(visible)	{
			this.showHand();
			ArrayList<Domino> valid = this.validDominoes(table);
			if(valid.size()==0) {
				System.out.println("No valid domino can be played. Skipping turn.\n");
				return false;
			}
			else {
				for(int i = 0; i < valid.size(); i++) {
					System.out.println("Press " + (i+1) + " to play " + valid.get(i).toString());
				}
				boolean reenter = true;
				int temp = -1;
				while(reenter) {
					temp = this.reader.nextInt()-1;
					if(temp >= 0 && temp < valid.size()) reenter = false;
					else System.out.println("Invalid domino.");
				}
				Domino playedDomino = valid.get(temp);
				table.play(playedDomino);
				for(Domino handDomino: this.hand)
					if(handDomino.equals(playedDomino)) {
						this.hand.remove(handDomino);
						break;
					}
				this.handSize--;
				System.out.println("Played " + playedDomino.toString() + ".\n");
				this.updateHandScore();
				return true;
			}			
		}

		else {
			System.out.println("Press s to skip turn, any key to enter domino:");
			String input = this.reader.next();
			if(input.equals("s")) {
				System.out.println("Skipped turn.");
				return false;
			}
			else {
				Domino tempDomino = new Domino(this.reader.nextInt(),this.reader.nextInt());
				boolean valid = table.play(tempDomino);
				while(!valid) {
					System.out.println(tempDomino.toString() + " is an invalid domino. Enter a valid domino:");
					tempDomino = new Domino(this.reader.nextInt(),this.reader.nextInt());
					valid = table.play(tempDomino);
				}
				this.handSize--;
				System.out.println("Played " + tempDomino.toString() + ".");
				return true;
			}
		}
	}


	public boolean handEmpty(){
		return this.handSize==0;
	}


	/**
	 * Checks the player's hand for valid dominoes than can be played given
	 * the current game table and returns them.
	 * @return a list of dominoes in the hand that can be played.
	 */
	private ArrayList<Domino> validDominoes(Table table){
		ArrayList<Domino> outerDominoes = table.getOuterDominoes();

		if(outerDominoes.size()==0)
			return(this.hand);

		else if(outerDominoes.size()==1){
			Domino tableDomino = outerDominoes.get(0);
			int leftRank = tableDomino.getLeftRank();
			int rightRank = tableDomino.getRightRank();
			ArrayList<Domino> validDominoes = new ArrayList<Domino>();
			for(Domino currDomino : this.hand) {
				if(rightRank==currDomino.getLeftRank() || leftRank==currDomino.getLeftRank()) {
					validDominoes.add(new Domino(currDomino.getLeftRank(), currDomino.getRightRank()));		
					if(currDomino.getLeftRank() == currDomino.getRightRank()) continue;
				}
				currDomino.rotate();
				if(rightRank==currDomino.getLeftRank() || leftRank==currDomino.getLeftRank()) {
					validDominoes.add(new Domino(currDomino.getLeftRank(), currDomino.getRightRank()));					
				}
			}
			return validDominoes;
		}
		
		else {
			int leftRank = outerDominoes.get(0).getLeftRank();
			int rightRank = outerDominoes.get(1).getRightRank();
			ArrayList<Domino> validDominoes = new ArrayList<Domino>();
			for(int i = 0; i < this.hand.size(); i++) {
				Domino currDomino = this.hand.get(i);
				if(rightRank==currDomino.getLeftRank() || leftRank==currDomino.getLeftRank()) {
					validDominoes.add(new Domino(currDomino.getLeftRank(), currDomino.getRightRank()));		
					if(currDomino.getLeftRank() == currDomino.getRightRank()) continue;
				}
				currDomino.rotate();
				if(rightRank==currDomino.getLeftRank() || leftRank==currDomino.getLeftRank()) {
					validDominoes.add(new Domino(currDomino.getLeftRank(), currDomino.getRightRank()));					
				}
			}
			return validDominoes;
		}
	}

	public int getScore(){
		if(!visible) {
			System.out.println("Enter " + this.getName() + "'s hand score:");
			this.handScore = this.reader.nextInt();
		}
		this.score += this.handScore;
		return this.score;
	}


	@Override
	public void initHand(ArrayList<Domino> dominoes, Table table) {
		if(dominoes.size()!=7)
			throw new IllegalArgumentException("Unable to initialize hand, size must be equal to 7.");

		else {
			table.remove(dominoes);
			this.hand = dominoes;
			this.visible = true;
			this.handSize = 7;
		}
	}


	@Override
	public void initHand(boolean init, Table table) throws Exception {
		if(init) {
			this.hand = new ArrayList<Domino>(7);
			for(int i = 0; i < 7; i++)
				this.hand.add(table.draw());
			this.visible = true;
			this.handSize = 7;
		}
		else {
			this.visible = false;
			this.handSize = 7;
		}
	}


	@Override
	public String getName() {
		return "Player " + this.name;		
	}


	@Override
	public boolean isAI() {
		return false;
	}


	@Override
	public boolean hasDoubleSix() {
		for(Domino currDomino : this.hand) 
			if(currDomino.getLeftRank() == 6 && currDomino.getRightRank() == 6)
				return true;	
		
		return false;
	}


	@Override
	public void resetScore() {
		this.score = 0;		
	}

	/**
	 * Prints the player's hand.
	 */
	private void showHand(){
		if(visible)
			for(Domino currDomino : this.hand)
				System.out.println(currDomino.toString() + " ");
	}
	
	private void updateHandScore(){
		if(visible) {
			this.handScore = 0;
			for(Domino currDomino : this.hand)
				this.handScore += currDomino.getScore();	
		}
	}


	@Override
	public void playDoubleSix(Table table) {
		if(this.hasDoubleSix()) {
			for(Domino domino : this.hand) 
				if(domino.getLeftRank()==6 && domino.getRightRank()==6) {
					table.play(domino);
					System.out.println("Played " + domino.toString() + ".\n");
					this.hand.remove(domino);
					this.updateHandScore();
					break;
				}			
		}	
	}

}
