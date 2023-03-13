package com.vi.migrationtool.mongodb.model;

import com.vi.migrationtool.schemas.model.ConsultingType;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "consulting_types")
public class ConsultingTypeEntity extends ConsultingType {}
