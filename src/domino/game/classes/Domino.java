package domino.game.classes;

public class Domino {

	private int leftRank;
	private int rightRank;
	private int score;
	
	public Domino(int leftRank, int rightRank){
		if(leftRank > 6 || leftRank < 0 || rightRank > 6 || rightRank < 0)
			throw new IllegalArgumentException("Invalid rank: ranks must be between 0 and 6.");
		this.leftRank = leftRank;
		this.rightRank = rightRank;
		this.score = leftRank + rightRank;
	}
	
	public void rotate(){
		int temp = this.rightRank;
		this.rightRank = this.leftRank;
		this.leftRank = temp;
	}
	
	public int getRightRank(){
		return this.rightRank;
	}
	
	public int getLeftRank(){
		return this.leftRank;
	}
	
	public String toString(){
		return "[" + this.leftRank + "|" + this.rightRank + "]";	
	}
	
	public boolean equals(Domino domino) {
		return (this.leftRank == domino.getLeftRank() && this.rightRank == domino.getRightRank()) || (this.leftRank == domino.getRightRank() && this.rightRank == domino.getLeftRank());
	}
	
	public int getScore() {
		return this.score;
	}
}
