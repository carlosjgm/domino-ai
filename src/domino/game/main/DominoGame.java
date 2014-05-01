package domino.game.main;

import java.util.ArrayList;
import java.util.Scanner;

import domino.game.classes.Game;
import domino.game.classes.GreedyPlayer;
import domino.game.classes.HumanPlayer;
import domino.game.classes.Player;
import domino.game.classes.RandomPlayer;

public class DominoGame {

	public static void main(String[] args) throws Exception {

		Scanner reader = new Scanner(System.in);

		ArrayList<Player> players = new ArrayList<Player>();
		for(int i=0; i < 4; i++){
			boolean done = false;
			while(!done){
				System.out.println("\nPlayer " + (i+1) + ": Enter 1 if human controlled, press 2 if AI controlled:");
				int option = reader.nextInt();
				switch (option) {

				case 1:
					System.out.println("Enter name: ");
					String name = reader.next();
					players.add(new HumanPlayer(name, reader));
					System.out.println(name + " added to the list of players.");
					done = true;
					break;

				case 2:
					System.out.println("Enter 1 for Random Player, 2 for maxGreedy Player, 3 for minGreedy Player, 4 for PSO Player:");
					int ai = reader.nextInt();
					if(ai == 1) {
						players.add(new RandomPlayer());
						System.out.println(players.get(i).getName() + " added to the list of players.");
						done = true;
					}
					else if(ai == 2) {
						players.add(new GreedyPlayer(true));
						System.out.println(players.get(i).getName() + " added to the list of players.");
						done = true;
					}
					else if(ai == 3) {
						players.add(new GreedyPlayer(false));
						System.out.println(players.get(i).getName() + " added to the list of players.");
						done = true;
					}
					else if(ai == 4) {
						System.out.println("maxGreedy Player not yet implemented.");
						done = false;
					}
					break;

				default:
					break;
				}
			}
		}

		Game dominoGame = new Game(players, reader);
		
		dominoGame.start(true);

		reader.close();
	}

}
