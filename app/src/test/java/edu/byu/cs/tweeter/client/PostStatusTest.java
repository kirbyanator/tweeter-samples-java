package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.client.presenter.view.MainView;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusTest {
    private MainActivityPresenter mainPresenterSpy;
    private MainView mockView;

    private final String testSuccessPost = "I am a post";
    private final String testErrorPost = "Error post";
    private final String testExceptionPost = "Exception post";
    private final String testExceptionMessage = "an example of an exception message";

    @BeforeEach
    public void setup(){
        mockView = Mockito.mock(MainView.class);
        mainPresenterSpy = Mockito.spy(new MainActivityPresenter(mockView));
        StatusService mockService = Mockito.mock(StatusService.class);
        Mockito.when(mainPresenterSpy.statusServiceFactory()).thenReturn(mockService);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Status status = invocation.getArgument(0);
                MainActivityPresenter.PostStatusObserver observer = invocation.getArgument(1);

                Assertions.assertNotNull(status);
                Assertions.assertNotNull(observer);

                if(status.post.equals(testSuccessPost)){
                    Assertions.assertEquals(status.post, testSuccessPost);
                    observer.handleSuccess();
                }
                else if(status.post.equals(testErrorPost)){
                    Assertions.assertEquals(status.post, testErrorPost);
                    observer.handleFailure(testErrorPost);
                }
                else if(status.post.equals(testExceptionPost)){
                    Assertions.assertEquals(status.post, testExceptionPost);
                    observer.handleException(new Exception(testExceptionMessage));
                }

                return null;
            }
        }).when(mockService).postStatus(Mockito.any(), Mockito.any());
    }

    @Test
    public void testSuccessfulPost(){
        mainPresenterSpy.postStatus(testSuccessPost);
        Mockito.verify(mockView).displayMessage("Posting status...");
        Mockito.verify(mockView).displayMessage("Successfully Posted!");
    }

    @Test
    public void testErrorPost(){
        mainPresenterSpy.postStatus(testErrorPost);
        Mockito.verify(mockView).displayMessage("Posting status...");
        Mockito.verify(mockView).displayMessage("Failed to post status: " + testErrorPost);
    }

    @Test
    public void testExceptionPost(){
        mainPresenterSpy.postStatus(testExceptionPost);
        Mockito.verify(mockView).displayMessage("Posting status...");
        Mockito.verify(mockView).displayMessage("Failed to post status because of exception " + testExceptionMessage);
    }



}
