# saved4cloud

A small command-line tool that checks if a cloud app's design respects its security rules, before anything gets deployed.

You write two files: one describes your architecture (`.arch.yaml`), the other your security rules (`.secdsl`). The tool merges them and tells you where the design doesn't deliver what the rules ask for, like a database connection that should be protected but runs in plaintext.

```bash
mvn package -q
java -jar target/saved-cli.jar my-app.arch.yaml my-app.secdsl --validate
```

Needs Java 21 and Maven. There's a short example in the [user guide](docs/user-guide.md).

Still a work in progress.
