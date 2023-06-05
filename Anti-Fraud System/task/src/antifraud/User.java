package antifraud;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name = "user_id")
    private long id;

    @NotNull
    @Column(name = "user_name")
    private String name;

    @NotNull
    @Column(name = "user_username", unique = true)
    private String username;

    @NotNull
    @Column(name = "user_password")
    private String password;

    @NotNull
    @Column(name = "user_role")
    private String role = "ROLE_MERCHANT";

    @NotNull
    @Column(name = "user_status")
    private String status = "LOCK";

    @org.springframework.data.annotation.Transient
    private boolean roleIsAssigned = false;

    public User() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRoleIsAssigned() {
        return roleIsAssigned;
    }

    public void setRoleIsAssigned(boolean roleIsAssigned) {
        this.roleIsAssigned = roleIsAssigned;
    }
}
