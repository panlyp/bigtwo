
/**
 * This class is a subclass of the Hand class and it models a hand of Single card in a Big Two card game.
 * 
 * @author Panpan
 */
public class Single extends Hand {

	/**
	 * This constructor creates an instance of the Single class.
	 * @param player player of this hand
	 * @param cards cards in this hand
	 */
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Check the hand to see if it is a valid Single hand
	 * @return boolean value that specifies validity of the hand
	 */ @Override
	public boolean isValid() {
		//Valid if only one card is in the hand
		return this.size() == 1 ? true : false;
	}

	/**
	 * This method returns the type of the hand in string form.
	 * @return string that specifies the type of hand
	 */ @Override
	public String getType() {
		return "Single";
	}
	
}
