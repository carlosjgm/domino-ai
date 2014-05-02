package domino.game.classes;

import java.util.ArrayList;
import java.util.Random;

public class PSOPlayer implements Player {

	private ArrayList<Domino> hand;
	private int score;
	private boolean silent;
	private int handScore;
	private float[] weights;
	private float[] velocities;
	private float[] localBestWeights;
	private float momentum;
	private float bestWinRatio;
	private Random rand;

	public PSOPlayer(boolean silent) {
		this.score = 0;
		this.silent = silent;
		this.rand = new Random();
		this.bestWinRatio = 0;
		this.momentum = rand.nextFloat();
		this.velocities = new float[5];
		this.weights = new float[5];
		this.localBestWeights = new float[5];
		for(int i = 0; i < 5; i++) {
			this.weights[i] = (float) (5*this.rand.nextFloat()-2.5);
			this.velocities[i] = 2*this.rand.nextFloat() - 1;
			this.localBestWeights[i] = this.weights[i];
		}
	}

	public PSOPlayer(float[] weights, float bestWinRatio, boolean silent) {
		this.score = 0;
		this.silent = silent;
		this.rand = new Random();
		this.bestWinRatio = bestWinRatio;
		this.momentum = rand.nextFloat();
		this.velocities = new float[5];
		this.weights = weights;
		this.localBestWeights = new float[5];
		for(int i = 0; i < 5; i++) {
			this.localBestWeights[i]=this.weights[i];		
			this.velocities[i] = 2*this.rand.nextFloat() - 1;
		}
	}

	@Override
	public boolean play(Table table) {
		if(!silent)
			System.out.println("Dominoes in hand = " + this.hand.size());
		ArrayList<Domino> valid = this.validDominoes(table);
		if(valid.size()==0) {
			if(!silent)
				System.out.println("No valid domino can be played. Skipping turn.\n");
			return false;
		}
		else if(valid.size()==1) {
			Domino playedDomino = valid.get(0);
			table.play(playedDomino);
			for(Domino handDomino: this.hand)
				if(handDomino.equals(playedDomino)) {
					this.hand.remove(handDomino);
					break;
				}
			if(!silent)
				System.out.println("Played " + playedDomino.toString() + ".\n");
			this.updateHandScore();
			return true;			
		}
		else {
			int temp = this.bestDomino(valid, table);
			Domino playedDomino = valid.get(temp);
			table.play(playedDomino);
			for(Domino handDomino: this.hand)
				if(handDomino.equals(playedDomino)) {
					this.hand.remove(handDomino);
					break;
				}
			if(!silent)
				System.out.println("Played " + playedDomino.toString() + ".\n");
			this.updateHandScore();
			return true;
		}			
	}

	private int bestDomino(ArrayList<Domino> valid, Table table) {		
		int currBestDomino = 0;
		float currBestScore = 0;
		for(int i = 0; i < valid.size(); i++) {
			Domino tempDomino = valid.get(i);
			float tempScore[] = new float[5];
			tempScore[0] = tempDomino.getScore()*this.weights[0];
			tempScore[1] = this.dominoRarity(tempDomino, table)*this.weights[1];
			tempScore[2] = this.handRarity(tempDomino)*this.weights[2];
			tempScore[3] = ((tempDomino.isDouble()) ? 1 : 0)*this.weights[3];
			tempScore[4] = this.outerRarity(tempDomino,table)*this.weights[4];

			float totalScore = 0;
			for(int j = 0; j < 5; j++)
				totalScore += tempScore[j];
			if(totalScore > currBestScore) {
				currBestDomino = i;
				currBestScore = totalScore;
			}
		}		
		return currBestDomino;
	}

	private int dominoRarity(Domino domino, Table table) {
		int leftRank = domino.getLeftRank();
		int rightRank = domino.getRightRank();

		int rarity = 0;
		ArrayList<Domino> played = table.getPlayed();
		for(Domino playedDomino : played) {
			int leftPlayed = playedDomino.getLeftRank();
			int rightPlayed = playedDomino.getRightRank();
			if(leftRank == leftPlayed || leftRank == rightPlayed)
				rarity++;
			else if(rightRank == leftPlayed || rightRank == rightPlayed)
				rarity++;
		}
		return rarity;
	}

	private int handRarity(Domino domino) {
		int leftRank = domino.getLeftRank();
		int rightRank = domino.getRightRank();
		int rarity = 0;
		for(Domino handDomino : this.hand) {
			int leftHand = handDomino.getLeftRank();
			int rightHand = handDomino.getRightRank();
			if(leftHand == leftRank || leftHand == rightRank)
				rarity++;
			if(rightHand == leftRank || rightHand == rightRank)
				rarity++;
		}
		return rarity;
	}

	private int outerRarity(Domino domino, Table table) {
		ArrayList<Domino> outer = table.getOuterDominoes();
		int outerLeftRank;
		int outerRightRank;
		if(outer.size()==1) {
			outerLeftRank = outer.get(0).getLeftRank();
			outerRightRank = outer.get(0).getRightRank();
		}
		else {
			outerLeftRank = outer.get(0).getLeftRank();
			outerRightRank = outer.get(1).getRightRank();
		}

		if(domino.getLeftRank() == outerLeftRank)
			outerLeftRank = domino.getRightRank();
		else if(domino.getLeftRank() == outerRightRank)
			outerRightRank = domino.getRightRank();

		int rarity = 0;
		for(Domino handDomino : this.hand) {
			if(handDomino.equals(domino))
				continue;

			int leftHand = handDomino.getLeftRank();
			int rightHand = handDomino.getRightRank();
			if(leftHand == outerLeftRank || leftHand == outerRightRank)
				rarity++;
			if(rightHand == outerLeftRank || rightHand == outerRightRank)
				rarity++;
		}

		return rarity;
	}

	public void updateWeights(float winRatio, float[] globalBestWeights) {
		if(winRatio > this.bestWinRatio) {
			this.bestWinRatio = winRatio;
			for(int i = 0; i < 5; i++)
				this.localBestWeights[i] = this.weights[i];
		}

		//update weights and velocities
		float gVelocityWeight = this.rand.nextFloat();
		float lVelocityWeight = this.rand.nextFloat();
		for(int i = 0; i < 5; i++) {
			float globalVelocity = gVelocityWeight*(globalBestWeights[i] - weights[i]);
			float localVelocity = lVelocityWeight*(localBestWeights[i] - weights[i]);
			this.velocities[i] = this.momentum*this.velocities[i] + globalVelocity + localVelocity;
			this.weights[i] += this.velocities[i];
		}
	}

	public float[] getLocalBestWeights() {
		return this.localBestWeights;
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
		return "PSO Player";
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
					if(!silent)
						System.out.println("Played " + domino.toString() + ".\n");
					this.hand.remove(domino);
					this.updateHandScore();
					break;
				}			
		}					
	}
}
