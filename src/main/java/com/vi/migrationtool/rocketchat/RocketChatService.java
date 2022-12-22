package com.vi.migrationtool.rocketchat;

import com.vi.migrationtool.rocketchat.login.LdapLoginDTO;
import com.vi.migrationtool.rocketchat.login.LoginResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class RocketChatService {

  private final RocketChatConfig rocketChatConfig;

  public void executeRocketChatRequest(String rocketChatMethod, String rocketChatRequest) {
    LoginResponseDTO loginResponse = loginRocketChatAdmin();
    var headers = new HttpHeaders();
    headers.set("X-User-Id", loginResponse.getData().getUserId());
    headers.set("X-Auth-Token", loginResponse.getData().getAuthToken());
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(rocketChatRequest, headers);
    var url = rocketChatConfig.getServerUrl() + "/v1/method.call/" + rocketChatMethod;
    new RestTemplate().postForEntity(url, request, Void.class);
  }

  private LoginResponseDTO loginRocketChatAdmin() {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    var ldapLoginDTO = new LdapLoginDTO();
    ldapLoginDTO.setLdap(true);
    ldapLoginDTO.setUsername(rocketChatConfig.getAdminUsername());
    ldapLoginDTO.setLdapPass(rocketChatConfig.getAdminPassword());
    HttpEntity<LdapLoginDTO> request = new HttpEntity<>(ldapLoginDTO, headers);
    var url = rocketChatConfig.getServerUrl() + "/v1/login";
    ResponseEntity<LoginResponseDTO> loginResponseDTOResponseEntity =
        new RestTemplate().postForEntity(url, request, LoginResponseDTO.class);
    return loginResponseDTOResponseEntity.getBody();
  }
}
