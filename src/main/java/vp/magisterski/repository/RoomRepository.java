package vp.magisterski.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vp.magisterski.model.shared.Room;

public interface RoomRepository extends JpaRepository<Room, String> {
}
