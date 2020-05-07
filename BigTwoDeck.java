/**
 * This class is a subclass of the Deck class and it models a deck of cards used
 * in a Big Two card game.
 *
 * @author Panpan
 */
public class BigTwoDeck extends Deck {

	/**
	 * Initialize the deck of Big Two cards.
	 *
	 */ @Override
	public void initialize() {
		removeAllCards();
		for (int i = 0; i < 4; i++) {
			for (int j = 2; j < 13; j++) { addCard(new BigTwoCard(i, j)); }
			// Add 'A' and '2' at last
			for (int k = 0; k < 2; k++) { addCard(new BigTwoCard(i, k)); }
		}
	}

}
