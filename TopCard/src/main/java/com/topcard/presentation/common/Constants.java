package com.topcard.presentation.common;

public class Constants {
    public static final String REQUIRED = "This field is required.";
    public static final String INVALID_DATE = "Invalid date format. Please use MM/DD/YYYY";
    public static final String DATE_FORMAT = "MM/dd/yyyy";
    public static final String ERROR = "Error";
    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";
    public static final String NO_PLAYER_FOUND = "No player found";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";
    public static final String PASSWORD_NOT_MATCH = "Password does not match";
    public static final String FIRST_NAME_CANNOT_HAVE_SPACES = "First name cannot have spaces.";
    public static final String LAST_NAME_CANNOT_HAVE_SPACES = "First name cannot have spaces.";
    public static final String USERNAME_CANNOT_HAVE_SPACES = "Username cannot have spaces.";
    public static final String PASSWORD_CANNOT_HAVE_SPACES = "Password cannot have spaces.";
    public static final String INVALID_POINT = "Invalid points value";
    public static final String PLAYER_ADDED = "Player added successfully";
    public static final String PLAYER_NOT_ADDED = "Player not added: Already exists";
    public static final String UPDATED = "Player information updated successfully";
    public static final String HOVER_MESSAGE = "Error: Hover on the textbox for detail";
    public static final String SIGN_UP_SUCCESS = "Sign Up Successful";
    public static final String AGREEMENT_CHECKBOX_MSG = "Please agree to the Terms of Use and Privacy Policy to proceed.";
    public static final String AGREEMENT_REQUIRED = "Terms Agreement Required";
    public static final String I_AGREE_TERMS = "<html>I agree to the <a href='#'>Terms of Use</a> and <a href='#'>Privacy Policy</a>.</html>";
    public static final String ALREADY_HAVE_ACCOUNT = "<html>Already have an account? <a href='#'>Login here</a></html>";
    public static final String ABOUT_GAME = "About Game";
    public static final String ABOUT_GAME_INFO = "<html><body style='width: 450px;'>" +
            "<b>About Game</b><br><br>" +
            "This is a GUI-based card game using a standard 52-card deck, supporting 3 to 5 players. " +
            "Before each round begins, players place their bets and the deck is shuffled. Each player is then dealt three cards. " +
            "Based on the cards they receive, players can win or lose money.<br><br>" +
            "<b>Points Calculation:</b><br>" +
            "• Ace is worth 1 point.<br>" +
            "• Cards 2 to 10 are worth their face value.<br>" +
            "• Face cards (J, Q, K) are each worth 10 points.<br><br>" +
            "The player with the highest total points wins. In the event of tied point totals, specific card rankings determine the winner: " +
            "K > Q, Q > J, J > 10, 10 > 9, and so on. If multiple players have the highest point with the same total and card rankings, " +
            "they both win and receive the betting amount from the other players.<br><br>" +
            "<b>Registration</b><br>" +
            "Players must register before entering the game, providing their name, date of birth, and email. " +
            "The login information is securely stored in the database.<br><br>" +
            "<b>Authentication</b><br>" +
            "Players must log in each time before playing, providing correct login information. " +
            "Players must be 18 years old or older to play.<br><br>" +
            "<b>Profile</b><br>" +
            "Players can view their profile information, including name, age, and points.<br><br>" +
            "<b>Update</b><br>" +
            "Players can update their profile information." +
            "</body></html>";
}
