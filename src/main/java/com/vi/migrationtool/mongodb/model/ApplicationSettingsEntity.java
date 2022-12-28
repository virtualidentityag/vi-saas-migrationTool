package com.vi.migrationtool.mongodb.model;

import com.vi.migrationtool.schemas.model.ApplicationSettings;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "application_settings")
public class ApplicationSettingsEntity extends ApplicationSettings {}
