package com.comp3004.a1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Game {
	private ArrayList<String> suits = new ArrayList<>(Arrays.asList("C","H","D","S"));
	private ArrayList<String> values = new ArrayList<>(Arrays.asList("A", "K", "Q", "J", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
	private final String invalidReturn = "Invalid file input";
	private final String playerWins = "Player Wins";
	private final String dealerWins = "Dealer Wins";
	public Game() {}
	
	public String file(String filePath) throws IOException {
		//Source: http://www.vogella.com/tutorials/JavaIO/article.html
		String cardSequence = new String(Files.readAllBytes(Paths.get(filePath)));
		String[] actionsTemp = cardSequence.split(" ");
		ArrayList<String> actions = new ArrayList<>(Arrays.asList(actionsTemp));
		ArrayList<String> playerActions = new ArrayList<>();
		ArrayList<Card> cards = new ArrayList<>();
		
		Player player = new Player();
		Player dealer = new Player();
				
		//Checking input is valid
		if(!checkValidSequence(actions)) {
			return invalidReturn;
		}
		
		//Add cards to their respective arrays
		for(int i = 0; i < actions.size(); i++) {
			if(actions.get(i).length() >= 2) {
				cards.add(new Card(actions.get(i)));
			} else {
				playerActions.add(actions.get(i));
			}
		}
		
		int cardsIndex = 4;
		int playerActionsIndex = 0;
		
		//First two cards are for the player
		for(int i = 0; i < 2; i++) {
			if(!player.receiveCard(cards.get(i))) {
				return dealerWins;
			}
		}
		
		//Next two are for the dealer
		for(int i = 2; i < 4; i++) {
			if(!dealer.receiveCard(cards.get(i))) {
				return playerWins;
			}
		}
		
		//Checks for blackjacks
		if(dealer.getTotal() == 21) {
			return dealerWins;
		} else if(player.getTotal() == 21) {
			return playerWins;
		}	
		
		//Loops through actions until a stand is reached
		while(!playerActions.get(playerActionsIndex++).equals("S")) {
			if(!player.receiveCard(cards.get(cardsIndex++))) {
				return dealerWins;
			}
		}
		
		//Gives the rest of the cards to the player
		while(cardsIndex < cards.size()) {
			if(!dealer.receiveCard(cards.get(cardsIndex++))) {
				return playerWins;
			}
		}
		
		//Checks the scores at the end
		if(dealer.getTotal() >= player.getTotal()) {
			return dealerWins;
		} else {
			return playerWins;
		}
	}

	private boolean checkValidSequence(ArrayList<String> actions) {
		String suit;
		String value;
		String action;
		for(int i = 0; i < actions.size(); i++) {
			action = actions.get(i);
			if(action.length() > 3 || action.length() < 1) {
				return false;
			} else if(action.length() == 1) {
				if(!action.equals("H") && !action.equals("S")) {
					return false;
				}
			} else {
				suit = action.substring(0, 1);
				value = action.substring(1);
				
				if(!suits.contains(suit) || !values.contains(value)) {
					return false;
				}
			}
		}
		return true;
	}
}
