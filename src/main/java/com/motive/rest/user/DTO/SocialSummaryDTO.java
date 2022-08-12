package com.motive.rest.user.DTO;

import java.util.List;

import org.springframework.data.util.Pair;

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
    List<Pair<String, Long>> requestsForUser;
    List<Pair<String, Long>> requestsByUser;
}
