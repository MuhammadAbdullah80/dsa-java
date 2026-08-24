# Contributing

## Getting set up

```sh
javac -d out src/main/java/dsa/*.java
java -cp out dsa.Main
```

JDK 17 or newer. No build tool required — the point of the repo is the implementations, so it stays runnable with a bare JDK.

## Before opening a pull request

- The test command above passes
- New behaviour has a test alongside it
- Public functions carry a comment saying *why*, not restating the signature

## Commit messages

Explain why the change is needed. The diff already says what it does.
