package com.vi.migrationtool.keycloak;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class UsersWithRole {

  @NonNull String role;

  @NonNull Collection<String> userIds;
}
