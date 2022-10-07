package com.motive.rest.user.Friendship.Circle.DTO;
import java.util.Set;


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
public class CircleDTO {
    String name;
    String color;
    Set<String> members;
}
