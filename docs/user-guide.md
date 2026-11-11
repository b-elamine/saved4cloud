# User guide

A short version for now.

## Try it

Describe the app in `shop.arch.yaml`:

```yaml
name: shop
components:
  - name: Api
    type: App
    attributes: { Domain: backend }
    exposure: external
    ports: [{ name: http, number: 8080, protocol: http }]
  - name: Db
    type: Data
    attributes: { Domain: database }
    ports: [{ name: sql, number: 5432, protocol: tcp }]
connectors:
  - name: Internet
    external: true
    protocol: https
  - name: ApiToDb
    protocol: tcp
links:
  - { port: Api.http, connector: Internet, direction: in }
  - { port: Api.http, connector: ApiToDb }
  - { port: Db.sql, connector: ApiToDb }
```

Write the rules in `shop.secdsl`:

```
#attribute Domain = (backend, database);
#context backend = (Domain=backend);
#context database = (Domain=database);
#property Confidentiality(backend, database);
```

Then run it:

```bash
java -jar saved-cli/target/saved-cli.jar shop.arch.yaml shop.secdsl --validate
```

It finds two problems:

```
! 'Api' is exposed (exposure: external) but no Authentication rule guards it -- unauthenticated entry point
! Confidentiality requires a protected channel but connector 'ApiToDb' is plaintext (protocol: tcp) -- data crosses this connector unprotected
```

## Useful options

| Option | What it does |
|---|---|
| `--validate` | Prints the report, writes nothing |
| `--strict` | Fails if there's any warning |
| `--html` | Also writes an interactive graph |
| `--init-secdsl` | Creates a starter rules file from your architecture |
| `--serve` | Opens the browser editor, on port 8080 by default |

`--validate` alone still exits with 0 when it only finds warnings, so use `--strict` too in CI.
