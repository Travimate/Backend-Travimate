package id.synrgy.travimate.repository;

import id.synrgy.travimate.model.Orders;
import id.synrgy.travimate.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Orders, UUID> {
    List<Orders> findByUsers(Users users);
}