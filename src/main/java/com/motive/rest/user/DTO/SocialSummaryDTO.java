package com.motive.rest.user.DTO;

import java.util.List;

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
public class SocialSummaryDTO {
    List<String> friends;
    List<String> requestsForUser;
    List<String> requestsByUser;
}
