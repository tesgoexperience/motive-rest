package com.motive.rest.user.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@NoArgsConstructor @Getter @Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SearchResultDTO {
    enum RELATIONSHIP {
        NOT_REQUEST, FRIENDS, REQUEST_BY_YOU, REQUEST_BY_THEM
    }
    String userName;
    RELATIONSHIP relation;
}
