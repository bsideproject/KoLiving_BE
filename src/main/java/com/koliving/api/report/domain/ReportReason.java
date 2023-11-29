package com.koliving.api.report.domain;

import static lombok.AccessLevel.PROTECTED;

import com.koliving.api.base.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * author : haedoang date : 2023/11/29 description :
 */
@Getter
@Entity(name = "TB_REPORT_REASON")
@SQLDelete(sql = "UPDATE TB_REPORT_REASON SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = PROTECTED)
public class ReportReason extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private ReportReason(String name) {
        this.name = name;
    }

    public static ReportReason of(String name) {
        return new ReportReason(name);
    }
}
