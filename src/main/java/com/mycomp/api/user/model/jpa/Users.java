package com.mycomp.api.user.model.jpa;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author Daniel
 */
@Data
@Entity
@Table(name = "users")
public class Users implements Serializable {

    @Id
    private String username;
    @NotBlank
    private String password;
    @Size(min = 1)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "username"))
    private Set<String> roles;

    public List<GrantedAuthority> getAuthorities() {
        return roles.stream().map(m -> new SimpleGrantedAuthority(m)).collect(Collectors.toList());
    }

    public void addRole(String role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

}
