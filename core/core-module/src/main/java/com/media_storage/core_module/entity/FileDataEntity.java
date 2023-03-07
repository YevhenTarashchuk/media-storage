package com.media_storage.core_module.entity;

import com.media_storage.core_data.enumeration.FileType;
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
@Table(name = "file_data")
@Getter
@Setter
@Where(clause = "is_deleted=false")
@SQLDelete(sql = "UPDATE file_data SET is_deleted = true WHERE id=?")
public class FileDataEntity extends BaseEntityAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private FileType fileType;
}
