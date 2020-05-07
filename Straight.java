
/**
 * This class is a subclass of the Hand class and it models a hand of Straight cards in a Big Two card game.
 * 
 * @author Panpan
 */
public class Straight extends Hand {

	/**
	 * This constructor creates an instance of the Straight class.
	 * @param player player of this hand
	 * @param cards cards in this hand
	 */
	public Straight (CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Check the hand of Straight to see if it is valid
	 * @return boolean value that specifies validity of the hand
	 */ @Override
	public boolean isValid() {
		 if(this.size() != 5) { return false; }

		 // to see if the cards are with consecutive ranks
		 for (int i = 1; i < 4; i++) {
			 int thisRank = this.getCard(i).getRank();
			 int nextRank = this.getCard(i+1).getRank();
			 if (thisRank + 1 != nextRank) { return false; }
		 }
		 // all cards checked
		 return true;
	}


	/**
	 * This method checks if this Straight beats a specific hand
	 * @param hand a hand to be compared with this hand
	 * @return boolean value that specifies if this hand beats another one
	 */
	public boolean beats(Hand hand){
		/* A straight flush always beats any straights, flushses,
		full houses and quads */
		String[] mustWins = {"Straight", "Flush", "Full House", "Quad"};
		for (String str : mustWins) {
			if (hand.getType().equals(str)) { return true; }
		}

		/* A straight flush that has a top card with higher
		rank wins */
		if (this.getTopCard().getRank() > hand.getTopCard().getRank()) {
			return true;
		} else {
			/* If the straight flushes have top cards of same rank,
			compare the suit */
			if (this.getTopCard().getSuit() > hand.getTopCard().getSuit()) {
				return true;
			}
		}
		return false;
	}
		
	/**
	 * This method returns the type of the hand in string form.
	 * @return string that specifies the type of hand
	 */ @Override
	public String getType() {
		return "Straight";
	}
	
}
