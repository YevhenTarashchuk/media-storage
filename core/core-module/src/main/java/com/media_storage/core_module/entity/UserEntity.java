package com.media_storage.core_module.entity;

import com.media_storage.shared_data.model.BaseEntityAudit;
import jakarta.persistence.Entity;
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
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String photoUrl;
}
