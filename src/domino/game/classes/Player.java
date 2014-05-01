package domino.game.classes;

import java.util.ArrayList;

/**
 * Represents a domino player, its hand and its valid actions.
 *
 */
public interface Player {

	/**
	 * Plays a domino. Returns true if player played a domino or false if player skipped turn.
	 * @param table the game Table
	 * @return true if player played a domino this turn, false if player skipped turn
	 */
	public boolean play(Table table);
	
	/**
	 * Returns true if the hand is empty, false otherwise. 
	 * Used to determine when a game round finishes.
	 * @return Return true if the hand is empty, false otherwise.
	 */
	public boolean handEmpty();
	
	public int getScore();
	
	/**
	 * Initializes the hand with the list of dominoes provided.
	 * @param dominoes Initial hand. Size must be equal to 7.
	 * @param table The game Table.
	 */
	public void initHand(ArrayList<Domino> dominoes, Table table);
	
	/**
	 * If init is true then the hand is initialized automatically. Otherwise, the hand initialization is skipped.
	 * If the Player is AI controlled then the hand must be either provided or automatically initialized. Automatics
	 * initialization is only available if all other hands are known.
	 * @param init Indicates whether the hand will be automatically initialized or not.
	 * @param table The game Table.
	 */
	public void initHand(boolean init, Table table) throws Exception;
	
	/**
	 * Returns the name of the player.
	 * @return a string containing the name of the player.
	 */
	public String getName();
	
	/**
	 * Returns true if the player is AI controlled, false if it is human controlled.
	 * @return true if player is AI controlled, false if it is human controlled.
	 */
	public boolean isAI();
	
	/**
	 * Return true if the player has the double six. Returns false if the player either does
	 * not have the double six or his hand is unknown.
	 * @return true only if player has the double six, false otherwise.
	 */
	public boolean hasDoubleSix();
	
	/**
	 * If the player has the double six then it plays it.
	 */
	public void playDoubleSix(Table table);
	
	/**
	 * Sets the player's score to 0/ 
	 */
	public void resetScore();
	
}
