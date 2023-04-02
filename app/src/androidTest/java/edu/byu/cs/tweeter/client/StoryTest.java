package edu.byu.cs.tweeter.client;

import android.os.Looper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.PagedTaskObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class StoryTest {

    private StatusService statusServiceSpy;
    private testObserver observer;
    private User user;
    private Status lastItem;
    public List<Status> story;
    boolean hasMorePages;
    private CountDownLatch latch;

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    private void resetCountDownLatch() {
        latch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        latch.await();
        resetCountDownLatch();
    }

    @BeforeEach
    public void setup(){
        statusServiceSpy = Mockito.spy(new StatusService());
        observer = new testObserver();
        resetCountDownLatch();
    }

    protected class testObserver implements PagedTaskObserver<Status>{

        public boolean success = false;

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            story = new ArrayList<>();
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            story = items;
            setHasMorePages(hasMorePages);
            latch.countDown();
            this.success = true;
        }

        @Override
        public void handleFailure(String message) {

        }

        @Override
        public void handleException(Exception exception) {

        }
    }

    @Test
    public void storyTest() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                statusServiceSpy.getStory(user,5,null,observer);
                Looper.loop();
            }
        }).start();
        awaitCountDownLatch();
        List<Status> expectedStory = FakeData.getInstance().getFakeStatuses().subList(0, 5);
        Assertions.assertEquals(5, story.size());
        Assertions.assertEquals(expectedStory, story);
        Assertions.assertTrue(observer.success);
    }

}
