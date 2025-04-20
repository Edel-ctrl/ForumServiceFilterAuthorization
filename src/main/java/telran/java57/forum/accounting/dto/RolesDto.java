package telran.java57.forum.accounting.dto;

import lombok.*;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolesDto {
    String login;
    @Singular // to allow adding roles one by one
    Set<String> roles;
}
