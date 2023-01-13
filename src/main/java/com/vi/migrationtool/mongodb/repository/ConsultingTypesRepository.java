package com.vi.migrationtool.mongodb.repository;

import com.vi.migrationtool.mongodb.model.ConsultingTypeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConsultingTypesRepository extends MongoRepository<ConsultingTypeEntity, String> {}
