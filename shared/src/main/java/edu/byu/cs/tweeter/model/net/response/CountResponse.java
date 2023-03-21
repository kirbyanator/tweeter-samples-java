package edu.byu.cs.tweeter.model.net.response;

public class CountResponse extends Response{

    private int count;

    /**
     * Creates a response indicating that the corresponding request was unsuccessful.
     *
     * @param message a message describing why the request was unsuccessful.
     */
    public CountResponse(String message) {
        super(false, message);
        this.count = 0;
    }

    /**
     * Creates a response indicating that the corresponding request was successful.
     */
    public CountResponse(int count) {
        super(true, null);
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
