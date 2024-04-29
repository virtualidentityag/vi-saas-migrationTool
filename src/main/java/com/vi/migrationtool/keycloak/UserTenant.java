package com.vi.migrationtool.keycloak;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserTenant {

  @NonNull String userId;
  @NonNull Long tenantId;
}
