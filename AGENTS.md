# AGENTS.md

## Verify
- After modifying Java sources run `./gradlew test`
- New and changed Java code must pass Checkstyle with no warnings: `./gradlew checkstyleMain checkstyleTest` (scope with a project path, e.g. `:core:core-api:checkstyleMain`). Rules: `checkstyle.xml`

## Java
- Use Java 25 syntax (e.g. `case null` patterns in switch, pattern matching, records, `var` where it improves clarity)
- Never use fully-qualified class names; import and use the short name, including static calls (`import static org.mockito.Mockito.times`) and `java.*` types. Only exception: two types with the same simple name
- New API should not accept or return `null`. Start new code as `@NullMarked` (JSpecify, package- or class-level) and mark the rare genuine exception `@Nullable`. Prefer overloads, empty collections, `Optional` returns, or throwing over a nullable parameter or return value
- Declare fields at the top of the class, before constructors, methods, initializers and inner classes
- Don't add clarifying comments; code should speak for itself. To explain an edge case, write a regression test that covers it
- In `-api` modules, every class, interface and method has Javadoc. Write it for the API user: what it does and how to use it, not how it is implemented or why it was designed that way

## Tests
- Instead of `assertThrows` use AssertJ's `assertThatThrownBy`
- To assert a call does not throw, use `assertDoesNotThrow(() -> ...)`; not a bare call with a `// should not throw` comment

## GitHub
- Issue titles must not exceed 50 characters
- PR title is the full issue title followed by the issue number: `Fix the thing #1234`
- Open PRs as drafts
