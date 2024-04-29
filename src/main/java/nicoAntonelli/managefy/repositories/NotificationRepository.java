package nicoAntonelli.managefy.repositories;

import nicoAntonelli.managefy.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification WHERE n.user.id = ?1")
    List<Notification> findByUser(Long userID);
}