package com.vi.migrationtool.userservice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MariaDbUser {

  String userId;
  String rocketChatId;
  String email;
}
