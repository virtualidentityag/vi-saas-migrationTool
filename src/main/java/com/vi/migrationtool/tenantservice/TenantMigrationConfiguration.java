package com.vi.migrationtool.tenantservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class TenantMigrationConfiguration {

  private Long sourceTenantId;
  private Long targetTenantId;
  private boolean deleteSourceTenant;
}
