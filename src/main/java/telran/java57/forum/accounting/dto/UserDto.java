package telran.java57.forum.accounting.dto;

import lombok.*;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    String login;
    String firstName;
    String lastName;
    @Singular // to create a Set<String> roles one by one.
    Set<String> roles;
}
