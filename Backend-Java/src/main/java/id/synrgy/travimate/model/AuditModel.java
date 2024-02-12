package id.synrgy.travimate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditModel implements Serializable {

    @Column(name = "created_date", updatable = false)
    private LocalDate createdDate;

    @Column(name = "updated_date")
    private LocalDate updatedDate;

    @Getter
    @Column(name = "deleted_date")
    private LocalDate deletedDate;

    @PrePersist
    private void prePersist() {
        this.createdDate = LocalDate.now();
    }
    @PreUpdate
    private void preUpdate() {
        this.updatedDate = LocalDate.now();
    }
    @PreRemove
    public void beforeAnyUpdate() {
        this.deletedDate = LocalDate.now();
    }
}
