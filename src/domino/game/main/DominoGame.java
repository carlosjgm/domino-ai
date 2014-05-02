package domino.game.main;

import java.util.ArrayList;
import java.util.Scanner;

import domino.game.classes.Game;
import domino.game.classes.GreedyPlayer;
import domino.game.classes.HumanPlayer;
import domino.game.classes.PSOPlayer;
import domino.game.classes.Player;
import domino.game.classes.RandomPlayer;

public class DominoGame {

	public static void main(String[] args) throws Exception {

		Scanner reader = new Scanner(System.in);

		/*ArrayList<Player> players = new ArrayList<Player>();
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
						players.add(new RandomPlayer(false));
						done = true;
					}
					else if(ai == 2) {
						players.add(new GreedyPlayer(true));
						done = true;
					}
					else if(ai == 3) {
						players.add(new GreedyPlayer(false));
						done = true;
					}
					else if(ai == 4) {
						players.add(new PSOPlayer(false));
						done = true;
					}
					System.out.println(players.get(i).getName() + " added to the list of players.");
					break;

				default:
					break;
				}
			}
		}

		Game dominoGame = new Game(players, reader);

		dominoGame.start(true);*/

		//first time training
		trainPSOPlayer(reader, false, null, 0);

		//continue training
		//		

		reader.close();
	}

	private static void trainPSOPlayer(Scanner reader, boolean existing, float[] weights, int winRatio) throws Exception {
		int numparticles = 100;
		int iternum = 1000;
		int rounds = 100;

		ArrayList<PSOPlayer> psoPlayers = new ArrayList<PSOPlayer>();
		for(int i = 0; i < numparticles; i++) {
			if(!existing)
				psoPlayers.add(new PSOPlayer(true));
			else
				psoPlayers.add(new PSOPlayer(weights, winRatio, true));
		}

		ArrayList<Game> games = new ArrayList<Game>();
		for(int i = 0; i < numparticles; i++) {
			ArrayList<Player> players = new ArrayList<Player>();
			players.add(psoPlayers.get(i));
			players.add(new RandomPlayer(true)); players.add(new RandomPlayer(true)); players.add(new RandomPlayer(true));
			games.add(new Game(players, reader));
		}

		float winratios[] = new float[numparticles];
		float globalBestWeights[] = {0,0,0,0,0};
		float globalBestWinRatio = 0;
		
		int iterBestParticle = 0;
		for(int i = 0; i < iternum; i++) {
			iterBestParticle = 0;
			for(int p = 0; p < numparticles; p++) {
				Game currGame = games.get(p);
				int currParticleWinRatio = 0;
				for(int r = 0; r < rounds; r++) {
					currGame.start(true);
					int winner = 0;
					for(int w = 0; w < 4; w ++)
						if(currGame.getScores()[w] < currGame.getScores()[winner])
							winner = w;					
					if(winner == 0)
						currParticleWinRatio++;
				}
				winratios[p] = ((float) currParticleWinRatio)/rounds;
				if(winratios[p] > winratios[iterBestParticle])
					iterBestParticle = p;
			}

			if(winratios[iterBestParticle] > globalBestWinRatio) {
				globalBestWinRatio = winratios[iterBestParticle];
				globalBestWeights = psoPlayers.get(iterBestParticle).getLocalBestWeights();
			}
			
			for(int p = 0; p < numparticles; p++) {
				psoPlayers.get(p).updateWeights(winratios[p], globalBestWeights);
			}
			System.out.println("Iteration " + i);
			System.out.println("Gobal Best Win Ratio = " + globalBestWinRatio);
			System.out.print("Global Best Weights = ");
			for(int j = 0; j < 5; j++)
				System.out.print(globalBestWeights[j] + " ");
			System.out.println("\n");
		}


	}

}
