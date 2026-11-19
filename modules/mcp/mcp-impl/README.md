# MCP app contribution contract

Public APIs live in `core:core-api`, in `com.enonic.xp.mcp`. Resource discovery and YAML parsing live in `mcp:mcp-impl`.

## App resources

```text
src/main/resources/
  mcp/
    example/
      example.yaml
      example.js
```

A group is identified by its application key and directory name. Discovery requires a YAML descriptor. `.yaml` takes precedence over `.yml`. JavaScript alone contributes no tools. Groups are returned in name order; tools retain declaration order.

```yaml
kind: MCP
allow:
  - role:example.reader
tools:
  - name: echo
    title: Echo a message
    description: Return the supplied message.
    inputSchema:
      type: object
      properties:
        message:
          type: string
      required: [message]
      additionalProperties: false
    outputSchema:
      type: object
      properties:
        message:
          type: string
      required: [message]
      additionalProperties: false
    annotations:
      readOnlyHint: true
      idempotentHint: true
      openWorldHint: false
```

`kind` is optional and has the value `MCP` when present. `tools` is required and may be empty. `allow` contains XP principal keys and defaults to an empty declaration when omitted. Permission enforcement belongs to iteration 2.

YAML tool definitions bind directly to the official SDK's `Tool` type. Supported MCP fields, JSON Schemas, `_meta` and nested JSON nulls are retained by SDK deserialization. Unknown tool fields follow the SDK's deserialization behavior. Local tool names contain 1–128 ASCII letters, digits, dots, underscores or hyphens and are unique within the group. `inputSchema` is a required object; an optional `outputSchema` is an object. Descriptor loading validates schemas through the SDK; argument and output validation occurs during invocation.

The controller exports:

```javascript
exports.execute = function (name, args) {
    if (name !== 'echo') {
        return {
            content: [{type: 'text', text: 'Unsupported declared tool: ' + name}],
            isError: true
        };
    }
    const result = {message: args.message};
    return {
        content: [{type: 'text', text: JSON.stringify(result)}],
        structuredContent: result
    };
};
```

`name` is the local tool name; `args` contains validated arguments. Completed results use standard MCP content, optional `structuredContent`, `isError` and metadata. Controllers omit protocol-owned `resultType`; the SDK supplies the completion discriminator. The protocol adapter owns JSON-RPC and revision-specific fields. Script dispatch invokes the declared controller through `PortalScriptService`, with conversion to a plain result map inside a bound script execution. The `/api/system:mcp` Universal API delegates HTTP/JSON handling to the SDK transport.

## Tool errors

A failing tool call completes with a standard result carrying `isError: true`, so the model can read the failure and recover. Both the JSON response and the progress stream deliver the same result.

- A controller reports a failure the model should see by returning it: `{isError: true, content: [{type: 'text', text: 'Unsupported declared tool: ' + name}]}`. The text reaches the client unchanged.
- A thrown failure returns `MCP tool execution failed (error id 1a2b3c4d)` with no exception details. This covers JavaScript errors, exceptions thrown by Java beans and libraries the script calls, a missing controller, a missing `execute` export and a non-object return. The server log records the tool name, the same error id and the exception with its stack trace at WARN level.
- Invalid arguments and invalid controller results return `isError` results describing the problem.

The adapter converts runtime exceptions and XP's `ResourceError`, which script engines raise for failures such as a stack overflow in the controller. JVM errors raised outside a script call reach the SDK unconverted.

JSON-RPC protocol errors remain for invalid requests, invalid parameters, unknown tools, tools that are no longer available and invalid progress tokens.

## Invocation progress

Controllers may accept an optional third argument: `execute(name, args, context)`. Existing two-argument controllers continue to work. `context` implements the SDK-independent `McpInvocation` API in `core-api`; SDK integration remains in `mcp-impl`.

```javascript
exports.execute = function (name, args, context) {
    context.progress(0, args.steps, 'Starting');
    for (let step = 1; step <= args.steps; step++) {
        context.progress(step, args.steps, 'Step ' + step);
    }
    const result = {completedSteps: args.steps};
    return {
        content: [{type: 'text', text: JSON.stringify(result)}],
        structuredContent: result
    };
};
```

Supported helper methods:

- `context.isProgressSupported()` checks whether the current invocation can report on the calling thread.
- `context.progress(progress)` omits total and message.
- `context.progress(progress, message)` omits total.
- `context.progress(progress, total)` omits message.
- `context.progress(progress, total, message)` reports all fields.

Progress and total must be finite and nonnegative; progress must be at most the supplied total. The first progress value may be zero; repeated or decreasing values are ignored. Reports use synchronous controller execution on the invocation's thread. Retained helpers, foreign-thread calls and helpers shadowed by a nested invocation cannot emit. Reports made without progress support are no-ops.

A client opts in by supplying a string or integer `_meta.progressToken`. The adapter selects an SDK streaming response for valid calls carrying a token. The SDK sends `notifications/progress` on that call's SSE response, retaining the original token, followed by the final standard result, which carries `isError: true` when the tool fails. A token-bearing call may finish without reporting progress. No separate progress-capability flag is required by this protocol revision. Calls without a token execute synchronously and return JSON. Invalid input is rejected before controller execution or streaming. Invalid progress-token types produce an invalid-params protocol error.

The deferred streaming body captures XP caller context and a separate portal-request wrapper before moving to the SDK execution thread. It restores the captured context for script execution and result validation, then deactivates the helper in `finally`. Context attributes and referenced web data retain XP's shallow-copy semantics. Progress never uses the catalog change feed or `subscriptions/listen`.

The SDK owns wire-stream cancellation and cleanup. Completion and exceptions deactivate the helper. A disconnected client receives no subsequent frames on the closed stream; disconnect does not guarantee immediate controller cancellation or immediate changes to `isProgressSupported()`. Enforced deadlines, cancellation and rate/budget controls remain iteration 2 in #12639.

Test-only YAML/controller resources live under `src/test/resources/mcp/progress`, and `src/test/resources/mcp/failing` holds a controller that returns an error result, throws a JavaScript error, or calls Java code that throws. They contribute no production tools or app dependency. The fixture exercises the actual JavaScript helper, schemas and final result. Latch-controlled HTTP peers cover an operation that remains running while progress is read, concurrent callers, exact SSE frames, error completion, disconnect and catalog-subscription separation. The module runs its fixture on both configured XP script engines through `test` and `testGraalJs`; the local Graal initialization failure currently blocks the Graal fixture before controller execution.

## Descriptor discovery API

- `McpDescriptorService.getByKey` returns an optional group.
- `getByApplication` returns an immutable collection, including an empty collection for apps without contributions.
- `getControllerResourceKey` resolves the conventional controller path without requiring the resource to exist.
- `McpDescriptor` contains only application/group identity and declared principals in `core-api`.
- Internal `LoadedMcpGroup` contains that metadata and an immutable ordered list of SDK `Tool` instances in `mcp-impl`.
- `McpDescriptorServiceImpl.getLoadedByKey` and `getLoadedByApplication` expose cached loaded groups to the implementation layer. Public descriptor access projects metadata from those same groups.

Discovery uses XP's `DescriptorKeyLocator` and `ResourceService` resource processing. YAML loading uses the shared `YmlParserBase` from `core-schema`, with strict parsing enabled for MCP descriptors. The shared parser binds group properties and SDK tools directly. A Jackson mixin requires tool name and input schema during loading, and the SDK validates the JSON Schemas. Parsing failures identify the descriptor resource. Resource caching and invalidation are delegated to the existing resource infrastructure.

## Official Java SDK integration

`mcp-impl` uses official Java SDK **2.2.0** through `mcp-core` and `mcp-json-jackson2`, matching XP's Jackson 2 stack. SDK classes remain internal to this module; `core-api` exposes XP group metadata with no SDK dependency. There is no parallel XP tool-definition model or map-to-tool conversion. YAML deserialization creates SDK tools once through XP's resource-processing path. Local name checks and SDK schema validation happen while loading the group.

`McpToolAdapter` uses the SDK's modern `Tool`, `CallToolRequest`, `CallToolResult` and `McpSyncTool` contracts for the `2026-07-28` revision. Invocation binding retains the loaded SDK `Tool` instance. The adapter converts plain JavaScript controller results into SDK results using SDK polymorphic content types and binds callbacks to SDK handlers. SDK schema validation checks arguments and declared structured output. Content-only error results are supported; any structured error data must conform to the declared output schema. Explicit JSON null structured data is supported when the schema allows it. Runtime exceptions and `ResourceError`s thrown by the callback become generic `isError` results with a logged error id, as described in [Tool errors](#tool-errors); SDK protocol exceptions thrown by the callback propagate to the SDK.

SDK JSON readers are tolerant and default some missing nested fields. This integration uses SDK validation and disables scalar coercion; it does not claim complete protocol conformance from JSON conversion alone. The Universal API uses the SDK's modern server and HTTP servlet transport. Embedded HTTP tests exercise discovery, calls, request validation and XP API routing with an SDK client. Full conformance remains separate verification work. Version 2.2.0 also provides legacy APIs, which are not bound here; legacy initialize requests receive an explicit unsupported-version error.

The runtime includes the SDK's OSGi bundles and required dependencies. Reactor is pinned to the SDK's `3.9.0-RC1` dependency; the Jackson 2 validator is NetworkNT 2.0.8. XP's existing Jackson versions are retained.

## Universal API endpoint

The built-in API is available at `/api/system:mcp` on XP's web connector. It requires no site, webapp or custom vhost. It supports protocol revision `2026-07-28` through the official SDK server and servlet transport. This iteration is for isolated development use: the API declares `role:system.everyone`, retains the SDK's default Origin checks, and has no OAuth or MCP-specific authorization enforcement.

`McpCatalog` discovers contributions from started installed apps through the descriptor service and owns an immutable ordered group snapshot plus one shared SDK change feed. Apps and groups are sorted by name; tools retain descriptor order. An installation with no contributions has an empty catalog. A scan rebuilds the snapshot from current app/resource state and publishes it. `McpCatalog` scans once on activation; `McpCatalogMonitor` triggers further scans on its own activation, on its schedule and on application events. Lists, lookups and calls read the snapshot published by the last scan; they take no lock and start no scan. Lifecycle monitoring publishes tool-list changes as described below. An application whose descriptors fail to load, or whose public tool names are oversized or duplicated, is excluded from the snapshot as a whole; all other applications keep listing and serving their tools.

`McpToolCatalog` implements the SDK tool repository. It projects the shared groups into namespaced definitions and caches SDK bindings by snapshot identity. Equal group content retains the same snapshot, including when the descriptor service returns new equivalent instances. Repeated lookups reuse bindings and avoid repeated schema validation during binding. Bindings are rebuilt when a scan publishes a new snapshot. Requests racing on a new snapshot may each rebuild them; every rebuild yields equivalent bindings, and a later request rebuilds again if an older result was kept. Bindings of an excluded application leave the cache with its tools: a lookup of their names returns the unknown-tool protocol error, and a retained binding reports the tool as no longer available.

`McpServerFactory` composes the SDK features, JSON mapper, schema validator, shared change publisher and servlet transport. `McpUniversalApiHandler` owns XP HTTP bridging and transport shutdown. Future prompt and resource repositories can consume the same group discovery and change feed, with explicit SDK feature registration in the factory. Their descriptor sections and execution contracts remain future work.

Public names use `encode(application)__encode(group)__encode(localName)`. ASCII letters, digits, dots and hyphens are preserved; `_` becomes `_u`; other UTF-16 code units become `_xhhhh` with four lowercase hexadecimal digits. This keeps component boundaries unambiguous. A name longer than 128 characters excludes its application; the logged warning instructs to shorten the contributing names. For example, the Chuck Norris app contributes `com.enonic.app.chucknorris__chucknorris__fact`.

Calls resolve the public name to the original group and local tool name, validate arguments, and invoke `execute(localName, args, context)` through the app's controller. Controllers must exist, export `execute`, and return an object. The SDK adapter validates MCP results and declared structured output. Script errors and execution exceptions return generic `isError` results with a logged error id, as described in [Tool errors](#tool-errors). The originating portal request is rebound during deferred response serialization; the contributing app is selected for controller execution and the previous app is restored in a `finally` block. XP caller identity is preserved, with no administrator elevation.

Tests inject an SDK echo repository into the handler; no echo registration or fixture dependency ships in the production catalog. The separately installed Chuck Norris app is the initial real contribution for manual integration checks.

Requests use HTTP POST with JSON bodies. The SDK handles JSON-RPC decoding, mirrored protocol/method/name headers, metadata, version errors, notification acknowledgments and response statuses. GET/DELETE receive the modern transport's method rejection. Calls without progress tokens return JSON. Calls with progress tokens and tools-only `subscriptions/listen` requests use separate SDK-managed SSE response streams. Broader subscriptions remain #12656.

Example discovery request against a running development server:

```sh
curl http://localhost:8080/api/system:mcp \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -H 'MCP-Protocol-Version: 2026-07-28' \
  -H 'Mcp-Method: server/discover' \
  --data '{"jsonrpc":"2.0","id":1,"method":"server/discover","params":{"_meta":{"io.modelcontextprotocol/protocolVersion":"2026-07-28","io.modelcontextprotocol/clientInfo":{"name":"xp-dev","version":"1.0"},"io.modelcontextprotocol/clientCapabilities":{}}}}'
```

`ServletResponseBody` lets the SDK servlet write through XP's existing response serializer. XP may consume JSON bodies before invoking a Universal API; `McpServletRequest` replays the cached UTF-8 body for the SDK. Other request attributes and multi-value headers are retained. SDK request limits apply after XP's eager body read; complete ingress budgets and early rejection remain iteration-2 work in #12639.

### Endpoint test evidence

- Progress HTTP tests capture exact SSE frames for string/integer tokens and final results, concurrent calls, final errors and no-token JSON responses. The actual controller fixture passes on Nashorn; GraalJS tests currently fail at the known engine-initialization issue before executing the fixture.
- A local Claude Code **2.1.318** run called a test-only operation once with `progress-12655` through the embedded routing harness. The client supplied a progress token, the server reported steps 0, 1 and 2, and Claude received the correct final result. This verifies streaming-call compatibility; progress rendering in the client UI was not established. The harness used mocked script exports and was stopped after the run.

- Embedded Jetty tests use localhost port 0 and exercise the actual XP API registry, SlashApiHandler and WebSerializerServiceImpl, with unrelated services mocked.
- The modern SDK client discovers an empty catalog and invokes the injected test-only echo repository.
- With one broken application next to a healthy one, `tools/list` returns the healthy tools and a call to the broken application's tool returns the unknown-tool protocol error.
- SDK subscription tests verify additions, definition changes, removals, unchanged scans, repeated cycles, concurrent clients, disconnect/reconnect, unsupported filters and graceful shutdown. A routed test exercises the real registry/SlashApiHandler and deferred response/body replay.
- A local **Claude Code 2.1.318** subscription smoke run opened `subscriptions/listen` against the embedded routing harness. A test-only bootstrap tool added echo and refreshed the catalog. After `ToolsListChanged`, Claude automatically fetched `tools/list` and called the newly added echo with `smoke-12635`. No reconnect or second discovery was observed. The exact SSE frame was not captured in that manual run; deterministic SDK peer tests cover stream notifications. The smoke run also passed after the shared catalog refactor using `McpCatalog`, the tool repository and `McpServerFactory`. This establishes client-refresh behavior in the harness, with a full XP app lifecycle run still pending.
- Raw HTTP tests cover malformed JSON/batches, media types, unsupported verbs/subpaths, mirrored headers, protocol versions, notification acknowledgment and shutdown.
- A manual smoke run with local **Claude Code 2.1.318** connected to that same embedded routing harness and invoked `mcp__xp__echo` once with `XP MCP smoke test`. The client trace contained the matching successful tool result. The temporary endpoint was stopped after the run. This is endpoint integration evidence; a full XP distribution boot was not exercised by that smoke run.

## Catalog lifecycle and notifications

`McpCatalogMonitor` refreshes the shared catalog on XP application install, start, stop and uninstall events. A single daemon also scans every second to observe app upgrades and development resource edits without an application event. XP's native descriptor/resource caching provides resource invalidation. Requests read the snapshot from the last scan, so a change becomes visible after the next monitor scan (about one second) or the lifecycle event that triggers one. No script runs during a scan.

The shared catalog compares complete ordered SDK tool definitions qualified by descriptor identity. Additions, removals, schema changes and metadata changes publish `ServerChange.ToolsListChanged` through the SDK's in-memory `McpChangeFeed`. Identical definitions produce no notification. The group snapshot and observed definition list are updated before publishing. Group permission metadata and empty-group changes update the shared snapshot without a tool-list notification; permission enforcement remains iteration 2. A controller-only edit with unchanged tool definitions needs no list-change notification; script loading follows XP's normal resource lifecycle.

Clients opt in with `subscriptions/listen` using `toolsListChanged: true` in the subscription filter. The SDK acknowledges the supported filter and sends `notifications/tools/list_changed` on that stream. Clients fetch `tools/list` again to obtain the updated definitions. Prompt/resource filters are not acknowledged; those features remain unavailable. Tool calls without progress tokens keep JSON responses. The existing XP Jetty dispatch servlet enables asynchronous requests, and the SDK owns stream framing, keep-alives, disconnect handling and graceful completion on MCP shutdown.

The feed reaches listeners on the same XP instance and retains no event history. Clients must fetch the current tool list on initial connection and after reconnecting; changes while disconnected are not replayed. A scan observes the latest resource state, so intermediate edits can be coalesced. Polling latency is approximately one second plus scan duration. This slice adds no catalog revision token; lists keep the SDK's zero-TTL private caching hints.

A binding checks availability and definition equality against the current snapshot immediately before calling the app executor. Removed tools and bindings with changed definitions are rejected. This check reads the published snapshot without a lock. Script execution runs outside the catalog scan lock; the tool repository holds no lock. A call already executing follows XP's existing script/application lifecycle; stop/uninstall does not add an MCP-specific cancellation guarantee. A lifecycle change can still race with the final availability check. Enforced cancellation belongs to iteration 2.

A contribution that fails to load or produces invalid public names excludes its whole application from the catalog. Listing, calls and change detection continue for all other applications. The excluded definitions publish a tool-list change like any other removal, and unrelated removals are still reported. `McpCatalog` logs a warning with the cause once when an application's failure appears or its message changes, and an info entry once when that application loads again, stops or is uninstalled; repeated scans of an unchanged failure log nothing. A corrected descriptor is picked up by the next scan, and its tools return with a tool-list change. The monitor logs unexpected scan failures and continues scanning. It stops scheduling on component deactivation.

### Manual lifecycle check

Use the separately installed Chuck Norris app (`enonic/app-chucknorris#68`) as the first real contribution. XP runtime and Guillotine have no dependency on it. Test-only echo repositories remain in test sources. #12630 was superseded by the Chuck Norris app task.

1. Connect a local client supporting protocol `2026-07-28` and tool-list subscriptions. Fetch the initial tools list and confirm the subscription acknowledgment.
2. Install/start the Chuck Norris app. Expect a tool-list change and refresh the list to find `com.enonic.app.chucknorris__chucknorris__fact`.
3. Invoke the tool, then update its descriptor. Expect another change only when the advertised definition changes.
4. Stop/uninstall the app. Expect a change, an empty list on an otherwise empty server, and an unknown-tool failure for a stale invocation.
5. Repeat install/remove, including with two clients. Disconnect/reconnect one client and fetch the current list again. Verify other app contributions remain available. No XP source change, rebuild or restart is needed.

Record client/version, protocol, subscription acknowledgment, notifications, authoritative tool lists and stale-call results in #12635. Client UI refresh behavior is client-dependent; SDK peer tests verify wire behavior independently. A real local-AI install/remove cycle and Guillotine coexistence remain manual acceptance checks.

## Delivery boundaries

This slice implements group metadata, discovery, shared YAML loading directly into SDK tools, namespaced catalog, app script-controller execution, catalog lifecycle detection and the SDK-backed Universal API with JSON responses and tools-only subscription streams. Progress uses invocation-scoped SDK streaming notifications; the shared change feed serves catalog/resource changes. Broader subscriptions, authentication and MCP-specific authorization have separate implementation tasks. Existing XP permissions remain intact. All security implementation is iteration 2. Echo exists only in test fixtures. HTTP integration tests exercise catalog and dispatch with mocked script exports; the recorded local-AI smoke run used an injected fixture. A real installed-app fact invocation remains a manual integration gate.

Guillotine contributes through this same contract. Its controller invokes its internal `SchemaProvider` in-process, with no HTTP request and no `lib-guillotine-query` dependency.

## References

- [MCP tools specification](https://modelcontextprotocol.io/specification/2026-07-28/server/tools)
- [Official Java SDK 2.2.0](https://github.com/modelcontextprotocol/java-sdk/releases/tag/v2.2.0)
- [Modern Java server documentation](https://java.sdk.modelcontextprotocol.io/latest/server-modern/)
- [Contract and milestones](https://github.com/enonic/xp/issues/12626)
- [Descriptor/controller contract](https://github.com/enonic/xp/issues/12628)
