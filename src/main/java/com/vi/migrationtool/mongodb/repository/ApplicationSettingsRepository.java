package com.vi.migrationtool.mongodb.repository;

import com.vi.migrationtool.mongodb.model.ApplicationSettingsEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApplicationSettingsRepository
    extends MongoRepository<ApplicationSettingsEntity, String> {}
