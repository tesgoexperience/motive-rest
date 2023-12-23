package com.motive.rest.motive.attendance;

import com.motive.rest.Auth.AuthService;
import com.motive.rest.dto.DTOFactory;
import com.motive.rest.motive.Motive;
import com.motive.rest.motive.MotiveService;
import com.motive.rest.motive.attendance.dto.AttendanceDTO;
import com.motive.rest.user.User;
import com.motive.rest.user.UserService;
import com.motive.rest.user.friendship.FriendshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AttendanceServiceTest {


    @Mock
    DTOFactory dtoFactory;
    @Mock
    AttendanceRepo repo;
    @Mock
    UserService userService;
    @Mock
    AuthService authService;
    @Mock
    FriendshipService friendshipService;
    @Mock
    MotiveService motiveService;
    @InjectMocks
    AttendanceService attendanceService;

    @Test
    void getPendingAttendance() {
        UUID motiveId = UUID.randomUUID();
        Motive motive = new Motive(); // Create a Motive object or mock it as needed

        // Stubbing the MotiveService methods
        when(motiveService.getMotive(any(UUID.class))).thenReturn(motive); // Use any(UUID.class) to match the UUID argument
        doNothing().when(motiveService).validateOwner(motive);

        // Define a list of expected AttendanceDTO objects
        List<AttendanceDTO> expectedAttendanceList = new ArrayList<>(); // Fill it with expected data

        // Call the method to test
        List<AttendanceDTO> result = attendanceService.getPendingAttendance(motiveId);

        // Verify that the MotiveService methods were called with the expected arguments
        verify(motiveService).getMotive(motiveId);
        verify(motiveService).validateOwner(motive);

        // Verify the result
        assertEquals(expectedAttendanceList, result);
    }

//    @Test
//    @Ignore
//    //TODO: https://stackoverflow.com/questions/20543966/incompatible-types-and-fresh-type-variable
//    void testGetPendingAttendance() {
//        Motive motive = new Motive(); // Create a Motive object or mock it as needed
//
//        // Create a list of expected AttendanceDTO objects
//        List<Attendance> expectedAttendanceList = new ArrayList<>(); // Fill it with expected data
//        Attendance testAttendance = new Attendance();
//        expectedAttendanceList.add(testAttendance);
//
//        // Stub the repo to return a list of attendance records with status ATTENDANCE_STATUS.REQUESTED
//        when(repo.findByMotiveAndStatus(motive, Attendance.ATTENDANCE_STATUS.REQUESTED)).thenReturn(expectedAttendanceList);
//
//        List<AttendanceDTO> expectedAttendanceDTOList = new ArrayList<>(); // Fill it with expected data
//        AttendanceDTO attendanceDTO = new AttendanceDTO();
//        expectedAttendanceDTOList.add(attendanceDTO);
//
//        // Stub the dtoFactory to return the same list
////        when(dtoFactory.getDto(expectedAttendanceList, DTOFactory.DTO_TYPE.ATTENDANCE)).thenReturn(expectedAttendanceDTOList);
//
//        // Call the method to test
//        List<AttendanceDTO> result = attendanceService.getPendingAttendance(motive);
//
//        // Verify that repo.findByMotiveAndStatus is called with the correct arguments
//        verify(repo).findByMotiveAndStatus(motive, Attendance.ATTENDANCE_STATUS.REQUESTED);
//
//        // Verify that dtoFactory.getDto is called with the expected arguments
////        verify(dtoFactory).getDto(eq(expectedAttendanceDTOList), eq(DTOFactory.DTO_TYPE.ATTENDANCE));
//
//        // Verify that the result matches the expectedAttendanceListSTOs
//        assertEquals(expectedAttendanceDTOList, result);
//    }


    @Test
    void testFindByMotiveAndUser_WhenAttendanceDoesNotExist() {
        // Arrange
        UUID motiveId = UUID.randomUUID();
        User authUser = new User(); // Create an authUser object or mock it as needed
        Attendance attendance = new Attendance(); // Create an Attendance object or mock it as needed
        Motive motive = new Motive();
        motive.setId(motiveId);

        // Stub the authService to return the authUser
        when(authService.getAuthUser()).thenReturn(authUser);
        // Stub the repo to return an empty Optional, indicating that attendance does not exist
        when(repo.findByMotiveAndUser(motive, authUser)).thenReturn(Optional.empty());
        when(motiveService.getMotive(motiveId)).thenReturn(motive);

        // Act
        Optional<Attendance> result = attendanceService.findByMotiveAndUser(motiveId);

        //Assert
        verify(authService).getAuthUser();
        verify(repo).findByMotiveAndUser(motive, authUser);
        assertFalse(result.isPresent()); // Verify that the result is an empty Optional
    }


    @Test
    void testFindByMotiveAndUser_WhenAttendanceExists() {

        // Arrange
        UUID motiveId = UUID.randomUUID();
        User authUser = new User(); // Create an authUser object or mock it as needed
        Attendance attendance = new Attendance(); // Create an Attendance object or mock it as needed
        Motive motive = new Motive();
        motive.setId(motiveId);

        when(authService.getAuthUser()).thenReturn(authUser);
        when(repo.findByMotiveAndUser(motive, authUser)).thenReturn(Optional.of(attendance));
        when(motiveService.getMotive(motiveId)).thenReturn(motive);

        // Act
        Optional<Attendance> result = attendanceService.findByMotiveAndUser(motiveId); // Invoke the service method

        //Assert
        verify(authService).getAuthUser(); // Verify that authService.getAuthUser is called.
        verify(repo).findByMotiveAndUser(motive, authUser); // Verify that repo.findByMotiveAndUser is called with the correct arguments.
        assertTrue(result.isPresent()); // Verify that the result is an Optional containing the attendance.
        assertEquals(attendance, result.get());
    }

    @Test
    public void testFindByMotiveAndUser_Success() {
        // Arrange
        UUID motiveId = UUID.randomUUID();
        User user = new User();
        Motive motive = new Motive();
        Attendance expectedAttendance = new Attendance();
        when(motiveService.getMotive(motiveId)).thenReturn(motive);
        when(repo.findByMotiveAndUser(motive, user)).thenReturn(Optional.of(expectedAttendance));

        // Act
        Optional<Attendance> result = attendanceService.findByMotiveAndUser(motiveId, user);

        // Assert
        verify(motiveService).getMotive(motiveId);
        verify(repo).findByMotiveAndUser(motive, user);
        assertEquals(Optional.of(expectedAttendance), result);
    }

    @Test
    public void testFindByMotiveAndUser_MissingMotive() {
        // Arrange
        UUID motiveId = UUID.randomUUID();
        User user = new User();
        when(motiveService.getMotive(motiveId)).thenReturn(null);

        // Act
        Optional<Attendance> result = attendanceService.findByMotiveAndUser(motiveId, user);

        // Assert
        verify(motiveService).getMotive(motiveId);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void testFindByMotiveAndUser_NotFound() {
        // Arrange
        UUID motiveId = UUID.randomUUID();
        User user = new User();
        Motive motive = new Motive();
        when(motiveService.getMotive(motiveId)).thenReturn(motive);
        when(repo.findByMotiveAndUser(motive, user)).thenReturn(Optional.empty());

        // Act
        Optional<Attendance> result = attendanceService.findByMotiveAndUser(motiveId, user);

        // Assert
        verify(motiveService).getMotive(motiveId);
        verify(repo).findByMotiveAndUser(motive, user);
        assertEquals(Optional.empty(), result);
    }


    @Test
    public void testMotiveAttendance_Success() {
        // Arrange
        UUID motiveId = UUID.randomUUID();
        Attendance attendance = new Attendance();
        AttendanceDTO expectedDto = new AttendanceDTO();
        when(attendanceService.findByMotiveAndUser(motiveId)).thenReturn(Optional.of(attendance));
        when(dtoFactory.getDto(attendance, DTOFactory.DTO_TYPE.ATTENDANCE)).thenReturn(expectedDto);

        // Act
        AttendanceDTO result = attendanceService.motiveAttendance(motiveId);

        // Assert
        verify(dtoFactory).getDto(attendance, DTOFactory.DTO_TYPE.ATTENDANCE);
        assertEquals(expectedDto, result);
    }

    @Test
    public void testMotiveAttendance_AttendanceNotFound() {
        // Arrange
        UUID motiveId = UUID.randomUUID();
        when(attendanceService.findByMotiveAndUser(motiveId)).thenReturn(Optional.empty());

        // Act
        AttendanceDTO result = attendanceService.motiveAttendance(motiveId);

        // Assert
        assertEquals(null, result);
    }

    @Test
    void cancelMyAttendance() {
        repo.delete(any());
        verify(repo, times(1)).delete(any());
    }

    @Test
    void removeAttendee() {
        repo.delete(any());
        verify(repo, times(1)).delete(any());
    }

    @Test
    void requestAttendance() {
        repo.save(any());
        verify(repo, times(1)).save(any());
    }

    @Test
    void respondToAttendanceRequest() {
        repo.save(any());
        verify(repo, times(1)).save(any());
    }

//    @Test
//    public void testRespondToAttendanceRequest_Accept() {
//        UUID motiveId = UUID.randomUUID();
//        AttendanceResponseDto responseDTO = new AttendanceResponseDto();
//        responseDTO.setMotiveId(motiveId);
//
//        User friend = new User();
//
//        Motive motive = new Motive();
//
//        User owner = new User("123");
//
//        Attendance attendance = new Attendance();
//        attendance.setMotive(motive);
//        motive.setOwner(owner);
//
////        attendance.(owner);
//
//        when(userService.findByUsername(any())).thenReturn(friend);
//
//        friendshipService.validateFriendship(friend);
//
//        when(motiveService.getMotive(motiveId)).thenReturn(motive);
//
//        when(repo.findByMotiveAndUser(motive,friend)).thenReturn(Optional.of(attendance));
//
//        when(authService.getAuthUser()).thenReturn(owner);
//
//        attendanceService.respondToAttendanceRequest(responseDTO, true);
//
//        verify(userService).findByUsername("attendeeUsername");
//        verify(friendshipService).validateFriendship(friend);
//        verify(motiveService).getMotive(motiveId);
//        verify(repo).findByMotiveAndUser(motive, friend);
//        verify(authService).getAuthUser();
//        assertEquals(Attendance.ATTENDANCE_STATUS.CONFIRMED, attendance.getStatus());
//        verify(repo).save(attendance);
//    }

//    @Test
//    public void testRespondToAttendanceRequest_Reject() {
//        AttendanceResponseDto response = new AttendanceResponseDto("attendeeUsername", "motiveId");
//        User friend = new User("attendeeUsername");
//        Motive motive = new Motive("Test Motive");
//        User owner = new User("ownerUsername");
//        Attendance attendance = new Attendance(motive, friend);
//        attendance.setMotiveOwner(owner);
//
//        when(userService.findByUsername("attendeeUsername")).thenReturn(friend);
//        when(friendshipService.validateFriendship(friend)).thenReturn(true);
//        when(motiveService.getMotive("motiveId")).thenReturn(motive);
//        when(repo.findByMotiveAndUser(motive, friend)).thenReturn(Optional.of(attendance));
//        when(authService.getAuthUser()).thenReturn(owner);
//
//        attendanceService.respondToAttendanceRequest(response, false);
//
//        verify(userService).findByUsername("attendeeUsername");
//        verify(friendshipService).validateFriendship(friend);
//        verify(motiveService).getMotive("motiveId");
//        verify(repo).findByMotiveAndUser(motive, friend);
//        verify(authService).getAuthUser();
//        assertEquals(ATTENDANCE_STATUS.CANCELED, attendance.getStatus());
//        verify(repo).delete(attendance);
//    }


}