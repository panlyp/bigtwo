
/**
 * This class is a subclass of the Hand class and it models a hand of Full House card in a Big
 * Two card game.
 * 
 * @author Panpan
 */
public class FullHouse extends Hand {
	
	/**
	 * This constructor creates an instance of the FullHouse class.
	 * @param player player of this hand
	 * @param cards cards in this hand
	 */
	public FullHouse (CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * This method retrieves the top card of this hand. The card in 
	 * the triplet with the highest suit in a full house is referred 
	 * to as the top card of this full house.
	 * @return a Card object which is the top card of this hand
	*/ @Override
	public Card getTopCard(){
		this.sort(); // do the sorting first
		/* Check if it is the case that the triplet is: (1) formed by
		 * the first 3 cards; or (2) last 3 cards */
		return this.getCard(0).getRank() == this.getCard(2).getRank() 
				? this.getCard(2) : this.getCard(4);
				// if (1), the 3rd card is the top card; else the last card
	}
	
	/**
	 * Check to see if the hand of FullHouse is valid
	 * @return boolean value that specifies validity of the hand
	 */ @Override
	public boolean isValid() {
		sort();
		if (this.size() != 5) { return false; }

		// first find where the triplet is by checking the 1st and 3rd cards
		if(getCard(0).getRank() == getCard(2).getRank()) {
			// if yes, check the first 2 cards
			if(getCard(0).getRank() != getCard(1).getRank()) { return false; }
			// if yes, also check the last 2 card
			if(getCard(3).getRank() != getCard(4).getRank()) { return false; }				
		} else {
			// if no, triplet is formed by the last 3 cards
			// just need to check the first 2 cards' rank
			if(getCard(0).getRank() != getCard(1).getRank()) { return false; }
			// then see if the remaining ones have the same rank
			for(int i = 2; i < 4; i++) {
				if(getCard(i).getRank() != getCard(i + 1).getRank()) { return false; }
			}
		}
		// all cards checked
		return true;
	}

	/**
	 * This method checks if this FullHouse beats another specific hand 
	 * @param hand a hand to be compared with this hand
	 * @return boolean value that specifies if this hand beats another one
	 */
	public boolean beats(Hand hand){
		// FullHouse always beats straights and flushes
		if (hand.getType() == "Straight" ||
				hand.getType() == "Flush") { return true; }
		// if it is also a full house
		if (hand.getType() == "FullHouse") {
			if (this.getTopCard().getRank() 
					> hand.getTopCard().getRank()) { return true; }
		}
		return false;
	}
	
	/**
	 * This method returns the type of the hand in string form.
	 * @return string that specifies the type of hand
	 */ @Override
	public String getType() {
		return "FullHouse";
	}
	
}
