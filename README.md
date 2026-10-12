# JamJar

JamJar — music streaming database app. Final project for **CSSE 333 Intro to Databases** (Rose-Hulman).

Java Swing desktop client backed by SQL Server over JDBC, with login/register, salted SHA-256 password hashing, artist/listener roles, and listener stats (favorite song / artist / genre).

> Ported from the team repo [`rhit-everetmr/JamJar`](https://github.com/rhit-everetmr/JamJar) to preserve my contributions under my own account.

## Features

- **Auth:** register + login with per-user salt and SHA-256 hash (with plaintext fallback for legacy rows)
- **Roles:** artist / listener profiles (`Artist`, `Listener` tables, `AddUser` / `UpdateUser` / `AddListener` stored procedures)
- **Listener stats:** favorite song, artist, genre via multi-table JOIN
- **UI:** Swing screens — Login, Register, Home, Listener Stats, Update User
- **DB layer:** `DatabaseConnectionService` (SQL Server JDBC) + `UserService` (prepared statements / callable statements)

## Tech stack

Java 17+ · Swing · SQL Server (T-SQL stored procedures) · JDBC (`mssql-jdbc`)

## Project layout

```
JamJar/                 # Eclipse Java project
  src/jamjar/
    ui/                 # Swing screens (Main, LoginUI, RegisterUI, JamJarHomeUI, ...)
    services/          # DatabaseConnectionService, UserService
    models/            # ListenerStats
    jamjar.properties  # DB connection config (see Setup — DO NOT commit real credentials)
```

## Setup

1. Create the `JamJar` SQL Server database and run the project SQL (tables + `AddUser`, `UpdateUser`, `AddListener` procedures).
2. Copy the config template and fill in your own values:
   ```bash
   cp JamJar/src/jamjar.properties.example JamJar/src/jamjar.properties
   # or on Windows: copy JamJar\src\jamjar.properties.example JamJar\src\jamjar.properties
   ```
   ```properties
   serverUsername=YOUR_DB_USER
   serverPassword=YOUR_DB_PASSWORD
   serverName=your-server.example.edu
   databaseName=JamJar
   ```
   The app loads `jamjar.properties` from the compiled `bin/` output dir at runtime (`Main.loadProperties`).
3. Add `mssql-jdbc` to the classpath, build in Eclipse (or `javac`), and run `jamjar.ui.Main`.

## Security note

`jamjar.properties` holds DB credentials — it is git-ignored. Never commit real passwords. Use `jamjar.properties.example` as the template.

## Contributors

- Nakul Mital ([@rhit-mitaln](https://github.com/rhit-mitaln))
- Original team repo: [`rhit-everetmr/JamJar`](https://github.com/rhit-everetmr/JamJar)

## Course

CSSE 333 — Intro to Databases, Rose-Hulman Institute of Technology.
