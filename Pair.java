
/**
 * This class is a subclass of the Hand class and it models a hand of Pair cards in a Big Two card game.
 * 
 * @author Panpan
 */
public class Pair extends Hand {

	/**
	 * This constructor creates an instance of the Pair class.
	 * @param player player of this hand
	 * @param cards cards in this hand
	 */
	public Pair (CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Check to see if the hand of Pair is valid
	 * @return boolean value that specifies validity of the hand
	 */ @Override
	public boolean isValid() {
		 //A valid pair must have 2 cards
		if (this.size() != 2) { return false; }
		//The 2 cards must have the same rank
		if (this.getCard(0).getRank() != this.getCard(1).getRank()) {
			return false;
		}
		return true;
	}

	/**
	 * This method returns the type of the hand in string form.
	 * @return string that specifies the type of hand
	 */ @Override
	public String getType() {
		return "Pair";
	}

}
