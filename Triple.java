
/**
 * This class is a subclass of the Hand class and it models a hand of Triple cards in a Big Two card game.
 * 
 * @author Panpan
 */
public class Triple extends Hand {

	/**
	 * This constructor creates an instance of the Triple class.
	 * @param player player of this hand
	 * @param cards cards in this hand
	 */
	public Triple (CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Check the hand to see if it the triple is valid
	 * @return boolean value that specifies validity of the hand
	 */ @Override
	public boolean isValid() {
		// See if the hand contains 3 cards
		if(this.size() != 3) { return false; }
		else { //check the rank of the cards - should be the same
			for (int i = 0; i < 3; i++) {
				if (this.getCard(0).getRank() != this.getCard(i).getRank()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * This method returns the type of the hand in string form.
	 * @return string that specifies the type of hand
	 */ @Override
	public String getType() {
		return "Triple";
	}
	
}
