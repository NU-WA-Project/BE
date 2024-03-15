package org.project.nuwabackend.repository.mongo;

import org.project.nuwabackend.domain.mongo.Canvas;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CanvasRepository extends MongoRepository<Canvas, String> {


    Slice<Canvas> findCanvasListByWorkSpaceId(Long workSpaceId, Pageable pageable);
}