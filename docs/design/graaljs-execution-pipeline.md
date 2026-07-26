# GraalJS in XP: why it is experimental, and a proposal for a new script execution pipeline

Status: DRAFT / discussion document
Related epic: [#8714 GraalJS](https://github.com/enonic/xp/issues/8714)

## 1. Summary

GraalJS support in XP is experimental today because the current integration keeps the
Nashorn-era execution model — one engine-wide mutable scope per application, callable from any
thread — and bolts GraalJS onto it with a single `org.graalvm.polyglot.Context` per application
guarded by `synchronized` blocks. GraalJS enforces what the JS spec has always assumed: a JS
realm is single-threaded. The consequences:

1. **Applications become effectively single-threaded.** Every HTTP request, task, websocket
   event and SSE event of an app contends on one monitor.
2. **`lib-task` `executeFunction` cannot work correctly.** The JS closure is handed to a task
   thread as a plain Java `Function` and invoked there without any synchronization, producing
   `IllegalStateException: Multi threaded access requested by thread ...` (or racing with lock
   holders). The same escape hatch exists in `lib-event` listeners and in
   `GraalObjectConverter.toFunction`.
3. **Websocket / SSE listeners only "work" by serialization.** They are dispatched through the
   same global lock from Jetty event threads, with unclear ordering and state-sharing semantics
   ([#8644](https://github.com/enonic/xp/issues/8644),
   [#10873](https://github.com/enonic/xp/issues/10873)).
4. **Value representation mismatches.** Numbers, dates, host objects and `ProxyObject`-backed
   maps do not always round-trip the way Nashorn's did
   ([#9011](https://github.com/enonic/xp/issues/9011),
   [#9339](https://github.com/enonic/xp/issues/9339)).

This document analyses the root causes in the current code and proposes a new execution
pipeline built on **explicit context ownership**: a pool of thread-confined "JS workers" per
application, eager data exchange at the Java/JS boundary, and routable function handles instead
of raw cross-thread closures. It also evaluates the async-servlet option and lists the
compatibility trade-offs ("works differently on GraalJS") we would consciously accept.

## 2. How script execution works today

### 2.1 Structure

- `ScriptRuntimeFactoryImpl` creates one `ScriptExecutor` per `(ScriptRuntime, application)`.
  For GraalJS this is `GraalScriptExecutor`; all apps share one `org.graalvm.polyglot.Engine`
  (code cache), but each executor owns exactly **one** `Context`
  (`modules/script/script-impl/src/main/java/com/enonic/xp/script/impl/ScriptRuntimeFactoryImpl.java`).
- `GraalScriptExecutor` keeps a per-app `ScriptExportsCache<Value>` — the `require()` cache.
  Cached exports are `Value` objects **bound to that single context**.
- `GraalJSContextFactory` builds the context with `HostAccess.ALL` and host class lookup. (It
  used to offer an opt-in `js.nashorn-compat` mode behind the `xp.script-engine.nashorn-compat`
  system property — removed on this branch, see §4.6.)

### 2.2 Where the locking is

Everything that touches the context synchronizes on the context object itself:

| Call path | Lock |
|---|---|
| `GraalScriptExecutor.requireJs` (module eval + require) | `synchronized (context)` |
| `GraalScriptExports.executeMethod` / `getMethod` (all controller invocations: HTTP verbs, `webSocketEvent`, `sseEvent`, task `run`, ...) | `synchronized (context)` |
| `GraalFunctionScriptValue.call` (calling a JS function captured as `ScriptValue`) | `synchronized (context)` |

So for one application there is exactly one context and one monitor. **Every JS execution of an
app serializes**: N concurrent requests to the same app are handled one at a time. Under
Nashorn (`ScriptExecutorImpl`) there is no such lock — Nashorn tolerated (unsafe) concurrent
execution, which is what XP's request model silently relied on.

### 2.3 Where the locking is bypassed — the real correctness bugs

Several APIs convert a JS function into a plain Java functional interface and hand it to a
different thread. Those invocations do **not** go through `GraalFunctionScriptValue.call` and
carry no synchronization at all:

- **`lib-task` `executeFunction`**: `ExecuteFunctionHandler.setFunc(Function<Void,Void>)`
  receives a host proxy over the JS closure; `TaskWrapper.run` calls `taskFunction.apply(null)`
  on a task thread. If any other thread is inside the context (very likely), GraalJS throws
  `IllegalStateException: Multi threaded access requested ...`. This is the user-visible
  "executeFunction is impossible on GraalJS".
- **`lib-event` listeners**: `EventListenerHelper.setListener(Consumer<Object>)` — the consumer
  is invoked on the event-dispatch thread.
- **`GraalObjectConverter.toFunction`**: `fromJs` of any executable value returns
  `arg -> toObject(source.execute(arg))` — a lambda around a context-bound `Value` with no
  routing or locking. Every Java API that accepts a callback from JS inherits the bug.
- **`executeMainAsync`**: runs `doExecuteMain` on the per-app single-thread
  `SimpleExecutor` (`ScriptAsyncServiceImpl`) — yet another thread entering the same context,
  correct only because `requireJs` synchronizes.

### 2.4 Websockets and SSE

`ControllerScriptImpl.onSocketEvent` / `onSseEvent` call
`scriptExports.executeMethod("webSocketEvent" | "sseEvent", ...)` on Jetty event threads. Under
GraalJS this is *safe* (the monitor serializes it) but:

- one slow `webSocketEvent` handler blocks every request of the app;
- there is no defined ordering or session-affinity model — messages for many sockets interleave
  through one mutex, and shared module-level state is the only way to keep per-socket data
  ([#8644](https://github.com/enonic/xp/issues/8644));
- any callback captured by the handler and invoked later (timers, event listeners) falls into
  the §2.3 trap.

### 2.5 Representation issues

`GraalObjectConverter` converts JS → Java eagerly (maps, lists, numbers via `as(Number.class)`,
dates via `GraalJSHelper`), and Java → JS through `GraalScriptMapGenerator` /
`ProxyObject.fromMap` (e.g. the `app` global). Known mismatches vs Nashorn: integer vs double
`Number` identity ([#9011](https://github.com/enonic/xp/issues/9011)), `Date`/`Temporal`
round-trips, bean property access (`bean.field` vs setters,
[#9236](https://github.com/enonic/xp/issues/9236)), trailing-comment source wrapping
([#9339](https://github.com/enonic/xp/issues/9339) — caused by the `PRE_SCRIPT`/`POST_SCRIPT`
string concatenation in `GraalScriptExecutor.doExecute`). These are individually fixable, but
they keep resurfacing because conversion is scattered and lazy in places (functions) and eager
in others.

## 3. Constraints imposed by GraalJS (non-negotiable)

Any redesign has to respect these facts:

1. **One thread inside a `Context` at a time.** Multiple threads may use a context only
   sequentially; concurrent entry throws. There is no "make it thread-safe" option for a
   single-threaded language.
2. **`Value` objects are bound to their context.** A JS object/function cannot be migrated or
   shared to another context. Only primitives and **host objects** can cross contexts.
3. **A shared `Engine` shares compiled code**, so N contexts evaluating the same cached
   `Source` pay parse/JIT cost once. Contexts are comparatively cheap; the expensive part is
   re-running module initialization.
4. **Host callbacks re-enter the context on the calling thread.** Whoever invokes a proxied JS
   function must own (or be able to enter) the context at that moment.

Conclusion: the choice is not *whether* to give up Nashorn's free-threaded model, but *which
ownership discipline* replaces it.

## 4. Proposed pipeline: owned contexts, routed calls, eager boundaries

### 4.1 Building block A — per-app context pool ("JS workers")

Replace the single `Context` in `GraalScriptExecutor` with a pool of **workers**:

```
GraalScriptExecutor
 └── JsWorkerPool (per app, size configurable)
      ├── JsWorker #1: Context + dedicated thread + FIFO job queue + ScriptExportsCache
      ├── JsWorker #2: ...
      └── JsWorker #N
```

- All contexts share the app classloader and the global `Engine`; module sources are built with
  `Source.newBuilder(...).cached(true)` so parse/JIT cost is shared.
- **Thread confinement instead of locking**: a worker's context is only ever touched by the
  worker's own thread. Every invocation — `executeMethod`, `require`, function calls — is a job
  submitted to a worker queue. `synchronized (context)` disappears; the queue is the
  serialization point. Confinement also removes a class of liveness hazards (the current design
  can deadlock if JS→Java→JS crosses threads while a monitor is held).
- A plain HTTP request executes on "any idle worker". The calling (Jetty) thread blocks on the
  job future initially — the async-servlet upgrade (§4.5) removes even that.
- The `require` cache becomes **per worker**. This is the central semantic trade-off, see §5.

Sizing: `pool-size = min(cores, configured max)`, lazy growth, idle shrink. Dev mode uses the
same sizing: a slot retained by a live websocket/SSE connection is never shared (§4.4), so a
one-slot dev pool would freeze the whole application behind a single open connection. Dev-mode
reload does not need a single context — each slot's `require` cache expires lazily on that
slot's next execution.

### 4.2 Building block B — function handles instead of raw closures

Introduce `JsFunctionHandle` as the *only* way a JS function crosses the Java boundary:

```java
final class JsFunctionHandle implements Function<Object[], Object> {
    private final JsWorker owner;   // worker whose context created the Value
    private final Value function;
    public Object apply(Object[] args) {
        return owner.submit(() -> convert(function.execute(convertArgs(args)))).join();
    }
}
```

- `GraalObjectConverter.toFunction`, `ScriptValue.call`, and every bean-setter conversion
  (`Function`, `Consumer`, `Runnable`, ... via a custom `HostAccess` target-type mapping)
  produce handles that **route the invocation back to the owning worker's queue**, whatever
  thread the caller is on.
- This mechanically fixes `lib-event` listeners, timers, and every "callback into JS" API —
  they become safe by construction, at the cost of executing on the owner worker (serialized
  with that worker's other jobs, not with the whole app).

### 4.3 `executeFunction` — fails fast on GraalJS

The user-facing contract of `task.executeFunction` is the problem: it promises "run this
closure on another thread", which JS cannot honor — a GraalJS function is bound to the exact
context that created it.

*Decision:* **`task.executeFunction` is deprecated, and on GraalJS it fails immediately at
submit** with an error pointing to named tasks (`task.submitTask`). The check is a one-liner in
the task lib — `typeof Graal !== 'undefined'` (GraalJS installs the `Graal` builtin global;
Nashorn has none) — so no engine-capability API exists at all. Engines without pooling
(Nashorn) keep the historical attached-closure behavior unchanged, but the API only works
there, so it is scheduled for removal together with the Nashorn engine. Apps bundling an older
compiled task lib skip the JS check and land on the routed-handle fallback of §4.2, so nothing
crashes: their closures run on the owning context with `setTimeout`-like semantics
(*asynchronous, but not parallel with that context*); recompiling surfaces the error.

Two alternatives were implemented or considered and rejected:

1. **Detached re-materialization (implemented, then removed)** — the function traveled as
   source plus eagerly converted data params and was re-evaluated in a fresh private context,
   Web Worker style. It worked, but carried a lot of machinery (a runner module, source
   transfer with native-source rejection, a `params` addition to the public API, an `isPooled`
   capability on two public interfaces) to prop up an API whose closure contract still could
   not be honored — captured variables threw. Named tasks already offer the honest version of
   the same thing. Failing fast is also **forward-compatible**: error → working is a
   compatible change, so detached mode can return later if real demand appears, while shipped
   semantics could never be withdrawn.
2. **Stealing a context for the task's duration (considered)** — retain the closure's owning
   context, websocket-style, until the task ends. Rejected: the stolen context can only ever be
   *the submitting one* (the closure is bound to it), so tasks submitted from `main.js` would
   hold the dedicated main context and starve every event listener for the task's whole
   runtime; tasks are long-lived by nature, so each running task would pin a context
   indefinitely against the global budget; and tasks submitted from the same context would
   silently serialize — or deadlock, if one waits on another.

Isolated execution is a **service-level concern only** — not a method on `ScriptExports` and
not a "background" API: the call is synchronous on the calling thread, so the name carries no
threading claim. It serves **named tasks** (`task.submitTask`), the canonical parallel
primitive: the task worker `require`s the named module itself in its own fresh private
context. The API is one direct call — `Object executeMethod(script, method, args...)` — that
resolves and executes in a single shot: one script, one method, a fresh private context per
call, nothing shared between calls, nothing held between them. The result comes back when it
is a **scalar** (string, number, boolean, date — unboxed eagerly, so it survives the private
context's close) and is `null` otherwise, uniformly on every engine: richer values could not
survive the private context as-is and are not (yet) deep-converted. The `Object` return sits
in the signature now, while the API is still unreleased, so conversion can be added later
without a breaking change.
(Three earlier drafts converged here: a returned `ScriptExports` needed a scalars-only rule
plus five methods that ranged from no-op to foot-gun on a context-less view; a per-call method
name misread as an exports object with cross-call state; and a resolve-then-execute-later view
captured the executor incarnation, going stale on redeploy — with resolve-at-run the task
simply executes against the application's current incarnation. The lone deferred user, named
tasks, validates at submit with the pre-existing `hasScript` instead.) A missing script fails
with one exception type on every engine — existence is checkable without a context, ahead of
the engine-specific require machinery; a missing *method* fails loudly — the `null` that
`ScriptExports.executeMethod` would answer with is a legal scalar-contract result here, so it
cannot signal the mistake. A script without the expected export is therefore detected when the task
runs — ending it FAILED with the error in the logs — no longer by an eager submit-time
`hasMethod` probe, which cost a throwaway private context on every named-task submit.

### 4.4 Websockets and SSE — connections keep the exact context of their request

- The handshake/subscribe request executes **bound to one context**
  (`ControllerScript.executeBound`), and the connection's endpoint keeps a view of the
  controller **pinned to that exact context** — not a hash-chosen one. Every subsequent event
  of the connection executes there, seeing precisely the module state the request initialized,
  in arrival order (fair per-slot locks). This gives per-connection FIFO ordering and race-free
  handler state, which is what [#8644](https://github.com/enonic/xp/issues/8644) asks for.
- While a connection references its context (`retain()` on OPEN, `release()` once on the first
  terminal event — CLOSE/ERROR/TIMEOUT), **the context leaves the request pool**: unrelated
  requests never run there, so connection state and event latency are not disturbed, and the
  pool grows replacement slots within capacity and budget instead. Exclusivity beats liveness:
  if every existing slot is retained and nothing can grow, a request fails loudly rather than
  intrude on a connection's context — a retained context carries one connection's module
  state, and GraalJS offers no way to share it safely.
- An earlier design hashed connection keys onto slots (zero bookkeeping, but a *random* stable
  slot that kept serving requests). Rejected: the handshake's module state ended up on a
  different context than the events, and request traffic interleaved with connection handlers.
  The refcount is the bookkeeping cost of doing it right — one integer per slot, no
  per-connection registry.
- Cross-connection coordination (`send to group`, subscriber counts) already lives in Java
  (`WebSocketManagerImpl`) and stays there — broadcast never needs to enter JS, so context
  affinity does not fragment groups.
- Per-connection JS state may live in module-level variables of the connection's context — the
  handshake and all events share one context. State spanning *multiple* connections must still
  be host-backed (§5): different connections may hold different contexts.

### 4.5 Scaling model: contexts ≈ threads (async model rejected)

*Decision:* the synchronous request model is retained — it has served well with Jetty's thread
pool, and an async servlet / promise-controller model (originally sketched here) is **out of
scope**. Instead, the pool scales until contexts are not the bottleneck: the target is a total
context budget in the order of **peak concurrent JS executions**, shared **across all
applications** rather than fixed per app. A blocked request then always owns a thread *and* a
context, exactly like a classic Java servlet application.

Sizing note: HTTP requests are bounded by the Jetty worker pool, but websocket and SSE
connections are async and do not hold worker threads — connection counts can be orders of
magnitude larger than any thread pool. Retained contexts (§4.4) therefore *are* the sizing
pressure to watch: each live connection app-wide keeps one context out of the request pool
(connections of one app naturally share a context when they were served by it — retention
counts references, not connections). The budget sizes for concurrent *executions plus retained
connections' contexts*, and pinned event handlers should stay short — one connection's slow
handler delays the other events bound to the same context.

What this requires of the pool (the "elastic pool" work):

1. **Lazy slot creation with fixed logical capacity.** Slots are addressed by index in
   `[0, capacity)` and created on first use; retention marks a slot as connection-owned and
   the pool grows replacements at free indices.
2. **A global budget, not per-app pools.** Apps grow on demand within a shared cap
   (`xp.script-engine.graal.max-contexts`); at the cap, requests wait for a free slot (today's
   behavior) instead of creating one. Idle-slot reaping can come later — grow-only is an
   acceptable first version since idle contexts cost only memory.
3. **Footprint measurement and metrics.** The viability constraint is per-context memory
   (each slot re-requires the app's module graph) and per-slot warmup. A slot-count gauge and
   a measured per-context RSS for representative apps must precede raising defaults.
4. **Host-backed shared state first.** At large N the §5 trade-off bites hardest through
   `lib-cache`: per-context caches at N≈200 mean cache hit rates collapse. The host-backed
   per-app cache registry (§5 mitigation) moves from "nice to have" to prerequisite.

What is consciously given up by rejecting async: intra-request parallelism (`Promise.all` over
several node queries) and freeing parked servlet threads under overload. Both can be revisited
later without unwinding anything here — the ownership machinery is what a promise bridge would
build on anyway.

#### Workload classes (bulkheads)

One shared pool lets any workload class starve the others; the budgets are partitioned instead:

- **Requests** — the elastic pool above. Nested executions (component rendering) stay on the
  request's slot via the scoped binding.
- **Websocket/SSE events** — a connection *steals the exact slot its request ran on* out of
  the pool (retained while the connection lives), and the pool grows replacements within
  capacity and budget. Connections additionally draw one permit each from a **retained-context
  budget** (`xp.script-engine.graal.max-retained-contexts`, default half of `max-contexts`):
  a connection outlives its request thread, so demand scales with *open connections* (arrival
  rate × lifetime — Little's law), not with the servlet thread count, and without its own cap
  a connection flood would consume the whole pool and starve plain requests. With the cap it is
  the **marginal connection that is rejected at open** — the next request always has headroom.
  A pinned execution has exactly one legal context, so it waits for it **without a time bound,
  interruptibly** (the fair lock preserves event order; the monitor acquisition after it is
  untimed as well): timing out could only break the connection sooner, and parked dispatch
  threads are what the Jetty virtual-threads option compensates.
- **Tasks — ephemeral context per execution.** XP runs tasks on **virtual threads**, and IO-wait
  workloads are a loved use case: task concurrency is effectively unbounded and an IO-waiting
  JS task holds its context for the entire wait. Any *bounded* task partition therefore
  regresses the Nashorn-era behavior (100 parked tasks over 8 slots = 8-way concurrency). So
  named `submitTask` executions get a **fresh context per execution** — create, run, close —
  with the shared engine reusing compiled code, so the per-run cost is module
  re-initialization only: noise for long IO tasks, documented for hot short ones. Concurrency
  then scales with in-flight tasks; memory backpressure is a single `max-isolated-contexts`
  semaphore that virtual threads park on cheaply. This also fixes a defect in the initial
  phase-4 implementation: task runs used to borrow request-serving slots for their full
  duration.
- **Legacy routed `executeFunction`** (old compiled task libs) cannot move to any task pool —
  a closure is physically bound to its submitting context; it stays serialized with its origin
  slot (one more reason `executeFunction` fails fast on GraalJS and the docs steer to
  `submitTask`).

Virtual-thread facts this design relies on: JEP 491 (JDK 24) removed synchronized-monitor
pinning, so the context-monitor discipline and the fair per-slot locks are both VT-safe on
XP's Java 25 baseline. GraalJS context enter/leave on virtual threads is covered by a dedicated
test, since task executions already run on virtual threads. Request handling on virtual threads
exists as an **experimental, default-off** Jetty option (`threadPool.virtualThreads`) — a
future consideration, not the supported mode. When enabled, the `QueuedThreadPool` keeps its
platform threads for the selectors/acceptors and hands blocking request handling to a Jetty
`VirtualThreadPool` — which despite the name does not pool, but **names** the virtual threads
(`xp-jetty-vt`, so they surface in thread dumps and the status reporter — the JDK per-task
executor leaves them anonymous, jetty #11353) and caps concurrency with a `Semaphore`
(`threadPool.virtualThreads.maxConcurrent`, default 1024; 0 = unbounded) so a load spike cannot
spawn unbounded virtual threads and exhaust memory, as the Jetty threading guide warns. The cap
sits well above the platform `maxThreads` (bounding it at the platform-thread count would negate
the point of virtual threads). `QueuedThreadPool` does not manage the executor it is handed, so
the pool is registered as a managed bean and starts/stops with the server. The executor's scope
binding uses `ScopedValue` (not `ThreadLocal`), aligned with the platform-wide ThreadLocal
elimination. (`VirtualThreadPool`'s carrier-thread-starvation bug — jetty #12651, seen on
12.0.15/16 — is a JDK ≤ 23 defect where `Selector.select()` pins its carrier; it is fixed in
JDK 24+ and so does not apply on the Java 25 baseline. It also cannot arise from this wiring
regardless of JDK: the selectors run on the `QueuedThreadPool`'s platform threads, and the
`VirtualThreadPool` only ever runs blocking request handling — never a selector loop.)

#### Engine code cache: what makes many contexts affordable — and its retention rule

The shared engine's code cache is what compensates the memory of many contexts: parsed/compiled
code is shared, so per-context cost is runtime module state only. But the cache is keyed by
`Source` equality and held **weakly** — an entry survives only while an equal `Source` instance
is strongly reachable. Today `doExecute` builds a fresh `Source` per require and discards it:
long-lived slots keep their code alive through their own contexts, but an **ephemeral task
context retains nothing after close** — between task runs the app's cache entries become
collectable, and repeated tasks may silently pay full re-parse/compile, defeating the
"re-init only" cost model. Requirement: a per-app **strong `Source` registry**
(`ResourceKey → Source`, invalidated with the app) used by `doExecute`. It pins the
engine-cache entries for the app's lifetime, makes new-slot and ephemeral-context warmup
parse-free, and costs only the retained source text. **Dev mode bypasses the registry** and
compiles every `Source` fresh: the engine cache is content-keyed, so an unchanged file still
parses once while an edited one misses by construction — a name-keyed strong entry could hand
a stale `Source` to a fresh context (an isolated run, a grown slot) in the window before a
request-path expiry check notices the edit.

### 4.6 Representation clean-up

While rebuilding the boundary, make conversion rules explicit and total in one place:

- one documented mapping table (JS ⇄ Java) for numbers (`int` when integral, `double`
  otherwise — decided once, tested), `Date`/`Instant`/`LocalDateTime`, byte streams, `Map`/
  `ProxyObject`;
- stop wrapping module source with string concatenation (`PRE_SCRIPT + text + POST_SCRIPT`
  broke trailing `//# sourceMap` comments, #9339) — use a real function-constructor style
  wrapper with a trailing newline, or `Source` with proper URI + a bound receiver;
- ~~drop `js.nashorn-compat` entirely; it exists only as a migration crutch
  ([#9071](https://github.com/enonic/xp/issues/9071))~~ — *done on this branch*: the
  `xp.script-engine.nashorn-compat` system property and the experimental-options flag on the
  shared engine are gone. Apps needing Nashorn semantics select the real Nashorn engine via
  `X-Script-Engine` instead of a half-compatible Graal mode.
- *(investigated, not possible)* disabling guest-value sharing (`allowValueSharing(false)`) as
  a fail-fast assertion against cross-slot `Value` leaks: GraalJS forbids it for contexts bound
  to a shared engine (context creation fails), and the shared engine is required for code-cache
  sharing. It also breaks the legitimate `Value.asValue(...)` host-context pattern. Isolation
  therefore relies on the executor's slot discipline (ThreadLocal / `holdsLock` resolution):
  guest objects never cross slots — convert eagerly or share host-backed state.

## 5. Trade-offs we consciously accept ("works differently on GraalJS")

The pool changes one observable behavior: **module-level mutable state is per-worker, not
per-app**. `main.js` and every required module run once *per context*. Impact and mitigations:

| Pattern | Today (Nashorn / Graal-single-context) | With pool | Mitigation |
|---|---|---|---|
| Module-level cache (`lib-cache`) | one instance per app | one per worker | `lib-cache` is a host object (Caffeine) — host objects may be shared across contexts; register instances in a host-side per-app registry keyed by module+name, so all workers see one cache |
| App singleton / counter in a module | global per app | per worker | document; provide `lib-app-state` (host-backed shared map with data-only values) for intentional shared state |
| `main.js` side effects (event listeners, cron via lib-cron) | run once | would run N times | run `main.js` on a **dedicated "main" worker** only; listeners registered there execute there (routed handles, §4.2) |
| WebSocket handler state in module vars | racy but shared | per context; a connection shares one context with its handshake request (§4.4) | works per connection; state spanning connections must be host-backed |
| Dev-mode file-change invalidation | clear one cache | clear N caches | each slot's cache expires lazily on its next execution — no coordinated sweep needed |

These are exactly the "minimal, documented" divergences the platform can afford — they mirror
what every Node.js cluster / worker deployment already imposes on developers.

## 6. What this replaces, per limitation

| Limitation today | Root cause | Fix in this proposal |
|---|---|---|
| App effectively single-threaded | one `Context` + `synchronized` everywhere | worker pool (§4.1) |
| `executeFunction` broken | closure invoked on foreign thread | fails fast on GraalJS, steering to named tasks; routed handle for old compiled libs (§4.3) |
| Callbacks (`lib-event`, converter `toFunction`) crash under load | unsynchronized cross-thread `Value.execute` | `JsFunctionHandle` routing (§4.2) |
| Websocket/SSE ordering & state unclear | global lock + module state | connections keep their request's exact context (§4.4) |
| Servlet threads blocked on JS lock | sync dispatch | async servlet + promise controllers (§4.5) |
| Object representation surprises | scattered lazy/eager conversion | single conversion spec + tests (§4.6) |

## 7. Phasing

0. **Build & CI groundwork** *(done on this branch)* — build on a GraalVM toolchain
   (`JvmVendorSpec.GRAAL_VM`, auto-provisioned via the foojay resolver; CI uses
   `graalvm/setup-graalvm` with `graalvm-community`) so GraalJS runs with runtime compilation
   instead of the slow fallback interpreter, and run every JS-executing test suite **twice**:
   `test` (Nashorn, as before) plus a new `testGraalJs` task wired into `check`
   (see `gradle/js-tests.gradle`; applied to all `lib:*` modules, `script-impl`, `portal-impl`,
   `core-task`, `app-system`, `tools:testing`). GraalJS regressions now fail CI instead of
   surfacing in production; every fix below lands with engine-parity coverage by construction.
1. **Boundary audit & handle type** *(started on this branch)* — introduce `JsFunctionHandle`
   and route *all* JS-function escapes through it. Implemented so far: `HostAccess` target-type
   mappings convert JS functions passed to `Function`/`Consumer`/`Runnable`/`Supplier`/
   `Predicate` parameters into handles (covers `lib-task` `executeFunction` and `lib-event`
   listeners with no lib changes), and `GraalObjectConverter.toFunction` returns handles. This
   alone turns crashes into correct-but-serialized behavior on the current single context.
   Small, independently shippable.
2. **Context pool behind a flag** *(started on this branch)* — `GraalScriptExecutor` now owns N
   `ContextSlot`s (context + value factory + per-slot `require` cache), checked out per
   invocation; `xp.script-engine.graal.pool-size` (initially defaulting to 1; capacity now
   defaults to the global budget — phase 5 — identically in dev mode, since a one-slot pool
   would freeze a dev server behind a single retained websocket/SSE connection). `ScriptExports` became a pool-aware facade so cached controller scripts don't pin
   one slot. The context monitor remains the ownership primitive — slot resolution is
   ThreadLocal → `Thread.holdsLock` scan → checkout, which keeps `require` on a foreign-thread
   callback in the callback's own slot and avoids slot/monitor deadlock cycles. The
   **main-worker rule is landed**: `main.js` loads into a dedicated context outside the pool
   array — the listeners it registers and the disposers it leaves behind are handles bound to
   that same context (unbudgeted, at most one per app), requests never disturb it and it never
   serves requests. Dropped from the original plan: dedicated worker threads (queue instead of
   monitor) — the ownership monitor has proven sufficient.
3. **Connection affinity** *(started on this branch)* — `ScriptExports.executeBound(work)` /
   `ControllerScript.executeBound(work)` run a request bound to one context and hand `work` a
   view pinned to that exact context. Portal handlers execute every controller this way; a
   websocket/SSE endpoint keeps the pinned view, so all events of a connection execute on the
   very context that ran the handshake — its module state included — in arrival order (fair
   per-slot locks). Endpoints `retain()` the context on OPEN and `release()` it once on the
   first terminal event: while retained, the context is excluded from the request pool (the
   pool grows replacements within capacity and budget; if everything is retained and nothing
   can grow, requests fail loudly rather than intrude — exclusivity over liveness). Replaced the earlier
   hash-key affinity (`pinned(affinityKey)`): a random stable slot lost the handshake's module
   state and kept serving unrelated requests. Universal-API endpoints are untouched (their
   handlers are Java-based today — extend when JS-backed handlers arrive). Addresses the
   ordering/state side of [#8644](https://github.com/enonic/xp/issues/8644) at pool sizes
   above 1.
4. **`executeFunction` deprecated, fails fast on GraalJS** *(started on this branch)* — `task.
   executeFunction` is deprecated (removal scheduled with the Nashorn engine) and throws
   immediately at submit on GraalJS (`typeof Graal !== 'undefined'` in
   the task lib — no engine-capability API), pointing to named tasks; Nashorn keeps the
   historical attached-closure behavior unchanged, and apps bundling an older compiled task lib
   fall back to the §4.2 routed handle (setTimeout-like semantics, nothing crashes). An earlier
   detached re-materialization mode (function travels as source plus eager data params, re-run
   by a runner module in a fresh private context) was implemented and then removed in favor of
   failing fast — see §4.3 for the trade-off analysis. **Named tasks** are the parallel
   primitive: submit validates with `hasScript` (context-free), and the run is one direct
   `PortalScriptService.executeMethod(script, method, args)` call in a fresh private
   context, so a task run never checks out a request-serving slot and always executes against
   the application's current incarnation. Still to do: the §5 migration guide for docs.
5. **Elastic pool (contexts ≈ concurrent executions)** *(started on this branch)* — replaces
   the earlier async-servlet phase, see §4.5. Landed: lazy slot creation over a fixed logical
   capacity (retention-aware growth), the global cross-app context budget
   (`xp.script-engine.graal.max-contexts`, default 1024; first slot per app always allowed),
   the retained-context budget for live connections
   (`xp.script-engine.graal.max-retained-contexts`, default half of `max-contexts`; the
   marginal connection is rejected at open instead of the next request failing), isolated
   contexts behind `PortalScriptService.executeMethod` bounded by
   `xp.script-engine.graal.max-isolated-contexts` (default 1024), the strong per-app `Source`
   registry, unbounded interruptible pinned waits, and the experimental Jetty virtual-threads
   option (default off). Remaining: slot-count/footprint metrics and the host-backed
   `lib-cache` registry (per-context caches fragment at scale).
6. **Flip the default** — `xp.script-engine=GraalJS` with pool enabled; Nashorn path stays for
   one release cycle via `X-Script-Engine` per app, then removed.

## 8. Open questions

- ~~Pool sizing policy: per app, global cap, or weighted by app traffic?~~ Decided (§4.5): a
  global cross-app budget grown lazily on demand, sized to concurrent open connections plus
  in-flight requests — not to the servlet thread count, which bounds neither connections nor
  isolated runs — with a separate cap for connection-retained contexts.
- ES modules (`import`) support could ride on the new pipeline (`Source.mimeType
  ("application/javascript+module")`) — worth deciding before freezing the wrapper design.
- ~~Should the "main" worker also serve requests, or stay reserved for listeners/timers?~~
  Decided (§9.1): the bootstrap runs on a **dedicated main context** that request traffic never
  touches (`bootstrap` pins to it; `executeMain` uses the pool), so `main.js` state and its
  listeners/disposers stay isolated from requests.
- `Value`-leak detection: debug mode that records stack traces when a context-bound `Value`
  escapes without a handle, to catch library regressions early.
- Interaction with `ApplicationInvalidationLevel` / dev-mode reload: invalidation must fan out
  to all workers atomically (quiesce queue, swap contexts) to avoid mixed-version modules.

## 9. Related issues

### 9.1 Adjacent engine issues and how this design meets them

**[#7821 main.js must execute before any other scripts](https://github.com/enonic/xp/issues/7821)**
— a bootstrap-ordering problem, not an engine problem, but the pipeline changes its tractability
twice over:

- *Partially defused structurally.* The scariest observed symptom — an event firing on a
  half-initialized module ("`TypeError: Cannot read property "Symbol(Symbol.iterator)"`" from
  half-loaded polyfills) — is a cross-thread visibility race on the shared Nashorn global. On
  the pooled pipeline it cannot happen: modules load inside their slot's lock+monitor
  (single-threaded per context, run-to-completion), and each slot's `require` cache is confined
  to that slot, so no thread can ever observe another thread's partially-initialized exports.
  `main.js` additionally gets a **dedicated context** (the main-worker rule, §4.1/phase 2):
  bootstrap, its listeners and its disposers share one context that requests never touch.
  Deadlock audit note: event dispatch is single-threaded today, so `main.js` structurally
  cannot wait on its own listeners — gating listeners would also be safe; they are left
  ungated because the dedicated context already serializes them with bootstrap (a listener
  handle waits on the main context's monitor until `main.js`'s evaluation completes).
- *The ordering half is fixed on this branch*, and the gate lives **on the per-application
  executor** rather than in the OSGi service registry. The script runtime already owns one
  `ScriptExecutor` per application, created lazily on first use and discarded on `invalidate`
  (the #10844 teardown) — i.e. a fresh instance per application *incarnation*. Each executor holds
  a **gate that starts closed**; every top-level execution (`ScriptRuntime.execute`) waits on it,
  and it is opened only by `bootstrap`. Because the gate is a field of the per-incarnation executor,
  "which incarnation bootstrapped?" is never a question a string has to answer: a caller always
  waits on the exact executor it is about to run, and two installs of the same key can neither share
  nor race a gate (the flaw of a registry marker keyed on the app-key string, where a stopping
  incarnation's marker can satisfy a starting one's wait). **Calling `bootstrap` is mandatory;
  running a script is optional** — `bootstrap(BootstrapParams)` takes an application key plus an
  *optional* `mainScript`: it runs that script once on the dedicated main context if present, and
  opens the gate either way. `MainExecutor` calls `PortalScriptService.bootstrap` with `app:/main.js`
  for every tracked app; the test harness calls it with no script (just opens the gate). So only
  `MainExecutor` (the portal layer) ever names the `/main.js` convention — the generic runtime does
  not. The wait is **centralized in the runtime**, so controllers, filters, error handlers, macros,
  response processors and named tasks are gated by construction — a new entry point inherits it.
  Exempt is any execution re-entrant within the same application's bootstrap: the entrypoint runs
  inside a thread-scoped `ScopedValue` carrying the bootstrapping app's key, so it and anything it
  triggers synchronously do not wait for the gate they are themselves about to open (which would
  self-deadlock); a *different* app's script invoked mid-bootstrap is still gated. `MainExecutor` is
  a `ServiceTracker<Application>` (not an `ApplicationListener`), DS-gated on the deploy-ready
  Condition (`osgi.condition.id=com.enonic.xp.server.deploy.ready`); its `open()` replays every
  already-registered `Application` the moment the gate is satisfied and delivers future ones via
  `addingService`, so every active app — side-effect-only ones included — has `bootstrap` called at
  deploy, regardless of boot order, system apps included. Because the gate starts closed and
  `execute` always waits, a request that reaches an app before its `bootstrap` call still blocks
  until `main.js` completes (no cold-activation window). A broken `main.js` still opens the gate
  (surfaces in the log, never a permanently dammed app); event callbacks and tasks on their own
  threads are gated normally (async, no deadlock); the wait is bounded (300 s, then fail-open with a
  warning, **latched** so the timeout is paid once — not by every subsequent caller) so a hanging or
  never-armed bootstrap degrades to proceeding rather than a deadlock. An earlier revision also
  remembered each application's last `BootstrapParams` and re-armed lazily recreated executors
  from them — compensation for the factory's `ApplicationInvalidator` round discarding the freshly
  bootstrapped successor after a reconfigure. That machinery is gone with its cause: the factory
  no longer implements `ApplicationInvalidator` at all (see the #10844 lifecycle note below), so
  no trailing invalidate ever discards a healthy successor.

**[#10844 Disposers race condition](https://github.com/enonic/xp/issues/10844)** — disposers
registered by one bundle incarnation invoked for its replacement (rooted in #7966). The
pipeline sharpens both the fix shape and the stakes:

- Everything app-scoped now lives in one closeable executor instance: slots, the strong
  `Source` registry, the disposer queues, the budgeted-slot count. *Fixed on this branch as
  instance-owned teardown*: app stop performs a full `invalidate` — atomically remove
  the executor instance, run **its** disposers against **its** still-open contexts, close them
  and return **its** budget permits. The name-keyed `runDisposers(key)` lookup (which under
  replacement resolves to the successor incarnation — the #10844 confusion) is gone; runtime
  disposal tears down all owned executors the same way. **The service tracker is the only stop
  signal.** `ScriptRuntimeFactoryImpl` is a `ServiceTracker<Application>` (like `MainExecutor`)
  and deliberately does *not* implement `ApplicationInvalidator`: every registry flow
  (reconfigure, stop, uninstall) calls `unregister()` **before** its invalidator round, and the
  tracker's `removedService` is delivered synchronously inside `unregister()` — so the tracker
  fires at the right moment (before a reconfigure's replacement registers), while the invalidator
  fires at the wrong one (after it, killing the freshly bootstrapped successor: the very race a
  pile of re-arm compensation used to paper over). The factory also tracks each application's
  **current incarnation** — the `ServiceReference` of its live registration (reconfigure
  re-registers the *same* `Application` object, so the registration is the identity, not the
  service object) — and executor creation resolves through this map instead of scanning the
  service registry. Executors are **stamped with the incarnation they were built from and
  revalidated on every use**: an executor whose registration is gone (stopped, or replaced by a
  reconfigure that raced its creation) dies on its next touch — full instance teardown, then a
  rebuild from the current incarnation — closing the check-then-act window that a plain
  app-key-keyed map leaves open. This resolves the #7966 event-ordering exposure for script
  runtimes: a late or misordered signal can no longer kill a healthy successor, and a stale
  executor cannot outlive its bundle.
- Teardown is **non-negotiable and race-hardened**: contexts close with *cancel* (a context still
  executing an in-flight request or connection dispatch must not veto app stop — its execution
  fails on its own thread, and a stale pinned websocket dispatch failing makes the container close
  the connection so clients reconnect), the shared budget permits are returned in a `finally`
  (a leaked permit would shrink the global pool for every application, forever), and a `closed`
  flag stops a bootstrap or task racing app stop from lazily resurrecting contexts no teardown
  path could ever reach. **Connections are torn down with their application**: endpoints carry
  the application they serve, and the SSE/websocket managers track `Application` services — on
  stop or redeploy every connection of that application is closed by the server (websockets
  with `GOING_AWAY`), so clients reconnect to the successor incarnation instead of lingering
  silently on a context of the gone one (nothing inbound would ever dispatch; the default SSE
  timeout is infinite).
- On pooled engines a disposer only outlives its registration meaningfully on the **main
  context**: bootstrap's disposer is stable and torn down with the app, whereas a pool slot's is
  per-context (one per slot the module loads into) and a task's ephemeral context is already
  closed by the time teardown runs. So `__.disposer` is honored only when called during
  `bootstrap` (`main.js`); a registration from a request or task slot is logged as a warning and
  ignored rather than silently dropped at teardown. (Nashorn keeps its single-context semantics
  unchanged.) Dev-mode cache expiry does **not** drain the disposer queues: only bootstrap can
  register, and a reload re-executes controllers, never `main.js` — draining would run the app's
  teardown mid-life and leave nothing for the actual stop.
- The urgency note stands for #7966 itself: executor teardown is only as reliable as the app
  lifecycle events that trigger it.

**[#6775 Global namespace](https://github.com/enonic/xp/issues/6775)** — align XP's global
namespace with browser/node. The pipeline moves in this issue's direction and GraalJS itself
closes part of it:

- The globals policy is now explicit: `app` is the only production global; custom injected
  globals (`ScriptSettings.globalVariable`) are deprecated (kept solely for the `xp-testing`
  harness until it migrates). Moving `app` into a lib, as the issue proposes, stays viable on
  this pipeline.
- GraalJS natively provides `globalThis` (ES2020) and a `console` built-in — the two probes
  webpack'd node modules trip over on Nashorn ES6 — so node-module compatibility improves with
  the engine flip without adding XP globals.
- One pool-specific rule for any future global (Buffer/`setTimeout` shims, console bridges):
  the global scope is **per context** on pooled engines, so a global must be immutable,
  host-backed-shared, or idempotently initializable per context. Mutable singleton globals are
  the one shape the pool cannot honor — which is the same conclusion #6775 reaches from the
  compatibility side.

### 9.2 Issue list

- [#8714 GraalJS (epic)](https://github.com/enonic/xp/issues/8714)
- [#8644 Graal Websocket data thread safety](https://github.com/enonic/xp/issues/8644) — open
- [#9059 Multi threading issue after migration on Graal JS](https://github.com/enonic/xp/issues/9059)
- [#8592 Context is not thread safe](https://github.com/enonic/xp/issues/8592)
- [#10873 WebSocket: context is lost in message events](https://github.com/enonic/xp/issues/10873)
- [#9011 Numbers in GraalJS](https://github.com/enonic/xp/issues/9011)
- [#9339 Sourcemap comment in graal](https://github.com/enonic/xp/issues/9339)
- [#9071 Do not use js.nashorn-compat](https://github.com/enonic/xp/issues/9071)
- [#8916 GraalVM JS support for portal module](https://github.com/enonic/xp/issues/8916)
- [#8053 Server-Sent Events support](https://github.com/enonic/xp/issues/8053)
- [#7821 main.js must execute before any other scripts](https://github.com/enonic/xp/issues/7821) — §9.1
- [#10844 Disposers race condition](https://github.com/enonic/xp/issues/10844) — §9.1
- [#6775 Global namespace](https://github.com/enonic/xp/issues/6775) — §9.1
