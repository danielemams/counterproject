# Counter Project

Sistema di tracciamento spese con backend REST (Jakarta EE) e frontend HTML/JS.

## Architettura

- **Backend**: Java 21, Maven, JPA, JAX-RS (RESTEasy), Jakarta EE 10, WildFly
- **Frontend**: HTML/CSS/JavaScript vanilla, Nginx
- **Database**: PostgreSQL 16

## Struttura Progetto

```
counterproject/
├── backend/                 # Backend Jakarta EE
│   ├── src/main/java/com/counterproject/
│   │   ├── entity/         # JPA entities
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── service/        # Business logic
│   │   ├── rest/           # JAX-RS REST endpoints
│   │   └── util/           # Utilities
│   ├── pom.xml
│   └── Containerfile
├── frontend/                # Frontend statico
│   ├── index.html          # Lista code
│   ├── queue-detail.html   # Dettaglio coda + metriche
│   ├── templates.html      # Gestione templates
│   ├── js/                 # JavaScript modules
│   ├── css/                # Stili
│   ├── nginx.conf
│   └── Containerfile
├── podman-compose.yml       # Orchestrazione container
└── deploy.sh                # Script di deploy automatico
```

## Deployment

### Prerequisiti
- Podman
- podman-compose

### Deploy Completo (con rebuild senza cache)

```bash
./deploy.sh
```

Lo script:
1. Ferma e rimuove tutti i container esistenti
2. Rimuove i volumi
3. Rimuove le immagini vecchie
4. Builda backend e frontend senza cache
5. Avvia tutti i servizi

### Accesso

- **Frontend**: http://localhost:8000
- **Backend API**: http://localhost:8080/counterproject/api
- **Database**: localhost:5432

### Comandi Utili

```bash
# Vedere i logs
podman-compose logs -f

# Vedere solo backend logs
podman-compose logs -f backend

# Fermare i servizi
podman-compose down

# Fermare e rimuovere volumi (reset completo database)
podman-compose down -v

# Rebuild manuale senza cache
podman build --no-cache -t counterproject-backend -f backend/Containerfile backend/
podman build --no-cache -t counterproject-frontend -f frontend/Containerfile frontend/
```

## API Endpoints

### Queues
- `GET /api/queues` - Lista tutte le code
- `GET /api/queues/{id}` - Dettaglio coda
- `POST /api/queues` - Crea coda
- `PUT /api/queues/{id}` - Aggiorna coda
- `DELETE /api/queues/{id}` - Elimina coda
- `POST /api/queues/{id}/clear-diffs` - Pulisce tutte le diff
- `POST /api/queues/{id}/link-template` - Linka template alla coda
- `GET /api/queues/{id}/metrics?dtRif=YYYY-MM-DD` - Calcola metriche

### Diffs
- `GET /api/diffs?queueId={id}` - Lista diff di una coda
- `GET /api/diffs/{id}` - Dettaglio diff
- `POST /api/diffs` - Crea diff
- `PUT /api/diffs/{id}` - Aggiorna diff
- `DELETE /api/diffs/{id}` - Elimina diff

### Templates
- `GET /api/templates` - Lista tutti i template
- `GET /api/templates/{id}` - Dettaglio template
- `POST /api/templates` - Crea template
- `PUT /api/templates/{id}` - Aggiorna template
- `DELETE /api/templates/{id}` - Elimina template

## Logica Business

### Metriche Calcolate

Per ogni coda, data una `dtRif` (data di riferimento):

1. **currentValue**: Somma di initValue + tutte le diff fino a dtRif
2. **expectedValue**: currentValue - spese manuali, considerando linear consumption e bonus
3. **budgetCurrent**: Budget giornaliero disponibile fino alla max expiry date
4. **speseAllaDtRifEOD**: Spese totali nel giorno dtRif
5. **spesaExpectedAllaDtRifEOD**: Spese previste (non manuali) nel giorno dtRif

### Diff Rules

- **Diff < 0** (spese): `isManual` obbligatorio, `dtExpiry` sempre null
- **Diff > 0** (ricariche): `isManual` sempre null, `dtExpiry` opzionale
- **Linear Consumption**: Se attivo sulla coda, le ricariche con dtExpiry vengono consumate linearmente

### Templates

I template generano diff automaticamente in base a:
- `value`: Valore della diff
- `dayOfPeriod`: Giorno del periodo (es: giorno 1 del mese)
- `frequencyNum` + `frequencyUnit`: Ogni N giorni/settimane/mesi/anni

Quando si modifica un template, le diff future non modificate manualmente vengono rigenerate.

## Note

- Le immagini container sono ottimizzate per dimensioni ridotte
- Il database PostgreSQL persiste in un volume Docker
- Il frontend comunica con il backend via REST API
- Timezone: LocalDateTime senza timezone (server timezone)
