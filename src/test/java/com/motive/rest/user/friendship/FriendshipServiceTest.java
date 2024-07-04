package com.motive.rest.user.friendship;

import com.motive.rest.Auth.AuthDetails;
import com.motive.rest.Auth.AuthService;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(JUnit4.class)
class FriendshipServiceTest {

    @Mock
    AuthService authService;
    @Mock
    private UserService userService;

    @Mock
    private FriendRepo repo;

    @InjectMocks
    FriendshipService friendshipService;

    @Test
    public void testGetFriends() {
        // Mock authenticated user
        User authUser = new User("authUser");
        authUser.setId(UUID.randomUUID());
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));
        authUser.setAuthDetails(authDetails);
        authUser.setHideStatusFrom(new HashSet<>());

        when(authService.getAuthUser()).thenReturn(authUser);

        // Mock data for approved friendships
        User friend1 = new User("friend1");
        friend1.setId(UUID.randomUUID());
        friend1.setAuthDetails(authDetails);
        friend1.setHideStatusFrom(new HashSet<>());
        User friend2 = new User("friend2");
        friend2.setId(UUID.randomUUID());
        friend2.setAuthDetails(authDetails);
        friend2.setHideStatusFrom(new HashSet<>());

        List<Friendship> approvedFriendships = Arrays.asList(
                new Friendship(authUser, friend1),
                new Friendship(friend2, authUser)
        );

        // Mock the repository's behavior
        when(repo.findApprovedRequests(any())).thenReturn(approvedFriendships);

        // Call the method to test
        List<User> friends = friendshipService.getFriends();

        // Assertions
        assert friends.size() == 2;
        assert friends.contains(friend1);
        assert friends.contains(friend2);
    }


    @Test
    public void testGetRequestsReceivedIncludeApproved() {
        // Mock authenticated user
        User authUser = new User("authUser");
        authUser.setId(UUID.randomUUID());
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));
        authUser.setAuthDetails(authDetails);
        authUser.setHideStatusFrom(new HashSet<>());

        // Mock data for both pending and approved requests received
        User friend1 = new User("friend1");
        friend1.setId(UUID.randomUUID());
        friend1.setAuthDetails(authDetails);
        friend1.setHideStatusFrom(new HashSet<>());
        User friend2 = new User("friend2");
        friend2.setId(UUID.randomUUID());
        friend2.setAuthDetails(authDetails);
        friend2.setHideStatusFrom(new HashSet<>());
        List<Friendship> receivedRequests = Arrays.asList(
                new Friendship(friend1, authUser),
                new Friendship(authUser, friend2)
        );

        // Mock the repository's behavior
        when(authService.getAuthUser()).thenReturn(authUser);
        when(repo.findRequestsRecieved(any(UUID.class), any(boolean.class))).thenReturn(receivedRequests);

        // Call the method to test
        List<User> receivedUsers = friendshipService.getRequestsRecieved(true);

        // Assertions
        assert receivedUsers.size() == 2;
        assert receivedUsers.contains(friend1);
        assert receivedUsers.contains(friend2);
    }

    @Test
    public void testGetRequestsReceivedExcludeApproved() {
        // Mock authenticated user
        User authUser = new User("authUser");
        authUser.setId(UUID.randomUUID());
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));
        authUser.setAuthDetails(authDetails);
        authUser.setHideStatusFrom(new HashSet<>());
        when(authService.getAuthUser()).thenReturn(authUser);

        // Mock data for only pending requests received
        User friend1 = new User("friend1");
        friend1.setId(UUID.randomUUID());
        friend1.setAuthDetails(authDetails);
        friend1.setHideStatusFrom(new HashSet<>());

        List<Friendship> pendingRequests = Arrays.asList(
                new Friendship(friend1, authUser)
        );

        // Mock the repository's behavior
        when(repo.findRequestsRecieved(any(UUID.class), any(boolean.class))).thenReturn(pendingRequests);

        // Call the method to test
        List<User> pendingUsers = friendshipService.getRequestsRecieved(false);

        // Assertions
        assert pendingUsers.size() == 1;
        assert pendingUsers.contains(friend1);
    }

    @Test
    public void testGetRequestsSentIncludeApproved() {
        // Mock authenticated user
        User authUser = new User("authUser");
        authUser.setId(UUID.randomUUID());
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));
        authUser.setAuthDetails(authDetails);
        authUser.setHideStatusFrom(new HashSet<>());
        when(authService.getAuthUser()).thenReturn(authUser);

        // Mock data for both pending and approved requests sent
        User friend1 = new User("friend1");
        friend1.setId(UUID.randomUUID());
        friend1.setAuthDetails(authDetails);
        friend1.setHideStatusFrom(new HashSet<>());
        User friend2 = new User("friend2");
        friend2.setId(UUID.randomUUID());
        friend2.setAuthDetails(authDetails);
        friend2.setHideStatusFrom(new HashSet<>());

        List<Friendship> sentRequests = Arrays.asList(
                new Friendship(authUser, friend1),
                new Friendship(friend2, authUser)
        );

        // Mock the repository's behavior
        when(repo.findRequestsSent(any(UUID.class), any(boolean.class))).thenReturn(sentRequests);

        // Call the method to test
        List<User> sentUsers = friendshipService.getRequestsSent(true);

        // Assertions
        assert sentUsers.size() == 2;
        assert sentUsers.contains(friend1);
        assert sentUsers.contains(friend2);
    }

    @Test
    public void testGetRequestsSentExcludeApproved() {
        // Mock authenticated user
        User authUser = new User("authUser");
        authUser.setId(UUID.randomUUID());
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));
        authUser.setAuthDetails(authDetails);
        authUser.setHideStatusFrom(new HashSet<>());
        when(authService.getAuthUser()).thenReturn(authUser);

        // Mock data for only pending requests sent
        User friend1 = new User("friend1");
        friend1.setId(UUID.randomUUID());
        friend1.setAuthDetails(authDetails);
        friend1.setHideStatusFrom(new HashSet<>());

        List<Friendship> pendingRequests = Arrays.asList(
                new Friendship(authUser, friend1)
        );

        // Mock the repository's behavior
        when(repo.findRequestsSent(any(UUID.class), any(boolean.class))).thenReturn(pendingRequests);

        // Call the method to test
        List<User> pendingUsers = friendshipService.getRequestsSent(false);

        // Assertions
        assert pendingUsers.size() == 1;
        assert pendingUsers.contains(friend1);
    }

    @Test
    public void testValidateFriendshipNotFriends() {
        // Mock authenticated user
        User authUser = new User("authUser");
        authUser.setId(UUID.randomUUID());
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));
        authUser.setAuthDetails(authDetails);
        authUser.setHideStatusFrom(new HashSet<>());
        when(authService.getAuthUser()).thenReturn(authUser);

        // Mock data for a user that is not friends with the authenticated user
        User friend1 = new User("friend1");
        friend1.setId(UUID.randomUUID());
        friend1.setAuthDetails(authDetails);
        friend1.setHideStatusFrom(new HashSet<>());

        // Mock the repository's behavior
        when(userService.findByUsername(any())).thenReturn(friend1);

        List<Friendship> approvedRequests = Arrays.asList(
                new Friendship(authUser, friend1)
        );

        when(repo.findApprovedRequests(any())).thenReturn(approvedRequests);

        // Test the method
        friendshipService.validateFriendship("friend1");

        verify(authService, times(2)).getAuthUser();
        verify(userService, times(1)).findByUsername(any());
    }


// TODO: This exception *might* occur in wrongly written multi-threaded tests.

//    @Test
//    public void testIsFriendsTrue() {
//        // Mock authenticated user
//        User authUser = new User("authUser");
//        authUser.setId(UUID.randomUUID());
//        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));
//        authUser.setAuthDetails(authDetails);
//        authUser.setHideStatusFrom(new HashSet<>());
//        when(authService.getAuthUser()).thenReturn(authUser);
//
//        // Mock data for friends
//        User friend1 = new User("friend1");
//        friend1.setId(UUID.randomUUID());
//        friend1.setAuthDetails(authDetails);
//        friend1.setHideStatusFrom(new HashSet<>());
//
//
//
//        List<Friendship> approvedFriendships = Arrays.asList(
//                new Friendship(friend1, authUser)
//        );
//
//        // Mock the repository's behavior
//        when(repo.findApprovedRequests(any(String.class))).thenReturn(approvedFriendships);
//
//
//        // Mock the service method getFriends
//        when(friendshipService.getFriends()).thenReturn(Arrays.asList(friend1));
//
//        // Test the method and assert that it returns true
////        assertTrue(friendshipService.isFriends(friend1));
//    }


    @Test
    void getFriendshipWithUser() {
    }

    @Test
    void removeFriendship() {
    }

    @Test
    void respondToRequest() {
    }

    @Test
    void createRequest() {
    }

    @Test
    void searchUsers() {
    }

    @Test
    void getSocialSummaryDTO() {
    }
}