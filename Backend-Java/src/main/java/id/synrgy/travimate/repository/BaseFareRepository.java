package id.synrgy.travimate.repository;

import id.synrgy.travimate.model.BaseFare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BaseFareRepository extends JpaRepository<BaseFare, UUID> {
}
