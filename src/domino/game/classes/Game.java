package domino.game.classes;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {

	private Table table;
	private ArrayList<Player> players;
	private int turn;
	private Scanner reader;
	private int skipCount;

	public Game(ArrayList<Player> players, Scanner reader) throws Exception {
		if (players.size() != 4)
			throw new IllegalArgumentException("number of players must be 4");

		this.table = new Table();
		this.players = players;
		this.reader = reader;
	}

	/**
	 * Plays one turn of the game. Returns false if the player's hand is empty
	 * after playing a domino this turn.
	 * 
	 * @return false if the player's hand is empty at the of the this turn, true otherwise
	 */
	private boolean next() {
		this.table.show();
		Player currPlayer = players.get((this.turn)%4);
		System.out.println("(" + (this.turn++)%4 + ") " + currPlayer.getName() + "'s turn: ");
		this.skipCount = (currPlayer.play(this.table)) ? 0 : skipCount+1;
		return !currPlayer.handEmpty() && this.skipCount != 4;
	}

	/**
	 * When start is called, the Game will ask for each player to either
	 * manually input their hand, to automatically draw from pile or to skip. If
	 * one player skips then the automatic option is disabled. 
	 * 
	 * If first is true then all scores are reset and the first turn goes to the player 
	 * with the double six. Otherwise, the scores are kept and the first turn goes to the
	 * player that won the last round.
	 * 
	 * @param first If true then a new match is initialized and the first turn goes to the player 
	 * with the double six. Otherwise, the previous match's scores are kept and the first turn 
	 * goes to the the player that won the last round.
	 * @throws Exception tried to draw from an empty pile.
	 */
	public void start(boolean first) throws Exception {
		this.initHands();
					
		this.turn = -1;
		if(first) {
			for(int i = 0; i < 4; i ++)
				if(this.players.get(i).hasDoubleSix()) {
					System.out.println("(" + i + ") " + players.get(i).getName() + "'s turn:");
					players.get(i).playDoubleSix(this.table);
					this.turn = (i + 1)%4;
					break;
				}
			if(this.turn == -1) {
				boolean reenter = true;
				while(reenter) {
					System.out.println("Enter the number of the first turn player: ");
					this.turn = this.reader.nextInt();
					if(this.turn >= 0 || this.turn <= 3) reenter = false;
				}
			}
			for(int i = 0; i < 4; i++)
				this.players.get(i).resetScore();
		}

		while(this.next());

		System.out.println("Round over.\n");
		this.table.show();
		int scores[] = this.getScores();
		for(int i=0; i < 4; i++)
			System.out.println("Player " + (i+1) + " score = " + scores[i]);
		
	}

	public int[] getScores() {
		int scores[] = new int[4];
		for (int i = 0; i < 4; i++)
			scores[i] = this.players.get(i).getScore();
		return scores;
	}

	private void initHands() throws Exception {
		ArrayList<Integer> manualOptions = new ArrayList<Integer>();
		ArrayList<Integer> otherOptions = new ArrayList<Integer>();
		boolean done;
		boolean skip;
		do{
			skip = false;
			for (int i = 0; i < 4; i++) {
				Player currPlayer = this.players.get(i);		
				System.out.println("\nInitializing hand of (" + i + ") " + currPlayer.getName() + ":");
				while(true) {
					int temp = -1;
					System.out.println("Press 1 for manual, 2 for automatic, 3 to skip");
					temp = this.reader.nextInt();
					if(temp == 1) {
						manualOptions.add(i);
						break;
					}
					else if(temp == 2) {
						otherOptions.add(i);
						break;
					}			
					else if(temp == 3) {
						otherOptions.add(i);
						skip = true;
						break;
					}
				}		
			}
			done = true;

			if(skip) 
				for(int i =0; i < otherOptions.size(); i++)
					if(otherOptions.get(i)==2) {
						System.out.println("Automatic and skip options cannot be chosen simultaneously.");
						done = false;
						break;
					}			

		} while(!done);

		// manual hand initialization
		for(int i = 0; i < manualOptions.size(); i++) {
			Player currPlayer = this.players.get(manualOptions.get(i));
			boolean reenter = true;
			while(reenter) {
				System.out.println(currPlayer.getName() + ", manually enter each domino:");
				ArrayList<Domino> newHand = new ArrayList<Domino>();
				for(int j=0; j < 7; j++){
					System.out.println("Enter domino " + (j+1));
					newHand.add(new Domino(this.reader.nextInt(), this.reader.nextInt()));
					System.out.println(newHand.get(j).toString() + " added.");
				}
				System.out.println("Press 1 to reenter dominoes or any key to continue.");
				String confirm = this.reader.next();
				if(!confirm.equals("1")) {
					currPlayer.initHand(newHand, this.table);
					reenter = false;
				}
			}
			System.out.println(currPlayer.getName() + ", hand has been initialized manually.");
		}

		// automatic hand initialization
		if(!skip)
			for(int i = 0; i < otherOptions.size(); i++){
				Player currPlayer = this.players.get(otherOptions.get(i));
				currPlayer.initHand(true, this.table);
				System.out.println(currPlayer.getName() + ", hand has been initialized automatically.");
			}

		//skip hand initialization
		else 
			for(int i = 0; i < otherOptions.size(); i++){
				Player currPlayer = this.players.get(otherOptions.get(i));
				currPlayer.initHand(false, this.table);
				System.out.println(currPlayer.getName() + ", hand initialization skipped.");
			}

	}		

}
