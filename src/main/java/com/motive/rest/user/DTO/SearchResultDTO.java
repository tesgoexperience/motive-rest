package com.motive.rest.user.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SearchResultDTO {
    public enum USER_RELATIONSHIP {

        NO_RELATION {
            public String getMessage() {
                return "You are not friends nor do you have any pending requests.";
            }
        },
        FRIEND {
            public String getMessage() {
                return "You are friends.";
            }
        },
        REQUESTED_BY_YOU {
            public String getMessage() {
                return "You have requested this user and they are yet to respond.";
            }
        },
        REQUESTED_BY_THEM {
            public String getMessage() {
                return "They have requested you and you are yet to respond.";
            }
        };

        public abstract String getMessage();

    }

    String username;
    USER_RELATIONSHIP relation;
}
