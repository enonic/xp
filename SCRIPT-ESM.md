# XP Script Runtime Modernization — ES Modules and the Web-Standard Surface

**Status:** Draft v0.1

**Date:** 2026-08-03

**Companion documents:** [`SCRIPT-SECURITY.md`](SCRIPT-SECURITY.md),
[`SCRIPT-DEBUGGING.md`](SCRIPT-DEBUGGING.md), [`CLOUD-ARCHITECTURE.md`](CLOUD-ARCHITECTURE.md),
`docs/design/graaljs-execution-pipeline.md` (on the GraalJS pipeline branch)

---

## 1. Purpose

What developers actually want is "my npm dependencies work without bundler
gymnastics" and a module system that matches the ecosystem they live in. This
document defines which compatibility surface XP commits to on GraalJS, how ES modules
land, and how the authority-carrying pieces (network, timers) are built so they align
with the capability model instead of undermining it.

The strategic decision: **target the web-standard runtime surface — the WinterTC
"Minimum Common API" that Cloudflare Workers, Deno, Vercel Edge and Bun converge on —
not Node.js emulation.** Node compatibility proper (`node:fs`, `node:http`,
`process`, native addons, runtime `node_modules`) is a multi-year tar pit consisting
mostly of ambient authority — `process.env` is exactly the config-leakage problem
`SCRIPT-SECURITY.md` exists to close. Meanwhile serious npm packages ship
web-standard builds because edge runtimes are deployment targets. That is the bar
that buys the most packages per unit of effort, and it is a published standard, not
"whatever Node does."

Everything in this document is **Graal-only**. Nashorn is frozen as is
(`SCRIPT-SECURITY.md` §8); the new surface is itself migration pressure.

## 2. Architectural decisions

1. **The compatibility target is WinterTC Minimum Common API vX**, published as an
   explicit table ("implemented / XP-specific / excluded") so package authors and
   Market developers get a straight answer.
2. **Three buckets, three treatments:**
   - *Free with the engine:* `globalThis`, `console`, modern ECMAScript. Verify and
     lock with tests; do not undo.
   - *Data-shaped shims:* `TextEncoder`/`TextDecoder`, `URL`/`URLSearchParams`,
     `atob`/`btoa`, `structuredClone`, `crypto.getRandomValues` + digest, a `Buffer`
     polyfill (npm's most-probed global; pure data, no authority), `FormData` and a
     minimal Blob over XP binary streams, `process.env.NODE_ENV` as a constant stub
     (never the real environment). Implemented by integrating proven polyfills into a
     per-context init module honoring the pool rule: globals must be immutable or
     idempotently initializable per context.
   - *Authority-carrying APIs are capabilities:* `fetch` and timers are granted
     handles from day one. The web-standard API *is* the capability handle — trusted
     tier resolves it unrestricted; restricted tier enforces the manifest.
3. **`fetch` is platform-owned and built on `java.net.http`.** One host-side HTTP
   capability (client construction, pooling, HTTP/2, TLS, redirects, timeouts) in XP
   core; `fetch` is its standard surface. lib-http-client already sits on the JDK
   client (via Methanol), but as a bundled library it is app Java — the platform
   cannot scope or meter it. Ownership, not dependency choice, is what the
   capability model requires.
4. **`fetch` v1.5 scope: buffered semantics + streaming consumption.**
   `text()`/`json()`/`arrayBuffer()`, `Headers`, redirect control, `AbortSignal`
   timeouts, `FormData` upload — plus an async-iterable body and a
   `text/event-stream` convenience. Streaming is cheap because Java streams natively
   (`Flow`-based bodies) and the execution model makes `reader.read()` a blocking
   pull that parks a virtual thread. This covers JSON API clients *and* LLM/SSE
   consumption — the actual demand curve. Explicitly excluded until demanded: the
   full WHATWG Streams machinery (`tee`, `pipeThrough`, transforms, writable side),
   full `Request` semantics.
5. **Connection policy lives on the grant, not the call site.** Per-request proxy,
   client certificates and custom CA trust — lib-http-client params today — are
   deployment policy. They move to the platform-configured egress grant (per
   destination, per tenant). No nonstandard fetch extensions; apps cannot override
   trust decisions in code.
6. **Timers ride the ownership machinery.** `setTimeout`/`setInterval`/
   `queueMicrotask` as `JsFunctionHandle`-routed callbacks on one shared host
   scheduler. Scoped where "later" has a well-defined owner: the main context and
   pinned connection contexts. Request-context policy is explicit (clamp to request
   lifetime or fail loudly) — never Node's anything-goes.
7. **Async syntax without an event loop.** `async` controllers and promise-returning
   handlers are supported by draining microtasks to completion before the response
   returns: host-backed operations block under the hood, resolve their promise,
   execution continues. Synchronous execution wearing async syntax — which is all
   package compatibility requires. Consistent with the pipeline branch's
   contexts≈threads decision; a real event loop remains a separate, later decision
   the ownership machinery does not preclude. Long streaming consumption holds its
   context and therefore belongs on task/pinned contexts, not pool request slots.
8. **ES modules land on the new pipeline, decided before the wrapper freezes.**
   `Source` with the module MIME type; resolution against app resources; explicit
   CJS(`require`)↔ESM interop rules with the module wrapper. XP `require` remains
   for compatibility. This is schedule-critical rather than effort-critical: it
   touches the same wrapper design the pipeline branch is finalizing (an open
   question there), and deciding it late means reworking warm code.
9. **Bundling remains the dependency story.** Build-time bundling of npm deps into
   app bundles, as today. XP is not a package manager at runtime; no bare-specifier
   resolution against a live registry.
10. **The commitment is regression-tested.** A package compatibility corpus — a
    dozen real npm packages (validation, dates, markdown, GraphQL client, an LLM SDK)
    bundled and exercised in CI on every build — is the mechanism that converts
    "node-style compatibility" from a vibe into a contract, exactly parallel to the
    NoDB golden-query corpus.

## 3. Non-goals

- Node emulation: `node:*` modules, `process` (beyond the `NODE_ENV` stub), native
  addons, runtime `node_modules` resolution.
- A real event loop / intra-request parallelism (revisitable; nothing here unwinds).
- Full WHATWG Streams, `WebSocket` client, service-worker-isms — until the corpus or
  a customer proves need.
- Any of this surface on Nashorn.

## 4. lib-http-client deprecation

With `fetch` built in, lib-http-client is deprecated **together with Nashorn** — its
remaining constituency is exactly the deprecated tier, since `fetch` is Graal-only.

- Feature disposition: bulk usage covered by fetch v1.5; multipart covered by
  `FormData`; proxy/client-cert/trust params absorbed into grants (decision 5).
- Mechanics are gentle: lib-http-client is bundled per app, so old apps keep working
  in the trusted tier indefinitely — it ages out; no removal event. Deprecated in
  Market and docs; no new majors.
- The restricted tier deprecates it automatically: a closed context has no bean
  bridge, so it physically cannot run there.
- Interim: it remains the HTTP story for Nashorn apps until they migrate engines.
- The operational argument to lead with: **a bundled HTTP client cannot be patched
  centrally.** A CVE in a library compiled into two hundred app jars is two hundred
  rebuilds; under platform-provided `fetch` it is one XP patch release. This is the
  strongest single argument for platform-owned capabilities, independent of
  sandboxing.

## 5. Delivery phases

Prerequisites: GraalJS pipeline branch merged; capability-registry skeleton from
`SCRIPT-SECURITY.md` stage 1 available before the `fetch`/timer gates (so they are
born as granted handles, not retrofitted). Shims, baseline and corpus can start the
day the branch merges.

Total ≈ **1.6M output tokens.**

| Gate | Deliverable | Est. |
|---|---|---|
| E-0 | Baseline verification: engine-provided surface locked with tests | ~30k |
| E-A | Data-shaped shims (incl. Buffer, URL, FormData/Blob) as per-context init module | ~250k |
| E-B | Platform HTTP capability + `fetch` v1.5 (buffered + async-iterable body + SSE convenience; egress-scope hook wired, trusted tier unrestricted) | ~500k |
| E-C | Timers: routed callbacks, shared scheduler, context-scoping semantics + tests | ~180k |
| E-D | Async handler support: microtask drain to completion, unhandled-rejection policy, pool interaction tests | ~200k |
| E-E | ES modules: module `Source`s, resolution, CJS↔ESM interop with the wrapper, dev-mode reload | ~300k |
| E-F | Compatibility corpus in CI + published WinterTC-vX table | ~130k |

Variance drivers: **fetch** (holds only if v1.5 scope is ruthlessly pinned — the
excluded list in decision 4 is part of the gate) and **ESM** (cost rises if decided
after the wrapper design freezes).

## 6. Risks and open questions

1. **fetch scope creep:** the spec surface is enormous; every "just add Request
   cloning" request must clear the corpus-or-customer bar.
2. **ESM/wrapper timing:** landing E-E after the pipeline branch finalizes its
   wrapper means touching the same code twice — the pattern this whole program
   avoids.
3. **Event-loop pressure:** sync-under-async is invisible until a package genuinely
   needs timer-driven concurrency inside a request; the corpus should include one
   such package deliberately, to document the boundary rather than discover it.
4. **Corpus selection bias:** packages must be chosen by Market/customer reality,
   refreshed periodically, not by what happens to pass.
5. **Grant model for egress** (shared with `SCRIPT-SECURITY.md`): destination
   allowlists, per-destination policy (proxy/trust), metering — one design serving
   `fetch`, future lib-http-client majors, and webhook senders.

## 7. Definition of success

- A developer bundles a mainstream npm package targeting edge runtimes and it runs
  on XP without shims or forks; the published table says so in advance.
- `await fetch(...)` works in a controller, including streaming an LLM response,
  with egress governed by the manifest on the restricted tier.
- New apps author ESM; existing CJS apps run unchanged side by side.
- HTTP policy (proxy, trust, allowlists) is tenant configuration, not app code, and
  an HTTP CVE is one platform patch, not an ecosystem rebuild.
- The corpus is green in CI, and its failures — not support tickets — are how
  compatibility regressions are discovered.
