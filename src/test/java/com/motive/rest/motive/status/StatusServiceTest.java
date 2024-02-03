package com.motive.rest.motive.status;

import com.motive.rest.Auth.AuthDetails;
import com.motive.rest.Auth.AuthService;
import com.motive.rest.exceptions.EntityNotFound;
import com.motive.rest.exceptions.UnauthorizedRequest;
import com.motive.rest.motive.status.dto.StatusBrowseDTO;
import com.motive.rest.notification.NotificationService;
import com.motive.rest.user.User;
import com.motive.rest.user.friendship.FriendshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(JUnit4.class)
class StatusServiceTest {

    @Mock
    StatusRepo repo;

    @Mock
    FriendshipService friendshipService;

    @Mock
    AuthService authService;

    @Mock
    User user;

    @Mock
    NotificationService notificationService;

    @InjectMocks
    StatusService statusService;

    @Test
    void getAll() {
        // Mock data
        String username = "2";
        UUID uuid = UUID.randomUUID();
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));

        User currentUser = new User(username);
        currentUser.setId(uuid);
        currentUser.setAuthDetails(authDetails);

        User friend1 = new User(username);
        friend1.setId(uuid);
        friend1.setAuthDetails(authDetails);

        User friend2 = new User(username);
        friend2.setId(uuid);
        friend2.setAuthDetails(authDetails);


        Status status1 = new Status("testStatus", friend1);
        Interest interest = new Interest(status1, currentUser);

        List<Interest> interests = Arrays.asList(interest, interest);
        status1.setInterest(interests);

        Status status2 = new Status("testStatus", friend1);
        status2.setInterest(interests);

        List<User> friends = Arrays.asList(friend1, friend2);
        List<Status> userStatuses = Arrays.asList(status1, status2);
        List<Status> friendStatuses = Arrays.asList(status1, status2);

        Set<User> hideStatusFrom = new HashSet<>();

        User user1 = new User("1");
        hideStatusFrom.add(user1);

        // Mocking behavior
        when(authService.getAuthUser()).thenReturn(currentUser);
        when(friendshipService.getFriends()).thenReturn(friends);
        when(repo.findByOwnerAndNotExpired(any(User.class))).thenReturn(userStatuses);
        when(user.getHideStatusFrom()).thenReturn(hideStatusFrom);

        // Ensure that the friends list and their statuses are not null
        for (User friend : friends) {
            if (friend != null) {
                when(repo.findByOwnerAndNotExpired(any())).thenReturn(friendStatuses.subList(0, 1));
            }
        }

        // Execute the method
        List<StatusBrowseDTO> result = statusService.getAll();

        // Verify the result
        assertEquals(3, result.size());

        // Verify that the necessary methods were called
        verify(authService, times(4)).getAuthUser();
        verify(friendshipService, times(1)).getFriends();
        verify(repo, times(3)).findByOwnerAndNotExpired(currentUser);

        // Verify that findByOwnerAndNotExpired was called for each non-null friend
        for (User friend : friends) {
            if (friend != null) {
                verify(repo, times(3)).findByOwnerAndNotExpired(friend);
            }
        }
    }


    @Test
    public void testShowInterest_SuccessfulInterestAddition() {
        // Arrange
        Long statusId = 1L;
        boolean add = true;

        UUID uuid = UUID.randomUUID();
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));

        User currentUser = new User();
        currentUser.setId(uuid);
        currentUser.setAuthDetails(authDetails);
        currentUser.setHideStatusFrom(new HashSet<>());


        UUID uuid2 = UUID.randomUUID();
        AuthDetails authDetails2 = new AuthDetails("test@email.coms", "testPass1", new User("4"));

        User user2 = new User();
        user2.setId(uuid2);
        user2.setAuthDetails(authDetails2);
        user2.setHideStatusFrom(new HashSet<>());


        Status status = new Status();
        status.setOwner(user2);
        Interest interest = new Interest(status, user2);
        ArrayList<Interest> interests = new ArrayList<>();
        interests.add(interest);
        interests.add(interest);
        status.setInterest(interests);

        when(authService.getAuthUser()).thenReturn(currentUser);
        when(repo.findById(any())).thenReturn(Optional.of(status));
        doNothing().when(friendshipService).validateFriendship(any(User.class));
        when(repo.save(any(Status.class))).thenReturn(status);

        // Act
        boolean result = statusService.showInterest(statusId, add);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testGetById_Successful() {
        // Arrange
        Long statusId = 1L;
        Status expectedStatus = new Status();

        when(repo.findById(statusId)).thenReturn(Optional.of(expectedStatus));

        // Act
        Status result = statusService.getById(statusId);

        // Assert
        assertEquals(expectedStatus, result);
    }

    @Test()
    public void testGetById_EntityNotFound() {
        // Arrange
        Long statusId = 1L;

        when(repo.findById(statusId)).thenReturn(Optional.empty());

        // Assert (exception expected)
        assertThrows(EntityNotFound.class,
                () -> {
                    statusService.getById(statusId);
                });
    }


    @Test
    public void testCreateStatus_Successful() {
        // Arrange
        String statusText = "New status text";
        User currentUser = new User();

        when(authService.getAuthUser()).thenReturn(currentUser);

        // Act
        ResponseEntity<Boolean> response = statusService.createStatus(statusText);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(repo, times(1)).save(any(Status.class)); // Ensure that the save method is called
    }

    @Test
    public void testGetInterests_Successful() {
        // Arrange
        Long statusId = 1L;
        User currentUser = new User("TestUser");
        UUID uuid = UUID.randomUUID();
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));

        currentUser.setId(uuid);
        currentUser.setAuthDetails(authDetails);
        currentUser.setHideStatusFrom(new HashSet<>());
        Status status = new Status();
        status.setOwner(currentUser);
        Interest interest1 = new Interest(status, new User("User1"));
        Interest interest2 = new Interest(status, new User("User2"));
        List<Interest> interests = Arrays.asList(interest1, interest2);
        status.setInterest(interests);

        when(authService.getAuthUser()).thenReturn(currentUser);
        when(repo.findById(any(Long.class))).thenReturn(Optional.of(status));
        when(authService.getAuthUser()).thenReturn(currentUser);

        // Act
        List<String> result = statusService.getInterests(statusId);

        // Assert
        assertEquals(Arrays.asList("User1", "User2"), result);
    }

    @Test()
    public void testGetInterests_UnauthorizedRequest() {
        // Arrange
        Long statusId = 1L;
        User currentUser = new User("TestUser");
        UUID uuid = UUID.randomUUID();
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));

        currentUser.setId(uuid);
        currentUser.setAuthDetails(authDetails);
        currentUser.setHideStatusFrom(new HashSet<>());

        User randomUser = new User("TestUser2");
        randomUser.setId(uuid);
        randomUser.setAuthDetails(authDetails);
        randomUser.setHideStatusFrom(new HashSet<>());

        Status status = new Status();
        status.setOwner(currentUser);
        Interest interest1 = new Interest(status, new User("User1"));
        Interest interest2 = new Interest(status, new User("User2"));
        List<Interest> interests = Arrays.asList(interest1, interest2);
        status.setInterest(interests);

        when(authService.getAuthUser()).thenReturn(currentUser);
        when(repo.findById(any(Long.class))).thenReturn(Optional.of(status));
        when(authService.getAuthUser()).thenReturn(randomUser);

        // Act
        // Assert
        assertThrows(UnauthorizedRequest.class,
                () -> {
                    statusService.getInterests(statusId);
                });
    }

    @Test
    public void testValidateOwnership_Successful() {
        // Arrange
        Long statusId = 1L;
        User currentUser = new User("TestUser");
        UUID uuid = UUID.randomUUID();
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));

        currentUser.setId(uuid);
        currentUser.setAuthDetails(authDetails);
        currentUser.setHideStatusFrom(new HashSet<>());

        User randomUser = new User("TestUser2");
        randomUser.setId(uuid);
        randomUser.setAuthDetails(authDetails);
        randomUser.setHideStatusFrom(new HashSet<>());

        Status status = new Status();
        status.setOwner(currentUser);
        Interest interest1 = new Interest(status, new User("User1"));
        Interest interest2 = new Interest(status, new User("User2"));
        List<Interest> interests = Arrays.asList(interest1, interest2);
        status.setInterest(interests);

        when(authService.getAuthUser()).thenReturn(currentUser);

        // Act
        statusService.ValidateOwnership(status);

        // No exception should be thrown
    }

    @Test()
    public void testValidateOwnership_UnauthorizedRequest() {
        // Arrange
        // Arrange
        Long statusId = 1L;
        User currentUser = new User("TestUser");
        UUID uuid = UUID.randomUUID();
        AuthDetails authDetails = new AuthDetails("test@email.com", "testPass", new User("3"));

        currentUser.setId(uuid);
        currentUser.setAuthDetails(authDetails);
        currentUser.setHideStatusFrom(new HashSet<>());

        User randomUser = new User("TestUser2");
        randomUser.setId(uuid);
        randomUser.setAuthDetails(authDetails);
        randomUser.setHideStatusFrom(new HashSet<>());

        Status status = new Status();
        status.setOwner(currentUser);
        Interest interest1 = new Interest(status, new User("User1"));
        Interest interest2 = new Interest(status, new User("User2"));
        List<Interest> interests = Arrays.asList(interest1, interest2);
        status.setInterest(interests);

        when(authService.getAuthUser()).thenReturn(randomUser);

        // Act
        // Assert (exception expected)
        assertThrows(UnauthorizedRequest.class,
                () -> {
                    statusService.ValidateOwnership(status);
                });
    }
}