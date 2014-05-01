package domino.game.classes;

import java.util.ArrayList;

public class GreedyPlayer implements Player {

	private ArrayList<Domino> hand;
	private int score;
	private int handScore;
	private boolean max;

	public GreedyPlayer(boolean max){
		this.score = 0;
		this.max = max;
	}

	@Override
	public boolean play(Table table) {
		System.out.println("Dominoes in hand = " + this.hand.size());
		ArrayList<Domino> valid = this.validDominoes(table);
		if(valid.size()==0) {
			System.out.println("No valid domino can be played. Skipping turn.\n");
			return false;
		}
		else {
			int temp = 0;
			for(int i = 0; i < valid.size(); i++) {
				if(max && (valid.get(i).getScore() > valid.get(temp).getScore()))
					temp = i;
				if(!max && (valid.get(i).getScore() < valid.get(temp).getScore()))
					temp = i;
			}
			Domino playedDomino = valid.get(temp);
			table.play(playedDomino);
			for(Domino handDomino: this.hand)
				if(handDomino.equals(playedDomino)) {
					this.hand.remove(handDomino);
					break;
				}
			System.out.println("Played " + playedDomino.toString() + ".\n");
			this.updateHandScore();
			return true;
		}			
	}

	@Override
	public boolean handEmpty() {
		return this.hand.size()==0;
	}

	@Override
	public int getScore() {
		this.score += this.handScore;
		return this.score;
	}

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

	@Override
	public void initHand(ArrayList<Domino> dominoes, Table table) {
		if(dominoes.size()!=7)
			throw new IllegalArgumentException("Unable to initialize hand, size must be equal to 7.");

		else {
			table.remove(dominoes);
			this.hand = dominoes;
		}
	}


	@Override
	public void initHand(boolean init, Table table) throws Exception {
		if(init) {
			this.hand = new ArrayList<Domino>(7);
			for(int i = 0; i < 7; i++)
				this.hand.add(table.draw());
		}
	}

	@Override
	public String getName() {
		return ((max) ? "max" : "min") + "Greedy Player";
	}

	@Override
	public boolean isAI() {
		return true;
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

	private void updateHandScore(){
		this.handScore = 0;
		for(Domino currDomino : this.hand)
			this.handScore += currDomino.getScore();	
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
