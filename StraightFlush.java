
/**
 * This class is a subclass of the Hand class and it models a hand of StraightFlush cards in a Big Two card game.
 *
 * @author Panpan
 */
public class StraightFlush extends Hand {

	/**
	 * This constructor creates an instance of the StraightFlush class.
	 * @param player player of this hand
	 * @param cards cards in this hand
	 */
	public StraightFlush (CardGamePlayer player, CardList cards) {
		super(player, cards);
	}

	/**
	 * This method checks if this StraightFlush beats another hand
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
		
		if (hand.getType() == "StraighFlush") {
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
		}
		return false;
	}

	/**
	 * Check if the StraightFlush hand is valid
	 * @return boolean value that specifies validity of the hand
	 */ @Override
	public boolean isValid() {
		if (this.size() != 5) { return false; }
		
		this.sort(); // get the hand sorted

		 // to see if the cards are with consecutive ranks
		 for (int i = 1; i < 4; i++) {
			 int thisRank = this.getCard(i).getRank();
			 int nextRank = this.getCard(i+1).getRank();
			 if (thisRank + 1 != nextRank) { return false; }
		 }
		 // all cards' rank checked, next check if the suits are same
		 for (int i = 0; i < 4; i++) {
			 if (this.getCard(i).getSuit() != this.getCard(i+1).getSuit()) { 
				 return false;
			 }
		 }
		// all cards checked
		return true;
	}

	/**
	 * This method returns the type of the hand in string form.
	 * @return string that specifies the type of hand
	 */ @Override
	public String getType() {
		return "StraightFlush";
	}

}
