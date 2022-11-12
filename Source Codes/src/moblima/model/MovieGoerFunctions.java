package moblima.model;

import java.util.*;
import java.time.format.DateTimeFormatter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import moblima.model.BookingInfo;
import moblima.model.Cineplex;
import moblima.model.Movie;
import moblima.model.master;
import moblima.model.show;
import moblima.controller.Movie_goer_IO;
import moblima.controller.HolidayConfig;

public class MovieGoerFunctions {
	
	Scanner sc = new Scanner(System.in);
	Movie_goer_IO mg = new Movie_goer_IO();
	
	public enum AgeCat{
		CHILD(0, 12),
		STUDENT(13, 18),
		ADULT(19, 54),
		SENIOR_CITIZEN(55, 100);
		
		private final int low;
		private final int high;
		private final static Set<Integer> ages = new HashSet<Integer>(AgeCat.values().length);
		
		private AgeCat(int low, int high) {
			this.low = low;
			this.high = high;
		}
		
		public static boolean contains (int ageVal) {
			return ages.contains(ageVal);
		}
	}

    public MovieGoerFunctions() {}

    public void ViewMovies(ArrayList<Movie> movies, int numOfMovies) {
        // Movie_goer can have a glance at a full list of all movies currently showing.
        // For example:
        // 1. Black Adam
        // 2. Come Back Home
        // 3. Black Panther - Wakanda Forever
        // Movie = ['Black Adam', 'Come Back Home', 'Black Panther - Wakanda Forever']
        // numOfMovies = 3
        Movie movie;

        movie = selectMovie(movies, numOfMovies);
        ListMovieDetails(movie);
        Review(movie);

    }

    public void ListMovieDetails(Movie movie) {
        int c, j, s;
        show show;

        System.out.println("Movie Title: " + movie.getMovieName());
        System.out.println("Directed By: " + movie.getDirectorName());

        System.out.println("Cast: ");
        String[] cast = movie.getCast();
        for (c = 0; c < cast.length; c++) {
            System.out.printf("%s \n", cast[c]);
            if (cast[c].equals("null"))
                continue;
        }
        System.out.println("Showing Status: " + movie.getShowingStatus());

        System.out.println("Movie Synopsis: ");
        System.out.println(movie.getSynopsis());

        System.out.println("Review(s) of the movie: ");
        String[] reviews = movie.getReviews();
        if (reviews.length != 0) {
            for (j = 0; j < reviews.length; j++) {
                System.out.printf("%s \n", reviews[j]);
            }
        } else
            System.out.println("No reviews available.");

        System.out.println("Movie Rating(s): ");
        double[] ratings = movie.getAllRatings();
        if (ratings.length != 0) {
            for (j = 0; j < ratings.length; j++) {
                System.out.printf("%s \n", ratings[j]);
            }
        } else
            System.out.println("No ratings available.");

        System.out.println(" ");
        System.out.println("-------------------------");
        System.out.println(" ");
        System.out.println("--- Shows available for this movie --- ");

        ArrayList<show> showsForSelectedMovie = movie.getShows();

        for (s = 0; s < showsForSelectedMovie.size(); s++) {
            show = showsForSelectedMovie.get(s);
            System.out.printf("Show %d\n", s + 1);
            System.out.println("Showtimes: ");
            System.out.println(show.getDateTime());
            System.out.printf("Cineplex: %d\n", show.getCineplexID() + 1);
            System.out.printf("Theatre: %d\n", show.getScreenNum() + 1);
            System.out.println("\n-------------------------");
        }
    }

    public void Review(Movie movie) {
        // Movie_goer can enter his / her review and rating for a particular movie.
        int choice = 0;

        System.out.println("Would you like to:\n"
                + "1. Review this movie?\n"
                + "2. Rate this movie?\n"
                + "3. Skip for now.\n");

        choice = sc.nextInt();
        do {
            switch (choice) {
                case 1:
                    System.out.println("Write your review for this movie.");
                    String review = sc.nextLine();
                    movie.writeReview(review);
                    System.out.println("New review added to the movie!");
                    break;
                case 2:
                    System.out.println("Rate this movie, from 0 to 5.");
                    double rating = sc.nextDouble();
                    movie.giveRating(rating);
                    System.out.println("New rating added to the movie!");
                    break;
            }
        } while (choice != 3);
        return;
    }

    public void CheckSeat() {
        // Movie_goer can check for empty seats in a cineplex before booking.
        master m = new master();
        m.readCineplexes();
        int cinemaChoice = 0;
        Cineplex cineplexChoice;

        System.out.println("--- Seat Availability ---");

        ArrayList<Cineplex> Cineplexes = new ArrayList<Cineplex>();
        Cineplexes = m.getCineplexes();

        cineplexChoice = selectCineplex(Cineplexes, Cineplexes.size());

        System.out.println("Select theatre - 1, 2 or 3:");
        cinemaChoice = sc.nextInt();
        while (cinemaChoice > 3) {
            System.out.println("Invalid input!");
            sc.nextLine();
        }

        System.out.println("--- All available movies at selected theatre ---");
        ArrayList<show> movieList = cineplexChoice.getCinema().get(cinemaChoice - 1).getCinemaShows();

        for (show show : movieList)
            show.printSeats();
    }

    public Movie selectMovie(ArrayList<Movie> movies, int numOfMovies) {
        // List out all available movies that are Now Showing or for Preview. ->
        // Movie.getStatus()
        // ListMovie();
        // User chooses the movie he / she wants to watch at the cinema.
        int i;
        int movie_index = 0;
        Movie movie = null;

        do {
            System.out.println("Select a movie below: ");
            System.out.println("--- Movie Listing ---");
            for (i = 0; i < numOfMovies; i++)
                System.out.printf(i + 1 + ". " + movies.get(i).getMovieName() + "\n");

            try {
                movie_index = sc.nextInt();

            } catch (Exception e) {
                System.err.println("Invalid input!");
                sc.nextLine();
            }

            if (movie_index > 0 && movie_index <= numOfMovies) {
                movie = movies.get(movie_index - 1);
                break;
            }

        } while (movie_index > numOfMovies);
        
        System.out.println(movie.getMovieName());
        return movie;
    }

    public Cineplex selectCineplex(ArrayList<Cineplex> Cineplexes, int numOfCineplexes) {
        int i;
        int cineplexChoice = 0;
        Cineplex cineplex;

        do {
            System.out.println("Select Cineplex: ");
            System.out.println("--- Cineplex Listing ---");
            for (i = 0; i < numOfCineplexes; i++)
                System.out.printf(i + 1 + ". " + Cineplexes.get(i).showLocation() + "\n");

            try {
                cineplexChoice = sc.nextInt();
            } catch (Exception e) {
                System.err.println("Invalid input!");
                sc.nextLine();
            }

            cineplex = Cineplexes.get(cineplexChoice - 1);

        } while (cineplexChoice > numOfCineplexes);
        
        System.out.println(cineplex.showLocation());
        return cineplex;
    }

    public int selectDate() {
        // List out the next 5 dates (in advance), from the day the user logs into the
        // app.
        int dateChoice;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currDate = LocalDate.now();
        String date = currDate.format(dateFormatter);
        LocalDate parsedCurrDate = LocalDate.parse(date, dateFormatter);

        for (int i = 0; i <= 5; i++) {
            String dateAfterCurrDate = LocalDate.parse(date).plusDays(i).toString();
            System.out.printf(i + 1 + ". " + dateAfterCurrDate + "\n");
        }

        dateChoice = sc.nextInt();
        return dateChoice;
    }

    public int selectTime(int movieChoice, int cineplexChoice, int dateChoice) {
        // List out all the available time slots of when the user's desired (chosen)
        // movie will be screened on a chosen date.
        // Cinema.getShowtime();
        int timeChoice;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime currTime = LocalTime.now();
        String time = currTime.format(timeFormatter);
        LocalTime parsedCurrTime = LocalTime.parse(time, timeFormatter);

        // get movie showtimes on the chosen day (use switch case statement, based on
        // dateChoice)
        // list out movie showtimes that are available for booking, if they are beyond
        // parsedCurrTime, otherwise do not show
        timeChoice = sc.nextInt();
        return timeChoice;
    }
    
    public void PopularMovies(ArrayList<Movie> movieList) {
        // Movie_goer can view the top 5 movies ranked / sorted based on ticket sales or
        // overall reviewrs' ratings.
        // popularMovie[] ?
        int i;
        int selection = 0;

        do {
            System.out.println("--- HIGHLY RATED MUST-WATCH MOVIES! ---");
            System.out.println("Here are the top 5 rated movies, based on: \n"
                    + "1. Overall ratings by reviewers \n"
                    + "2. Overall ticket sales \n"
                    + "3. Back \n");
            System.out.println("Select an option: ");
            try {
                selection = sc.nextInt();

                switch (selection) {
                    case 1:
                        Collections.sort(movieList, Movie.topratings);
                        System.out.println(
                        "Top 5 rated movies based on overall reviewers' ratings, with first movie having highest rating: ");
                        for (i = movieList.size() - 1; i >= 0; i--)
                            System.out.println(
                                    movieList.get(i).getMovieName() + ": " + movieList.get(i).getTotalRating());
                        break;
                    case 2:
                        Collections.sort(movieList, Movie.topticketsales);
                        System.out.println(
                        "Top 5 rated movies based on overall ticket sales, with first movie having highest ticket sales: ");
                        for (i = movieList.size() - 1; i >= 0; i--)
                            System.out.println(movieList.get(i).getMovieName() + ": " + movieList.get(i).getTsales());
                        break;
                }
            } catch (Exception e) {
                System.err.println("Invalid input!");
                sc.nextLine();
                //throw e;
            }

        } while (selection != 3);
        return;
    }

    public void BookTickets() throws FileNotFoundException {
        // Movie_goer can book and purchase movie ticket(s) for a particular chosen
        // movie.
        BookingInfo booking = new BookingInfo();
        int movieChoice;
        int cineplexChoice;
        int dateChoice;
        int timeChoice;
        
        master Master = new master();
        ArrayList<Movie> moviesAvailableForBooking = new ArrayList<Movie>();
        moviesAvailableForBooking = Master.getMovies();
        Movie m;
        show show;
        Movie_goer_IO mg = new Movie_goer_IO();
        Movie_goer user = new Movie_goer();
        
        int cust_id = 0;
        int movie_ID = 0;
        int selection;
        String movieBooked, showtime, firstSeat, cust_name, cust_email, ageCat, bookingConfirmation, cancel, transaction_id;
        int numSeats, cust_mobile, cust_age, show_index;
        boolean validAge;
        
        String[] holidayList = null;
        boolean publicHols = false;
        HolidayConfig holIO = new HolidayConfig();
        holidayList = holIO.readHolidays();  
        String cinemaClass;

        System.out.println("--- Ticket Booking & Purchase ---");
        
        try {
        	System.out.println("Enter your customer ID to proceed: ");
        	cust_id = sc.nextInt();
        } catch (Exception e) {
            System.err.println("Invalid input!");
            sc.nextLine();
        }
        
        System.out.println("Enter your name: ");
        cust_name = sc.next();
        
        System.out.println("Enter your mobile number: ");
        cust_mobile = sc.nextInt();
        
        System.out.println("Enter your email address: ");
        cust_email = sc.next();
        
        do {
        	ageCat = null;
        	selection = 0;
        	System.out.println("Choose your age category: "
            		+ "1. Senior Citizen \n"
            		+ "2. Adult \n"
            		+ "3. Student \n"
            		+ "4. Child \n\n"
            		+ "Note: \n"
            		+ "- Senior Citizen: Aged 55 and above  \n"
            		+ "- Student: Between 13 and 18 years old \n"
            		+ "- Child: Below 12 years old");
        	
        	try {
        		// to be referenced with enum Age in Movie_goer.java
        		selection = sc.nextInt();
        		switch (selection) {
        			case 1:
        				System.out.println("Validation of your age and identity will be carried out physically at the cineplex.");
        				ageCat = "Senior Citizen";
        				break;
        			case 2:
        				System.out.println("Enter your age for validation of your eligibility for an ADULT ticket: ");
        				cust_age = sc.nextInt();
        				validateAge(cust_age);
        				ageCat = "Adult";
        				break;
        			case 3:
        				System.out.println("Enter your age for validation of your eligibility for a STUDENT ticket: ");
        				cust_age = sc.nextInt();
        				validateAge(cust_age);
        				ageCat = "Student";
        				break;
        			case 4:
        				System.out.println("Enter your age for validation of your eligibility for a CHILD ticket: ");
        				cust_age = sc.nextInt();
        				validateAge(cust_age);
        				ageCat = "Child";
        				break;
        		}
        	} catch (Exception e) {
                System.err.println("Invalid input!");
                sc.nextLine();
        	}
        } while (selection > 4);
        System.out.println("Invalid input!");
        
        m = selectMovie(moviesAvailableForBooking, moviesAvailableForBooking.size());        

        ArrayList<show> showsOfSelectedMovie = m.getShows();
        
        for (int i = 0; i < showsOfSelectedMovie.size(); i++) {
        	publicHols = false;
        	show = showsOfSelectedMovie.get(i);
        	showtime = show.getDateTime().split(" ")[0];
        	
        	for (int h = 0; h < holidayList.length; h++) {
        		if (holidayList[h].equals(showtime)) {
        			publicHols = true;
        			break;
        		}
        	}
        	
			cinemaClass = Master.getCineplexes().get(show.getCineplexID()).getCinema().get(show.getScreenNum()).getCinemaClass();
			// MovieTicket price = new MovieTicket(show.get3D(), movieDetails, ageCat, publicHols);
			MovieTicket price = new MovieTicket(String typeofmovie, String cinemaclass, int age, int date);
			System.out.println(" ");
			System.out.printf("\nShow %d:\n", i+1);
			System.out.println("Date & Time: \n" + show.getDateTime());
			System.out.printf("Cineplex ID: %d\n", show.getCineplexID()+1);
			System.out.printf("Cinema ID: %d\n", show.getScreenNum()+1);
			System.out.printf("Cinema Class: %s\n", cinemaClass);
			System.out.printf("3D Movie? %s\n", show.get3D());
			// System.out.printf("Ticket Price: S$%s (Inclusive of GST)\n", price.getPrice());
			System.out.println("----------------------------------------------");
        }
        
        System.out.println("Enter show index: ");
        show_index = sc.nextInt() - 1;
        
        show = showsOfSelectedMovie.get(show_index);
        show.printSeats();
        
        System.out.println("Enter the total number of seats: ");
        numSeats = sc.nextInt();
        
        System.out.println("Enter the first seat: "); // seat ID formatting to be confirmed
        firstSeat = sc.nextLine();
        
        System.out.println("Confirm booking? Y/N");
        bookingConfirmation = sc.nextLine();
        if (bookingConfirmation == "N") {
        	System.out.println("Confirm cancellation? Y/N");
        	cancel = sc.nextLine();
        	if (cancel == "Y") {
        		System.out.println("Booking cancelled!");
        	}
        }
        else {
        	System.out.println("Booking ID: " + transaction_id);
        	System.out.println("Movie tickets successfully booked!");
        }
        
        System.out.println("Select a timeslot: ");
        timeChoice = selectTime(movieChoice, cineplexChoice, dateChoice);

        booking.getTID();
        booking.getSeatNum();
        booking.getDateTime();
        
        return;
    }
    
    public void viewBookingHistory() throws IOException {
		// Movie_goer can browse through his / her past movie bookings with ease.
		int cust_id = 0;
		int i;
		ArrayList<BookingInfo> bookings = new ArrayList<>();
		Movie_goer customer = new Movie_goer();
    	
		try {
        	System.out.println("Enter your customer ID to proceed: ");
        	cust_id = sc.nextInt();
        } catch (Exception e) {
            System.err.println("Invalid input!");
            sc.nextLine();
        }
    	
    	customer = mg.getMovieGoer(cust_id);
    	
    	bookings = customer.getBooking();
    	
    	System.out.println("--- Your Past Bookings ---");
    	
    	for (i = 0; i < bookings.size(); i++) {
    		System.out.println("BOOKING " + (i+1) + ": ");
			System.out.println("Transaction ID: " + bookings.get(i).getTID());
			System.out.println("Customer ID: " + cust_id);
			System.out.println("Customer Name: " + bookings.get(i).getCustName());
    		System.out.println("Customer Email ID: " + bookings.get(i).getEmailAddress());
			System.out.println("Movie Booked: " + bookings.get(i).getMovieBooked());
			System.out.println("Date & Time: " + bookings.get(i).getDateTime());
			System.out.println("Total number of seats booked: " + bookings.get(i).getSeatNum());
			System.out.println("First seat booked: " + bookings.get(i).getFirstSeat());
    	}
	}
    
    public void validateAge(int ageVal) {
		// Movie_goer needs to validate their age when purchasing tickets online, except if he / she is a senior citizen then
		// he / she will only validate his / her age upon entering the cinema.
		boolean validAge;
    	
    	validAge = AgeCat.contains(ageVal);
    	if (validAge == true)
			System.out.println("You are eligible to purchase tickets of this age category!");
		else
			System.out.println("You are NOT eligible to purchase tickets of this age category!");
	}

}