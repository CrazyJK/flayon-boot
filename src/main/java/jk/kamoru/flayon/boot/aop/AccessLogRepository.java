package jk.kamoru.flayon.boot.aop;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccessLogRepository extends MongoRepository<AccessLog, String> {

}
