package com.motive.rest.user.dto;

import java.util.List;

import com.motive.rest.dto.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SocialSummaryDTO implements DTO{
    List<String> friends;
    List<String> requestsForUser;
    List<String> requestsByUser;
}
