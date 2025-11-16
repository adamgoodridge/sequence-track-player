# Plantuml Rendar User Story

## User Story
**As a developer**  
I want to have a Plantuml render capability
**So that** I can view UML diagrams directly in the documentation on GitLab

## Acceptance Criteria

- **Given:** User is viewing content without an existing Plantuml render
- **When:** The page is loaded
- **Then:** Plantuml diagram is generated and displayed
## Monitoring
Uptime Kuma Monitoring is configured to monitor the Plantuml server container to ensure it is operational and responsive.
every 1 minute, a HTTP request is sent to the Plantuml. If the server does not respond with a 200 OK status, it retries every 20 seconds and an alert is triggered to notify the development team of potential issues with the Plantuml rendering service.
## Inscope
 - Deploy Plantuml server container
 - Configure GitLab to use Plantuml server for rendering diagrams
 - Ensure plantuml server is up-to-date
 - Configure uptime kuma monitoring for plantuml server
## Future Enhancements
 - Implement SSL 
## Firewall Rules
 - Allow inbound traffic on port 8880 TCP on the podman host
## Container Infrastructure Summary

| Service Name | Host IP | Port Mapping | Image | User | Engine |
|--------------|---------|--------------|-------|------|--------|
| **GitLab** | 192.168.181.33 | 443:443 | gitlab/gitlab-ce | root | Docker |
| **PlantUML Server** | 192.168.181.24 | 8888:8080 | plantuml/plantuml-server | adam | Podman |


## C4 System Architecture Diagram

```plantuml
@startuml plantumlSystem
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Context.puml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Component.puml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Dynamic.puml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Deployment.puml
Person(developer, "Developer", "Creates and maintains documentation with UML diagrams")
System_Boundary(gitlabSystem, "Docker Server") {
    Container(gitlab, "GitLab", "Documentation Platform", "Hosts project documentation and integrates with PlantUML server for rendering diagrams.")
}
System_Boundary(dockerSystem, "Podman Host") {
    Container(plantumlServer, "PlantUML Server", "UML Rendering Service", "Renders UML diagrams from PlantUML syntax.")
}
developer --> gitlab : Views and documentation
gitlab --> plantumlServer : Sends PlantUML syntax for rendering by 192.168.181.45:8880
plantumlServer --> gitlab : Returns rendered UML diagrams
developer --> plantumlServer : Access PlantUML server directly for local rendering on laptop via vscode plugin