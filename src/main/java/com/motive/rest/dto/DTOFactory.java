package com.motive.rest.dto;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.motive.rest.motive.attendance.dto.AttendanceDTO;
import com.motive.rest.motive.dto.MotiveBrowseDTO;
import com.motive.rest.motive.dto.MotiveManageDTO;
import com.motive.rest.user.dto.UserDto;

// Takes in an object or List and returns the DTO requested if it is available
@Component
public class DTOFactory {
    public enum DTO_TYPE {
        MOTIVE_BROWSE,
        MOTIVE_MANAGE,
        ATTENDANCE,
        USER
    }

    @Autowired
    private ModelMapper modelMapper;

    public DTO getDto(Object entity, DTO_TYPE type) {
        switch (type) {
            case MOTIVE_BROWSE:
                return modelMapper.map(entity, MotiveBrowseDTO.class);
            case MOTIVE_MANAGE:
                return modelMapper.map(entity, MotiveManageDTO.class);
            case ATTENDANCE:
                return modelMapper.map(entity, AttendanceDTO.class);
            case USER:
                return modelMapper.map(entity, UserDto.class);
        }
        return null;
    }

    public List<? extends DTO> getDto(List<?> entities, DTO_TYPE type) {

        List<DTO> dtoList = new ArrayList<>();

        for (Object entity : entities) {
            DTO dto = getDto(entity, type);
            dtoList.add(dto);
        }

        return dtoList;
    }
}
