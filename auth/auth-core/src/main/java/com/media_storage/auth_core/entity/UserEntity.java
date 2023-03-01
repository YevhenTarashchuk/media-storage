package com.media_storage.auth_core.entity;

import com.media_storage.auth_data.enumeration.Role;
import com.media_storage.auth_data.enumeration.UserStatus;
import com.media_storage.shared_data.model.BaseEntityAudit;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity
@Table(name = "users")
@Getter
@Setter
@Where(clause = "is_deleted=false")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id=?")
public class UserEntity extends BaseEntityAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;
}
