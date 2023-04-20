package edu.byu.cs.tweeter.client;

import static org.mockito.Mockito.times;

import android.os.Looper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class StoryIntegrationTest {
    private CountDownLatch countDownLatch;

    private MainActivityPresenter mainPresenterSpy;

    private MainActivity mockView;

    private ServerFacade serverFacade;

    private LoginRequest loginRequest;

    private AuthenticationResponse authResponse;

    private AuthToken token;

    @BeforeEach
    public void setup(){
        loginRequest = new LoginRequest("@davis", "davis");
        serverFacade = new ServerFacade();
        try{
            authResponse = serverFacade.login(loginRequest, "/login");
        } catch (Exception e) {
            e.printStackTrace();
        }
        token = authResponse.getAuthToken();
        Cache.getInstance().setCurrUser(authResponse.getUser());
        Cache.getInstance().setCurrUserAuthToken(authResponse.getAuthToken());

        mockView = Mockito.mock(MainActivity.class);
        mainPresenterSpy = Mockito.spy(new MainActivityPresenter(mockView));


        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                var parameter = (String)invocationOnMock.getArgument(0);
                Assertions.assertNotNull(parameter);
                System.out.println("argument for view is " + parameter);
                countDownLatch.countDown();
                return null;
            }
        }).when(mockView).displayMessage(Mockito.anyString());

        resetCountDownLatch();

    }

    @Test
    public void postStatus() throws InterruptedException, IOException, TweeterRemoteException {
        System.out.println("Starting test");
        User davisUser = new User("davis", "forster", "@davis", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis");
        List<String> urls = new ArrayList<>();
        List<String> mentions = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mainPresenterSpy.postStatus("Here is my test post!");
                Looper.loop();
            }
        }).start();
        awaitCountDownLatch();

        Mockito.verify(mockView,times(1)).displayMessage("Successfully Posted!");

        StoryRequest storyRequest = new StoryRequest(token, "@davis", 10, null);
        StoryResponse storyResponse = serverFacade.getStory(storyRequest, "/getstory");

        Status mostRecentPost = storyResponse.getItems().get(0);
        Assertions.assertEquals("Here is my test post!", mostRecentPost.getPost());
        Assertions.assertEquals("@davis", mostRecentPost.getUser().getAlias());
        Assertions.assertEquals(0, mostRecentPost.getMentions().size());
    }

    public static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

}
