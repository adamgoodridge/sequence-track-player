
## Container Infrastructure Summary

| Service Name | Container Name | Host IP | Port Mapping | Engine/Image | User | Network |
|--------------|----------------|---------|--------------|--------------|------|---------|
| **HAProxy** | haproxy | 192.168.181.24 | 80:80, 443:443, 27017:27017, 6080:6080 | ghcr.io/tomdess/docker-haproxy-certbot:master | root | backend (192.168.183.2) |
| **Homepage** | homepage | 192.168.181.24 | Internal | ghcr.io/gethomepage/homepage:latest | root | backend (192.168.183.3) |
| **Keycloak SSO** | keycloak | 192.168.181.24 | 889:8443 | keycloak/keycloak:latest | root | backend (192.168.183.44) |
| **Audio File Server** | audioFileWebServer2 | 192.168.181.24 | 89:80 | nginx | root | backend (192.168.183.5) |
| **Uptime Kuma** | uptime-kuma | 192.168.181.24 | 3001:8085 | louislam/uptime-kuma:1 | root | backend |
| **Track Player (Prod)** | sequence-track-player-api-prod | 192.168.181.24 | 84:8443 | sequence-track-player-api-prod:v10 | root | default |
| **Track Player (NonProd)** | sequence-track-player-api-nonprod | 192.168.181.24 | 85:8443 | sequence-track-player-api-nonprod:v10 | root | default |
| **AI Web UI** | aiWebUI | 192.168.181.24 | 8068:8080 | ghcr.io/open-webui/open-webui:main | root | default |
| **MariaDB** | mariadb | localhost | Internal | mariadb:10.11 | root | default |
| **phpMyAdmin** | phpmyadmin | localhost | 8080:80 | phpmyadmin | root | default |

### Network Architecture
- **Host Server**: `192.168.181.24` (Main Docker host)
- **Backend Network**: `192.168.183.0/24` (Internal container network)
- **External Access**: HAProxy serves as reverse proxy for SSL termination
- **Storage**: NFS mounts from `192.168.181.14` for media files
- **SSL Certificates**: Let's Encrypt via HAProxy certbot integration

### Key Services Access
- **Production Radio**: `https://radio.adsmac.net` → `84:8443`
- **Non-Prod Radio**: `https://radio.nonprod.adsmac.net` → `85:8443`  
- **Homepage Dashboard**: `https://home.adsmac.net` → Internal
- **AI Interface**: `https://ai.adsmac.net` → `8068:8080`
- **SSO Authentication**: `https://sso.adsmac.net` → `889:8443`