
/**
 * This class is a subclass of the Hand class and it models a hand of Flush cards in a Big Two card game.
 * 
 * @author Panpan
 */
public class Flush extends Hand {
	
	/**
	 * This constructor creates an instance of the Flush class.
	 * @param player player of this hand
	 * @param cards cards in this hand
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Check the hand to see if it is a valid Flush hand
	 * @return boolean value that specifies validity of the hand
	 */ @Override
	public boolean isValid() {
		//must not form a flush with fewer or more than 5 cards
		if (this.size() != 5) return false; 
		
		//check the suit of all cards by comparing them with the first card
		for (int i = 0; i < 4; i++) {
			//if any of the card does
			if(this.getCard(0).getSuit() != this.getCard(i + 1).getSuit()) {
				return false;
			} 
		} //done checking, the flush is valid
		return true;
	}
	 
	/**
	 * This method checks if the Flush beats a specific hand.
	 * @param hand a hand to be compared with this hand
	 * @return boolean value that specifies if the Flush beats another one
	 */ @Override
	public boolean beats(Hand hand) {
		 //"A flush always beats any straights."
		 if (hand.getType().equals("Straight")) { return true; }
		 
		 if (hand.getType().equals("Flush")) {
			//"A flush with a higher suit beats a flush with a lower suit."
			 if (this.getCard(0).getSuit() > hand.getCard(0).getSuit()) {
				 return true;
		 	 } else {
				 //"For flushes with same suit, the one having a top card with a higher rank will win"
				 //Compare the top card of the flushes.
				 if (this.getTopCard().compareTo(hand.getTopCard()) == 1) {
					 return true;
		 		 }
			 }
		 }
		//False because flush cannot beat other hands
		 return false;
	 }
	 
	/**
	 * This method returns the type of the hand in string form.
	 * @return string that specifies the type of hand
	 */ @Override
	public String getType() {
		return "Flush";
	}
	
}
