package com.vi.migrationtool.weblate.config;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class ProjectDTO {

  @NonNull String name;

  @NonNull String web;

  @NonNull String slug;
}
