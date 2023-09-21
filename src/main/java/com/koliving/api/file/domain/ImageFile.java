package com.koliving.api.file.domain;

import com.koliving.api.base.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@ToString
@Getter
@Entity(name = "TB_IMAGE_FILE")
@SQLDelete(sql = "UPDATE TB_IMAGE_FILE SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = PROTECTED)
public class ImageFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String path;

    private Long size;


    private ImageFile(String path, Long size) {
        this.path = Objects.requireNonNull(path);
        this.size = Objects.requireNonNull(size);
    }

    public static ImageFile valueOf(String path, Long size) {
        return new ImageFile(path, size);
    }
}
