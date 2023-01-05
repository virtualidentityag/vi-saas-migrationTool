#Migration tool 

New features in Onlineberatung  or updates of third party tools, that the system relies on, sometimes need manual interventions in the configurations of those tools, and sometimes the configuration update can be automated. 

Usually configuration updates on tools like KeyCloak and RocketChat, where the config is stored in a database can be automated by calling REST endpoints of the tools with some admin user. 

Most of the configuration are handled with helm releases, but there are some cases, usually theming and customization of Keycloak and Jitsi, where the configuration updates are executed via some scripts. The migration tool should inform us that there is a need for an update and point to new resources.

Since the tool is based in Liquibase in case a manual intervention is needed, we would need to update the step in database as completed when the manual step is executed. 

#### Setup migration schema
```SQL
 CREATE DATABASE IF NOT EXISTS migrationservice CHARACTER SET utf8 COLLATE utf8_unicode_ci;
 GRANT ALTER, CREATE, CREATE VIEW, DELETE, DROP, INDEX, INSERT, REFERENCES, SELECT, SHOW VIEW, TRIGGER, UPDATE, ALTER ROUTINE, EXECUTE ON migrationservice.* TO 'liquibase'@'%'; 
```