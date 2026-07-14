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
 └── JsWorkerPool (per app, size configurable, dev mode: 1)
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

Sizing: `pool-size = min(cores, configured max)`, lazy growth, idle shrink. Dev mode pins the
pool to 1 worker to keep today's debugging and cache-invalidation behavior.

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

### 4.3 `executeFunction` — always detached on pooled engines

The user-facing contract of `task.executeFunction` is the problem: it promises "run this
closure on another thread", which JS cannot honor.

*Decision:* **the engine decides — there is no user-facing flag.** On pooled engines tasks are
always detached: JS developers have long been familiar with worker patterns, and a routed
closure would only *appear* to work while silently serializing the task with the submitting
context. Engines without pooling (Nashorn) always keep the historical attached-closure
behavior. The engine is probed at submit via `ScriptExports.isolated()` (pooled engines return
a distinct view); apps bundling an older compiled task lib (no source captured) keep the
routed-handle fallback of §4.2, so nothing crashes. `params` are delivered on every path.

A detached function must be able to talk to the world: `log`, `require`, `resolve` and `__`
are module-wrapper *parameters* in this codebase, not globals, so a bare re-materialized
function would see only `params`, `app` and engine built-ins. The runner therefore evaluates
the source inside a wrapper — `(function (log, require, resolve, __) { return (<source>); })` —
and applies its own module environment, so detached functions can load libraries and log.
`require` resolves relative to the runner's location (`/lib/xp/`): absolute paths are the
documented convention. (An earlier `detached: true` opt-in flag existed briefly and was
removed: engine-dependent defaults plus a flag made three behavior combinations to document
and test, for no real use case.)

1. **Routed fallback — "run later on owner"**: the handle from §4.2 keeps closure-based calls
   from old compiled libs correct: the task thread submits the closure invocation to the owning
   worker. Semantics: *asynchronous, but not parallel with that worker* — exactly how
   `setTimeout` behaves in a browser.
2. **Detached mode — eager params, no closures (the pooled-engine default)**:
   `task.executeFunction({ func, params })` where:
   - `params` are **eagerly converted to plain data at submit time** (JSON-like deep copy via
     `GraalObjectConverter`, rejecting functions/host references);
   - `func` is re-materialized in the target pooled context from its source
     (`Function.prototype.toString` / `Value.getSourceLocation()`), evaluated with `params` as
     the only input; **captured outer variables are not available** and referencing them fails
     fast with a clear error.
   This is Web-Worker semantics, matching the suggestion "parameters provided eagerly, external
   closures ignored". It should fail loudly, not silently, when a closure variable is touched:
   evaluate the function source in a scope whose `with`-like proxy throws
   `ReferenceError: <name> is not transferable to a detached task` for anything but `params`
   and globals.
3. Keep steering documentation toward `task.submitTask` (named module + serializable config)
   as the canonical parallel primitive — it already has the right shape: the task worker
   `require`s the module itself in its own context.

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
  pool grows replacement slots within capacity and budget instead. Liveness beats exclusivity
  in one corner: if every existing slot is retained and nothing can grow, requests share a
  retained slot rather than starve.
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
- **Websocket/SSE events** — no separate partition: a connection *steals the exact slot its
  request ran on* out of the pool (retained while the connection lives), and the pool grows
  replacements within capacity and budget. Because event-dispatch threads are shared, pinned
  executions bound their slot wait (30 s instead of the 5-minute request wait) so a saturated
  connection slot fails events fast instead of holding shared threads.
- **Tasks — ephemeral context per execution.** XP runs tasks on **virtual threads**, and IO-wait
  workloads are a loved use case: task concurrency is effectively unbounded and an IO-waiting
  JS task holds its context for the entire wait. Any *bounded* task partition therefore
  regresses the Nashorn-era behavior (100 parked tasks over 8 slots = 8-way concurrency). So
  detached tasks (and named `submitTask` executions) get a **fresh context per execution** —
  create, run, close — with the shared engine reusing compiled code, so the per-run cost is
  module re-initialization only: noise for long IO tasks, documented for hot short ones (which
  can stay on the routed legacy path). Concurrency then scales with in-flight tasks; memory
  backpressure is a single `max-task-contexts` semaphore that virtual threads park on cheaply.
  This also fixes a defect in the initial phase-4 implementation: detached tasks currently
  borrow request-serving slots for their full duration.
- **Legacy routed `executeFunction`** cannot move to any task pool — a closure is physically
  bound to its submitting context; it stays serialized with its origin slot (one more reason
  the docs steer to detached/`submitTask`).

Virtual-thread facts this design relies on: JEP 491 (JDK 24) removed synchronized-monitor
pinning, so the context-monitor discipline and the fair per-slot locks are both VT-safe on
XP's Java 25 baseline. GraalJS context enter/leave on virtual threads is covered by a dedicated
test, since task executions already run on virtual threads. Request handling on virtual threads
exists as an **experimental, default-off** Jetty option (`threadPool.virtualThreads`) — a
future consideration, not the supported mode. The executor's scope binding uses `ScopedValue`
(not `ThreadLocal`), aligned with the platform-wide ThreadLocal elimination.

#### Engine code cache: what makes many contexts affordable — and its retention rule

The shared engine's code cache is what compensates the memory of many contexts: parsed/compiled
code is shared, so per-context cost is runtime module state only. But the cache is keyed by
`Source` equality and held **weakly** — an entry survives only while an equal `Source` instance
is strongly reachable. Today `doExecute` builds a fresh `Source` per require and discards it:
long-lived slots keep their code alive through their own contexts, but an **ephemeral task
context retains nothing after close** — between task runs the app's cache entries become
collectable, and repeated tasks may silently pay full re-parse/compile, defeating the
"re-init only" cost model. Requirement: a per-app **strong `Source` registry**
(`ResourceKey → Source`, invalidated with the app and on dev-mode reload) used by `doExecute`.
It pins the engine-cache entries for the app's lifetime, makes new-slot and ephemeral-context
warmup parse-free, and costs only the retained source text.

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
| Dev-mode file-change invalidation | clear one cache | clear N caches | pool size 1 in dev mode |

These are exactly the "minimal, documented" divergences the platform can afford — they mirror
what every Node.js cluster / worker deployment already imposes on developers.

## 6. What this replaces, per limitation

| Limitation today | Root cause | Fix in this proposal |
|---|---|---|
| App effectively single-threaded | one `Context` + `synchronized` everywhere | worker pool (§4.1) |
| `executeFunction` broken | closure invoked on foreign thread | detached eager-param mode on pooled engines, routed handle elsewhere (§4.3) |
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
   invocation; `xp.script-engine.graal.pool-size` (default 1 = today's behavior; dev mode
   forces 1). `ScriptExports` became a pool-aware facade so cached controller scripts don't pin
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
   can grow, requests share a retained slot — liveness over exclusivity). Replaced the earlier
   hash-key affinity (`pinned(affinityKey)`): a random stable slot lost the handshake's module
   state and kept serving unrelated requests. Universal-API endpoints are untouched (their
   handlers are Java-based today — extend when JS-backed handlers arrive). Addresses the
   ordering/state side of [#8644](https://github.com/enonic/xp/issues/8644) at pool sizes
   above 1.
4. **Detached `executeFunction`** *(started on this branch)* — the engine decides, no
   user-facing flag: on pooled engines `task.executeFunction({ params, func })` always runs
   detached — the function travels as source (captured via `Function.prototype.toString` at
   submit) plus eagerly converted data params (functions rejected at submit), and is
   re-materialized by an internal runner module (`/lib/xp/detached-task.js`, executed through
   `PortalScriptService` → `ScriptExports.isolated()`) in a fresh context — true parallelism
   on GraalJS. The runner applies its module environment (`log`, `require`, `resolve`, `__`)
   to the re-materialized function, so detached functions can load libraries and log; captured
   outer variables throw `ReferenceError`, matching Web-Worker expectations. Nashorn always
   keeps the historical attached-closure behavior. Still to do: the §5 migration guide for
   docs.
5. **Elastic pool (contexts ≈ concurrent executions)** *(started on this branch)* — replaces
   the earlier async-servlet phase, see §4.5. Landed: lazy slot creation over a fixed logical
   capacity (retention-aware growth), the global cross-app context budget
   (`xp.script-engine.graal.max-contexts`, default 200; first slot per app always allowed),
   ephemeral task contexts behind `ScriptExports.isolated()` bounded by
   `xp.script-engine.graal.max-task-contexts`, the strong per-app `Source` registry, bounded
   pinned waits, and the experimental Jetty virtual-threads option (default off). Remaining:
   slot-count/footprint metrics and the host-backed `lib-cache` registry (per-context caches
   fragment at scale).
6. **Flip the default** — `xp.script-engine=GraalJS` with pool enabled; Nashorn path stays for
   one release cycle via `X-Script-Engine` per app, then removed.

## 8. Open questions

- ~~Pool sizing policy: per app, global cap, or weighted by app traffic?~~ Decided (§4.5): a
  global cross-app budget in the order of the Jetty thread pool, grown lazily on demand.
- ES modules (`import`) support could ride on the new pipeline (`Source.mimeType
  ("application/javascript+module")`) — worth deciding before freezing the wrapper design.
- Should the "main" worker also serve requests, or stay reserved for listeners/timers?
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
- *The ordering half is fixed on this branch* — at the portal choke point rather than inside
  the engines: `MainExecutor` arms a per-app bootstrap gate before starting `main.js` and opens
  it when the execution completes (success, failure or app deactivation — a broken `main.js`
  surfaces in the log, never as a permanently dammed app), and
  `ControllerScriptFactory.fromScript` awaits the gate (`BootstrapState`), so every controller
  — webapp, service, mapping, API, error, id-provider — observes a fully bootstrapped
  application on both engines. Tasks and event callbacks are deliberately *not* gated: worker
  patterns spawned by `main.js` itself must run (a gated task that `main.js` polls would
  deadlock). The wait is bounded (300 s, then fail-open with a warning) so a hanging
  `main.js` degrades to today's behavior instead of a deadlock. Remaining window: requests in
  the instant between bundle activation and the `ApplicationListener` round slip through
  ungated — strictly better than today, not perfect.

**[#10844 Disposers race condition](https://github.com/enonic/xp/issues/10844)** — disposers
registered by one bundle incarnation invoked for its replacement (rooted in #7966). The
pipeline sharpens both the fix shape and the stakes:

- Everything app-scoped now lives in one closeable executor instance: slots, the strong
  `Source` registry, the disposer queues, the budgeted-slot count. *Fixed on this branch as
  instance-owned teardown*: app deactivation performs a full `invalidate` — atomically remove
  the executor instance, run **its** disposers against **its** still-open contexts, close them
  and return **its** budget permits. The name-keyed `runDisposers(key)` lookup (which under
  replacement resolves to the successor incarnation — the #10844 confusion) is gone; runtime
  disposal tears down all owned executors the same way. The remaining exposure is the #7966
  event-ordering root: a late `deactivated` event can still tear down a *healthy successor*
  executor — but that now self-heals (it is lazily recreated on next use) instead of running
  the wrong incarnation's disposers or leaking budget.
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
