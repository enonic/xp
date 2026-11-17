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

The controller contract is proposed as:

```javascript
exports.execute = function (name, args) {
    if (name !== 'echo') {
        throw new Error('Unsupported declared tool: ' + name);
    }
    const result = {message: args.message};
    return {
        content: [{type: 'text', text: JSON.stringify(result)}],
        structuredContent: result
    };
};
```

`name` is the local tool name; `args` contains validated arguments. Completed results use standard MCP content, optional `structuredContent`, `isError` and metadata. Controllers omit protocol-owned `resultType`; the SDK supplies the completion discriminator. The protocol adapter owns JSON-RPC and revision-specific fields. Script dispatch and public-name allocation are subsequent implementation slices. The `/api/system:mcp` Universal API delegates HTTP/JSON handling to the SDK transport.

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

`McpToolAdapter` uses the SDK's modern `Tool`, `CallToolRequest`, `CallToolResult` and `McpSyncTool` contracts for the `2026-07-28` revision. Invocation binding retains the loaded SDK `Tool` instance. The adapter converts plain JavaScript controller results into SDK results using SDK polymorphic content types and binds callbacks to SDK handlers. SDK schema validation checks arguments and declared structured output. Content-only error results are supported; any structured error data must conform to the declared output schema. Explicit JSON null structured data is supported when the schema allows it. Unexpected callback exceptions propagate for protocol-layer handling.

SDK JSON readers are tolerant and default some missing nested fields. This integration uses SDK validation and disables scalar coercion; it does not claim complete protocol conformance from JSON conversion alone. The Universal API uses the SDK's modern server and HTTP servlet transport. Embedded HTTP tests exercise discovery, calls, request validation and XP API routing with an SDK client. Full conformance remains separate verification work. Version 2.2.0 also provides legacy APIs, which are not bound here; legacy initialize requests receive an explicit unsupported-version error.

The runtime includes the SDK's OSGi bundles and required dependencies. Reactor is pinned to the SDK's `3.9.0-RC1` dependency; the Jackson 2 validator is NetworkNT 2.0.8. XP's existing Jackson versions are retained.

## Universal API endpoint

The built-in API is available at `/api/system:mcp` on XP's web connector. It requires no site, webapp or custom vhost. It supports protocol revision `2026-07-28` through the official SDK server and servlet transport. This iteration is for isolated development use: the API declares `role:system.everyone`, retains the SDK's default Origin checks, and has no OAuth or MCP-specific authorization enforcement.

The production tool repository is currently empty. Tool catalog integration and script dispatch belong to #12634. Tests inject an SDK echo repository into the handler; no echo registration or fixture dependency ships in the production catalog.

Requests use HTTP POST with JSON bodies. The SDK handles JSON-RPC decoding, mirrored protocol/method/name headers, metadata, version errors, notification acknowledgments and response statuses. GET/DELETE receive the modern transport's method rejection. Responses in this slice are JSON; progress delivery is #12655 and long-lived subscriptions are #12656.

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

- Embedded Jetty tests use localhost port 0 and exercise the actual XP API registry, SlashApiHandler and WebSerializerServiceImpl, with unrelated services mocked.
- The modern SDK client discovers the empty production repository and invokes the injected test-only echo repository.
- Raw HTTP tests cover malformed JSON/batches, media types, unsupported verbs/subpaths, mirrored headers, protocol versions, notification acknowledgment and shutdown.
- A manual smoke run with local **Claude Code 2.1.318** connected to that same embedded routing harness and invoked `mcp__xp__echo` once with `XP MCP smoke test`. The client trace contained the matching successful tool result. The temporary endpoint was stopped after the run. This is endpoint integration evidence; a full XP distribution boot was not exercised by that smoke run.

## Test fixture lifecycle

The echo example belongs in a separately packaged development/test app. Production XP and Guillotine have no dependency on it. Installing the app contributes tools; stopping or uninstalling it removes contributions through standard application/resource lifecycle. Keep automated fixture sources in test scope. A later integration test must verify removal without an XP rebuild or restart and confirm that Guillotine continues working.

## Delivery boundaries

This slice implements group metadata, discovery, shared YAML loading directly into SDK tools, invocation binding and the SDK-backed Universal API JSON endpoint. It adds no app script-controller execution, progress helper, subscription streams, authentication or MCP-specific authorization. Existing XP permissions remain intact. All security implementation is iteration 2. Echo exists only in test fixtures. SDK-client tests and the local-AI smoke run cover the endpoint with an injected fixture; production app tools remain pending #12634.

Guillotine contributes through this same contract. Its controller invokes its internal `SchemaProvider` in-process, with no HTTP request and no `lib-guillotine-query` dependency.

## References

- [MCP tools specification](https://modelcontextprotocol.io/specification/2026-07-28/server/tools)
- [Official Java SDK 2.2.0](https://github.com/modelcontextprotocol/java-sdk/releases/tag/v2.2.0)
- [Modern Java server documentation](https://java.sdk.modelcontextprotocol.io/latest/server-modern/)
- [Contract and milestones](https://github.com/enonic/xp/issues/12626)
- [Descriptor/controller contract](https://github.com/enonic/xp/issues/12628)
