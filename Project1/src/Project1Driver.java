public class Project1Driver{
	public static void main(String[] args) {
			MyBot bot = new MyBot("MyBot");
	}
}
/* A large part of the time being spent on this project was spent trying to understand
how to use gson to populate field in a class and then making classes which correspond
to pieces of data held in a json object, particularly from the weather api.
I had thought that I needed much more in the model than I really did, so there
is a large sum of information being stored on each call which ultimately didn't get used
so never even had an accessor made for it
*/