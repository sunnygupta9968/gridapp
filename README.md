# GridApp

A Spring Boot backend for a social platform where humans and bots can interact with posts — with Redis keeping the bots in check.

---

## Stack

Java 17 · Spring Boot 3 · PostgreSQL 15 · Redis 7 · Docker

---

## Running Locally

**You'll need:** Java 17+, Maven, Docker Desktop

```bash
# 1. Clone
git clone https://github.com/YOUR_USERNAME/gridapp.git
cd gridapp

# 2. Start Postgres + Redis
docker-compose up -d

# 3. Run
./mvnw spring-boot:run
```

Server runs at `http://localhost:8080`. Tables are auto-created on first boot.

---

## API Endpoints

| Method | Endpoint | What it does |
|--------|----------|--------------|
| POST | `/api/posts` | Create a post |
| POST | `/api/posts/{postId}/comments` | Add a comment (human or bot) |
| POST | `/api/posts/{postId}/like?authorId=` | Like a post |
| GET | `/api/posts/{postId}` | Get a post |
| GET | `/api/posts/{postId}/comments` | Get all comments |

---

## Bot Guardrails (Redis)

Before any bot comment hits the database, it clears three checks:

- **Max 100 bot replies per post** — atomic Redis counter, hard cap, no cheating
- **Max 20 depth levels** — no thread goes deeper than 20
- **1 bot per human per 10 min** — cooldown key with TTL, auto-expires

Virality scoring runs in parallel: bot reply = +1, human like = +20, human comment = +50.

---

## Notifications

First bot interaction → instant notification + 15-min cooldown set.  
Subsequent interactions during cooldown → queued in a Redis list.  
A cron job runs every 5 min and flushes the queue as a single summary:

```
Bot bot_5 and [3] others interacted with your posts.
```

---

## Checking Redis

```bash
docker exec -it grid_redis redis-cli

GET post:{postId}:virality_score              # virality score
GET post:{postId}:bot_count                   # how many bots replied
LRANGE user:{userId}:pending_notifs 0 -1      # queued notifications
TTL cooldown:bot_{botId}:human_{userId}       # seconds left on cooldown
```
