package com.vi.migrationtool.rocketchat;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vi.migrationtool.userservice.MariaDbUser;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RocketChatMongoDbService implements InitializingBean {

  @Value("${spring.data.mongodb.rocketchat.uri}")
  String mongoUrl;

  @Value("${spring.data.mongodb.rocketchat.database}")
  String databaseName;

  @NonNull MongoClient mongoClient;

  public List<MariaDbUser> findInconsistentUsers(List<MariaDbUser> users) {
    MongoDatabase database = mongoClient.getDatabase(databaseName);
    MongoCollection<Document> collection = database.getCollection("users");
    return users.stream()
        .filter(user -> cannotFindUserInRocketChat(user, collection))
        .collect(Collectors.toList());
  }

  private boolean cannotFindUserInRocketChat(
      MariaDbUser mariaDbUser, MongoCollection<Document> collection) {
    Document query = new Document("_id", mariaDbUser.getRocketChatId());
    Document user = collection.find(query).first();
    return user == null;
  }

  public Map<MariaDbUser, Optional<String>> retrieveValidRocketchatIdByUserEmail(
      List<MariaDbUser> inconsistentUsers) {
    MongoDatabase database = mongoClient.getDatabase(databaseName);
    MongoCollection<Document> collection = database.getCollection("users");
    return inconsistentUsers.stream()
        .collect(
            Collectors.toMap(user -> user, user -> retrieveValidRocketChatId(user, collection)));
  }

  private Optional<String> retrieveValidRocketChatId(
      MariaDbUser user, MongoCollection<Document> collection) {
    return Optional.ofNullable(
            collection.find(new Document("emails.0.address", user.getEmail())).first())
        .map(document -> document.get("_id").toString());
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    initializeMongoClient();
  }

  private void initializeMongoClient() {
    if (mongoClient == null) {
      mongoClient = MongoClients.create(mongoUrl);
    }
  }
}
