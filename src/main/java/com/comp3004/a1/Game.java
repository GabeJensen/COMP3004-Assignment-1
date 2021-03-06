package com.comp3004.a1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Game {
	private ArrayList<String> suits = new ArrayList<>(Arrays.asList("C","H","D","S"));
	private ArrayList<String> values = new ArrayList<>(Arrays.asList("A", "K", "Q", "J", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
	private final String invalidInputReturn = "Invalid file input";
	private final String invalidSequenceReturn = "Invalid card sequence";
	private final String duplicateCardReturn = "Card has already been played";
	private final String playerWins = "Player Wins";
	private final String dealerWins = "Dealer Wins";
	public Game() {}

	public void playGame() throws IOException {
		Scanner in = new Scanner(System.in);
		String command;
		
		System.out.println("Welcome to BlackJack");
		System.out.print("Enter f for file input or c for console input: ");
		command = in.next();
		while(!command.toLowerCase().equals("f") && !command.toLowerCase().equals("c")) {
			System.out.println("Sorry that is not a proper command. Please try again");
			System.out.print("\nPlease enter f for file input or c for console input: ");
			command = in.next();
		}
		if(command.equals("f")) {
			System.out.print("Enter the directory to the file: ");
			command = in.next();
			System.out.println(file(command));
		} else {
			playConsole();			
		}
	}
	public void playConsole() {
		Player player = new Player();
		Player dealer = new Player();
		Deck deck = new Deck();
		deck.shuffleDeck();
		
		player.receiveCard(deck.dealCard());
		player.receiveCard(deck.dealCard());
		
		dealer.receiveCard(deck.dealCard());
		dealer.receiveCard(deck.dealCard());
		
		System.out.println("Player's cards");
		System.out.println(player.getCards());
		
		System.out.println("Dealer's cards");
		System.out.println(dealer.getDealerCards());
		
		System.out.print("Your score: " + player.getTotal());
		
		if(player.getTotal() == 21) {
			System.out.println("Player Blackjack!");
			if(dealer.getTotal() == 21) {
				System.out.println("Dealer Blackjack!");
			}
			if(!playDealer(dealer, deck)) {
				System.out.println("Dealer busted with a total of: " + dealer.getTotal());
			}
		} else {
			if(playPlayer(player, deck)) {
				if(!playDealer(dealer, deck)) {
					System.out.println("Dealer busted with a total of: " + dealer.getTotal());
				}			
			}
		}
		
		if(checkWinner(player,dealer)) {
			System.out.println("\nPlayer Wins");
		} else {
			System.out.println("\nDealer Wins");
		}
		System.out.println("\nYour hand:     " + player.getCards());
		System.out.println("Dealer's hand: " + dealer.getCards());
		
		System.out.println("\nYour total:     " + player.getTotal());
		System.out.println("Dealer's total: " + dealer.getTotal());
		
	}
	
	public boolean checkWinner(Player player, Player dealer) {
		if(player.getTotal() > 21) {
			return false;
		} else if (dealer.getTotal() > 21) {
			return true;
		}
		
		if(player.getTotal() <= dealer.getTotal()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean playPlayer(Player player, Deck deck) {
		Scanner in = new Scanner(System.in);
		String command;
		Card drawedCard;
		boolean bust = true;
		while(bust) {
			System.out.print("\nPlease enter H to hit or S to stand: ");
			command = in.next();
			while(!command.toLowerCase().equals("h") && !command.toLowerCase().equals("s")) {
				System.out.println("Sorry that is not a proper command. Please try again");
				System.out.print("\nPlease enter H to hit or S to stand: ");
				command = in.next();
			}
			
			if(command.toLowerCase().equals("h")) {
				drawedCard = deck.dealCard();
				System.out.println("You drew: " + drawedCard.getFullName());
				bust = player.receiveCard(drawedCard);			
			} else {
				break;
			}
			if(!bust) {
				System.out.println("You went bust!");
				System.out.println("Your current cards: " + player.getCards());
				System.out.println("Your current total: " + player.getTotal());
				in.close();
				break;
			} else {
				System.out.println("Your current cards: " + player.getCards());
				System.out.println("Your current total: " + player.getTotal());
			}
		}
		System.out.println("Your current cards: " + player.getCards());
		System.out.println("Your current total: " + player.getTotal());
		in.close();
		return bust;
	}

	public boolean playDealer(Player dealer, Deck deck) {
		boolean bust = true;
		Card drawedCard;
		while(dealer.getTotal() <= 16 || (dealer.getTotal() == 17 && dealer.hasAce())) {
			drawedCard = deck.dealCard();
			System.out.println("Dealer drew: " + drawedCard.getFullName());
			bust = dealer.receiveCard(drawedCard);
		}
		
		return bust;
	}

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
		if(!checkValidCharacters(actions)) {
			return invalidInputReturn;
		}
		
		if(!checkValidSequence(actions)) {
			return invalidSequenceReturn;
		}
		
		//Add cards to their respective arrays
		for(int i = 0; i < actions.size(); i++) {
			if(actions.get(i).length() >= 2) {
				cards.add(new Card(actions.get(i)));
			} else {
				playerActions.add(actions.get(i));
			}
		}
		
		//Check for duplicates
		if(!checkDuplicateCards(cards)) {
			return duplicateCardReturn;
		}
		
		int cardsIndex = 4;
		int playerActionsIndex = 0;
		
		//First two cards are for the player
		for(int i = 0; i < 2; i++) {
			if(!player.receiveCard(cards.get(i))) {
				displayCards(player,dealer);
				return dealerWins;
			}
		}
		
		//Next two are for the dealer
		for(int i = 2; i < 4; i++) {
			if(!dealer.receiveCard(cards.get(i))) {
				displayCards(player,dealer);
				return playerWins;
			}
		}
		
		//Checks for blackjacks
		if(dealer.getTotal() == 21) {
			displayCards(player,dealer);
			return dealerWins;
		} else if(player.getTotal() == 21) {
			displayCards(player,dealer);
			return playerWins;
		}	
		
		//Loops through actions until a stand is reached
		while(!playerActions.get(playerActionsIndex++).equals("S")) {
			if(!player.receiveCard(cards.get(cardsIndex++))) {
				displayCards(player,dealer);
				return dealerWins;
			}
		}
		
		//Gives the rest of the cards to the player
		while(cardsIndex < cards.size()) {
			if(!dealer.receiveCard(cards.get(cardsIndex++))) {
				displayCards(player,dealer);
				return playerWins;
			}
		}
		
		//Checks the scores at the end
		if(dealer.getTotal() >= player.getTotal()) {
			displayCards(player,dealer);
			return dealerWins;
		} else {
			displayCards(player,dealer);
			return playerWins;
		}
	}

	private void displayCards(Player player, Player dealer) {
		System.out.println("Player's cards: " + player.getCards());
		System.out.println("Dealer's cards: " + dealer.getCards());
		
		System.out.println("Player's score: " + player.getTotal());
		System.out.println("Dealer's score: " + dealer.getTotal());
	}

	private boolean checkDuplicateCards(ArrayList<Card> cards) {
		ArrayList<String> cardStr = new ArrayList<>();
		for (Card card : cards) {
			if(cardStr.contains(card.getFullName())) {
				return false;
			}
			cardStr.add(card.getFullName());
		}
		return true;
	}

	private boolean checkValidCharacters(ArrayList<String> actions) {
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
	
	private boolean checkValidSequence(ArrayList<String> actions) {
		if(actions.size() < 4) {
			return false;
		}
		
		for(int i = 0; i < 4; i++) {
			if(actions.get(i).length() == 1) {
				return false;
			}
		}
		
		int count = 4;
		
		while(count < actions.size()) {
			if(count % 2 == 0) {
				if(actions.get(count) == "S") {
					count++;
					break;
				}
				if(!(actions.get(count).length() == 1)) {
					count++;
					return false;
				}
				count++;
			} else {
				if(!(actions.get(count).length() >= 2)) {
					count++;
					return false;
				}
				count++;
			}
		}
		
		while(count++ < actions.size()) {
			if(actions.get(count).length() == 1) {
				return false;
			}
		}
		
		return true;
	}

	public static void main(String[] args) throws IOException {
		Game game = new Game();
		
		game.playGame();
	}
}
