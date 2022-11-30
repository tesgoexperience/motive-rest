package com.motive.rest.motive.attendance;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.motive.rest.motive.Motive;
import com.motive.rest.motive.attendance.Attendance.ATTENDANCE_STATUS;
import com.motive.rest.user.User;

public interface AttendanceRepo extends CrudRepository<Attendance,Long>{
    Optional<Attendance> findByMotiveAndUser(Motive motive, User user);
    List<Attendance> findByMotiveAndStatus(Motive motive, ATTENDANCE_STATUS status);
}
