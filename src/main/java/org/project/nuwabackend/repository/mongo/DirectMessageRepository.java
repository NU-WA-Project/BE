package org.project.nuwabackend.repository.mongo;

import org.project.nuwabackend.domain.mongo.DirectMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DirectMessageRepository extends MongoRepository<DirectMessage, String> {

    Slice<DirectMessage> findDirectMessagesByRoomId(String roomId, Pageable pageable);

    Slice<DirectMessage> findDirectMessageByRoomIdOrderByCreatedAtDesc(String roomId, Pageable pageable);

    Optional<DirectMessage> findFirstByRoomIdOrderByCreatedAtDesc(String roomId);

}