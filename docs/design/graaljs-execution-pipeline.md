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

### 4.3 `executeFunction` — two modes

The user-facing contract of `task.executeFunction` is the problem: it promises "run this
closure on another thread", which JS cannot honor. Proposal:

1. **Default (safe) mode — "run later on owner"**: the handle from §4.2 makes today's API work
   correctly: the task thread submits the closure invocation to the owning worker. Semantics:
   *asynchronous, but not parallel with that worker* — exactly how `setTimeout` behaves in a
   browser. No API change, no crash, mild documentation note.
2. **Detached mode — eager params, no closures (opt-in)**: for real parallelism, add
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

### 4.4 Websockets and SSE — per-connection worker affinity

- On socket open (`webSocketEvent` with `type: open`) / SSE stream start, **bind the connection
  to one worker** (round-robin or least-loaded). All subsequent events for that connection are
  jobs on that worker's queue — giving per-connection FIFO ordering and race-free handler
  state, which is precisely what [#8644](https://github.com/enonic/xp/issues/8644) asks for.
- Cross-connection coordination (`send to group`, subscriber counts) already lives in Java
  (`WebSocketManagerImpl`) and stays there — broadcast never needs to enter JS, so worker
  affinity does not fragment groups.
- Per-connection JS state should be attached to the event object (`event.session.attributes`
  style, host-backed map) rather than module-level variables; module state is per-worker after
  this change (§5).

### 4.5 Scaling model: contexts ≈ threads (async model rejected)

*Decision:* the synchronous request model is retained — it has served well with Jetty's thread
pool, and an async servlet / promise-controller model (originally sketched here) is **out of
scope**. Instead, the pool scales until contexts are not the bottleneck: the target is a total
context budget in the order of **peak concurrent JS executions**, shared **across all
applications** rather than fixed per app. A blocked request then always owns a thread *and* a
context, exactly like a classic Java servlet application.

Sizing note: HTTP requests are bounded by the Jetty worker pool, but websocket and SSE
connections are async and do not hold worker threads — connection counts can be orders of
magnitude larger than any thread pool. That is exactly why affinity *hashes many connections
onto few slots* (§ phase 3) instead of dedicating a context per connection: 10k idle sockets
cost nothing, and only their momentarily-executing event handlers occupy slots. Two
consequences: the budget sizes for concurrent *executions* (workers + event-dispatch
concurrency), not connections; and pinned event handlers should stay short — a hot slot
serializes all connections hashed to it, and event-dispatch threads block while waiting on it.

What this requires of the pool (the "elastic pool" work):

1. **Lazy slot creation with fixed logical capacity.** Slots are addressed by index in
   `[0, capacity)` and created on first use. Affinity hashing maps keys onto the *capacity*,
   not the live slot count, so a pool growing under load never remaps existing connections.
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
  request's slot via the ThreadLocal binding.
- **Websocket/SSE events** — a small hash-shared partition (connections vastly outnumber
  contexts by design). Caveat recorded from review: partitioning *contexts* does not partition
  the *threads that wait for them* — event-dispatch threads are shared, so event executions
  must bound their slot wait (short timeout + error event) or move to per-connection queues
  drained by a dedicated dispatcher; otherwise a saturated ws partition still blocks shared
  threads and the bulkhead leaks.
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
XP's Java 25 baseline. **Validation item:** confirm GraalJS context enter/leave on virtual
threads with the shipped polyglot version (a dedicated test), since every task-side execution
does exactly that.

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
| WebSocket handler state in module vars | racy but shared | per worker; per-connection thanks to affinity | prefer connection-scoped state; document |
| Dev-mode file-change invalidation | clear one cache | clear N caches | pool size 1 in dev mode |

These are exactly the "minimal, documented" divergences the platform can afford — they mirror
what every Node.js cluster / worker deployment already imposes on developers.

## 6. What this replaces, per limitation

| Limitation today | Root cause | Fix in this proposal |
|---|---|---|
| App effectively single-threaded | one `Context` + `synchronized` everywhere | worker pool (§4.1) |
| `executeFunction` broken | closure invoked on foreign thread | routed handle by default; detached eager-param mode opt-in (§4.3) |
| Callbacks (`lib-event`, converter `toFunction`) crash under load | unsynchronized cross-thread `Value.execute` | `JsFunctionHandle` routing (§4.2) |
| Websocket/SSE ordering & state unclear | global lock + module state | per-connection worker affinity (§4.4) |
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
   callback in the callback's own slot and avoids slot/monitor deadlock cycles. Still to do
   from the original plan: dedicated worker threads (queue instead of monitor) and a
   main-worker rule for `main.js` listeners at pool sizes above 1.
3. **Connection affinity** *(started on this branch)* — `ScriptExports.pinned(affinityKey)` /
   `ControllerScript.pinned(affinityKey)` give hash-stable slot affinity with no per-connection
   bookkeeping (equal keys always resolve to the same slot). Portal websocket and SSE endpoints
   pin every event by session id / SSE client id, so one connection's handlers execute in one
   context, in arrival order (fair per-slot locks). Scripts stay freshly resolved per event, so
   dev-mode reload semantics are unchanged. Universal-API endpoints are untouched (their
   handlers are Java-based today — extend when JS-backed handlers arrive). Addresses the
   ordering/state side of [#8644](https://github.com/enonic/xp/issues/8644) at pool sizes
   above 1.
4. **Detached `executeFunction`** *(started on this branch)* —
   `task.executeFunction({ detached: true, params, func })`: the function travels as source
   (captured via `Function.prototype.toString` at submit) plus eagerly converted data params
   (functions rejected at submit), and is re-materialized by an internal runner module
   (`/lib/xp/detached-task.js`, executed through `PortalScriptService` → the pooled exports
   facade) in whatever slot serves the task thread — true parallelism on GraalJS, identical
   semantics on Nashorn. Captured outer variables throw `ReferenceError` (strict-mode global
   eval), matching Web-Worker expectations. Still to do: the §5 migration guide for docs.
5. **Elastic pool (contexts ≈ threads)** — replaces the earlier async-servlet phase, see §4.5:
   lazy slot creation over a fixed logical capacity, a global cross-app context budget, slot
   metrics + footprint measurement, and the host-backed `lib-cache` registry as prerequisite.
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
