# XP Script Security — Trust Tiers and the Capability Model

**Status:** Draft v0.1

**Date:** 2026-08-03

**Companion documents:** [`CLOUD-ARCHITECTURE.md`](CLOUD-ARCHITECTURE.md),
[`SCRIPT-ESM.md`](SCRIPT-ESM.md), [`SCRIPT-DEBUGGING.md`](SCRIPT-DEBUGGING.md),
`docs/design/graaljs-execution-pipeline.md` (on the GraalJS pipeline branch)

---

## 1. Purpose

XP applications today run with flat, total trust: every app executes inside the
platform JVM with the same authority as the platform itself. Any app can read every
configuration loaded into XP, reach any class via `__.newBean`, and act with ambient
platform authority. This was acceptable when apps were first-party or closely reviewed;
it does not scale to a marketplace ecosystem, a multi-product platform, or a cloud
offering.

This document defines the target trust model for application code: three explicit
trust tiers, a capability-based bridge between scripts and the platform, and the
migration path from today's open bridge. It resolves the immediate practical problem
(cross-app config exposure) and establishes the foundation for sandboxed marketplace
apps.

The scope is application code trust. Tenant isolation is out of scope here: it is
enforced below the runtime by NoDB (`nodb/DESIGN.md` §7.2) and holds regardless of
anything in this document. A total sandbox escape is contained to the tenant's own
data. The script sandbox is defense in depth for intra-tenant app separation and
platform integrity — not the primary wall.

## 2. Architectural decisions

1. **Inside one JVM there is no security boundary against Java code.** The Java
   SecurityManager is removed (JEP 411); OSGi provides modularity, not isolation;
   reflection and direct filesystem access cannot be denied to bytecode in-process.
   The only hard boundary the JVM offers is the process itself. No design may claim
   otherwise.
2. **Trust is classified by confinability, not language.** The tier question is:
   *does installing this app introduce new executable code into the platform's trust
   domain, and can the platform confine it?*
3. **Three trust tiers**, each with a different control:

   | Tier | Code | Control | Bridge |
   |---|---|---|---|
   | **trusted** | Java apps; Nashorn apps; GraalJS apps using the open bridge | review and signing | open (scoped `__.newBean`) |
   | **restricted** | GraalJS apps in a closed context | construction — only granted capabilities exist | none; injected handles only |
   | **remote** | any language, out of process | process/OS boundary + token scopes | APIs, events, webhooks |

4. **Nashorn apps are trusted-tier by definition.** Nashorn is unconfinable
   (free-threaded, open bridge, no context primitive) and its upstream is in
   maintenance stasis. Nashorn is frozen "as is" — no new APIs, no restricted-tier
   eligibility, no hardening spend — deprecated, and removed at a later major.
   Sharp edges included: a Nashorn app can still read neighbor config files; its tier
   makes no confinement claim. See §8.
5. **Scripts gain authority only through granted capabilities.** An app manifest
   declares what it needs (config, repos, APIs, egress); the platform resolves the
   declarations to pre-scoped handle objects injected at context construction.
   Capabilities are object-capability style: a handle is born bound to its scope and
   cannot be widened by the script.
6. **Only data crosses the script boundary.** Handles accept and return primitives
   and JSON-shaped values, never live platform objects. JS functions cross into Java
   only as routed function handles (`JsFunctionHandle`, already landed on the GraalJS
   pipeline branch). Java invoking a JS function is the safe direction — the guest
   gains no authority by being called; guest code naming host classes is the dangerous
   direction and is what this design removes.
7. **`__.newBean` is demoted and scoped, not dropped.** Stage 1 (with the Graal
   release): resolution moves from open classloader lookup to a closed registry —
   an app resolves only beans exported by bundles it declares, plus its own bundle.
   The restricted tier has no `newBean` at all. Full removal happens at a later major
   once marketplace scanning shows the tail is gone.
8. **App-facing lib surfaces are frozen.** `require('/lib/xp/content')` behaves
   identically throughout the migration. Libs are rewired internally from bean
   instantiation to injected capability handles; app code never changes.
9. **Secrets leave configuration.** Config files carry references
   (`${secret:name}`); the platform resolves them at injection time from a secret
   provider and delivers each secret only to the app whose manifest names it.
   `app.config` is the only config API and is injected per app, scoped to the app's
   own PID.
10. **The lib concept bifurcates.** *Guest-side libs* are pure JS, bundled with the
    app, running inside its context — they change nothing about trust. *Host-side
    capability providers* carry Java, install as platform-level (trusted-tier)
    extensions, and register capabilities that restricted apps may declare. A lib
    that ships Java is app Java; depending on it makes an app trusted-tier.
11. **Calling platform libs does not make a script app a Java app.** The lib's Java
    is platform code — pre-installed, audited, versioned, loaded regardless of the
    app. Installing a script app adds zero bytecode to the process; it invokes a
    fixed, enumerable menu of audited operations with data arguments — the syscall
    model. Review of a restricted app is manifest + scripts; review of a Java app is
    adoption of its bytecode into the trust base.
12. **Isolation strength is a ladder, chosen per tier, behind one contract:**

    | Rung | Mechanism | Gives | Caveats |
    |---|---|---|---|
    | 1 | restricted `Context` (default-deny host access, no host class lookup) | full API-level confinement | shared heap; engine bugs; allocation bombs |
    | 2 | `SandboxPolicy.CONSTRAINED` | rung 1 with misconfiguration welded shut | same residuals |
    | 3 | polyglot isolates (`ISOLATED`/`UNTRUSTED`) | own heap, CPU/memory caps, engine-bug containment | Oracle GraalVM only — licensing decision |
    | 4 | separate OS process | survives JVM compromise; OS controls | latency, ops cost |

    Default: rung 1/2 in-process everywhere (its realistic threat is undisciplined
    apps and compromised dependencies). Rungs 3/4 are a cloud-side hardening option
    per plan/tier. Because the contract (capabilities in, data out) is identical at
    every rung, the rung is a deployment decision, not an API decision.
13. **Per-product deployments shrink trust domains for free.** In the multi-product
    direction (each product its own XP deployment), a CMS app's JVM simply does not
    contain the commerce product's credentials. Blast radius by decomposition,
    independent of sandbox technology.

## 3. Non-goals

- Claiming in-JVM sandboxing of Java or Nashorn code. The trusted tier is controlled
  by review; pretending otherwise is the trap this design avoids.
- Making OSGi a security boundary.
- Per-extension processes as the default (the remote tier exists for code that needs
  it; co-located execution remains a deployment optimization).
- Reproducing Node.js ambient-authority APIs (`fs`, `process.env`) inside contexts —
  see `SCRIPT-ESM.md`.
- Intra-tenant sandboxing as a substitute for the NoDB tenant boundary.

## 4. The open bridge and its alternatives

The open bridge (`__.newBean(anyClass)` + `HostAccess.ALL` + open host class lookup)
answers three design questions in the least safe way: the app names any
implementation, live object references cross, enforcement exists nowhere. The
alternatives tighten these:

1. **Scoped bridge** — `newBean` resolves only app-own + declared classes.
   Transitional; kills cross-boundary reach, keeps ergonomics.
2. **Explicit host access** — `HostAccess.EXPLICIT`, only annotated methods callable,
   class lookup off. The enforcement substrate under everything below.
3. **Capability registry** — the platform names everything; manifest-resolved handles
   injected at construction. The primary model.
4. **Command bus** — one injected `invoke(op, params)` entry point, data-only.
   The discipline: handle contracts are specified data-in/data-out even while they
   are plain Java calls, keeping every capability one transport swap away from remote.
5. **Process boundary** — the bridge becomes RPC. The remote tier; also the only
   option that confines Java.

Composition chosen: **2 as mechanism, 3 as model, 4 as discipline**, with 1 as the
compatibility move and 5 where confinement of arbitrary code is required. (Watch
item: WASM guests as a future language-agnostic remote/restricted variant; nothing
built for 3–4 is wasted getting there.)

## 5. Config isolation (the immediate problem)

A script can reach another app's config through exactly three doors; each closes
structurally:

1. **`app.config`** — already scoped to the app's own PID; becomes an injected,
   frozen, plain-data object. There is no "read config" function in the guest world.
2. **The filesystem** — today wide open via `Java.type('java.nio.file.Files')`
   because contexts allow host class lookup. Closed by context policy (lookup off,
   host access explicit) — a few lines at the single context factory the pipeline
   branch established. `lib-io` is already resource-scoped.
3. **Platform objects** — `newBean` into ConfigAdmin or config-bearing services.
   Closed by registry resolution; in the capability model the door never existed.

For the restricted tier the protection is the *absence of capability*, not a runtime
check — it costs nothing and cannot drift. For the trusted tier (Java, Nashorn) the
files remain readable; the effective control is decision 9: nothing worth reading is
in them. Audit rule for capability authors: no path-shaped parameters that escape the
app's own resource space; no environment or system-property exposure.

## 6. What libs look like without `newBean`

Today the insecurity is entirely in resolution — `__.newBean('…GetContentHandler')`
is an open `Class.forName`. Two stages:

**Stage 1 — closed resolution, zero JS changes.** Lib bundles register their handler
classes (bundle header or annotation scan); `newBean` resolves against the registry,
filtered by declared dependencies plus the app's own bundle. `GetContentHandler` and
`content.js` are unchanged. Undeclared reach fails with a loud, specific error naming
the manifest fix (never a generic "class not found" that would be misattributed to
the engine swap).

**Stage 2 — capability handles, for scoping and the restricted tier.**

```java
@Component(service = ScriptCapabilityFactory.class, property = "name=xp:content")
public class ContentCapabilityFactory implements ScriptCapabilityFactory {
    public Object create(AppGrant grant) {
        return new ContentCapability(contentService, grant.contentScope());
    }
}
```

```js
// lib/xp/content.js
var content = __.capability('xp:content');   // absent if not granted
exports.get = function (params) {
    return content.get({ key: required(params, 'key') });
};
```

Handler bodies largely survive (bean-per-op becomes one handle with methods taking
param maps); the existing `toNativeObject`/`MapSerializable` marshaling carries over.
The genuinely new work is grant design per capability: what a content scope, an event
scope, an egress scope *is*. Structural facts that make this cheap: `__` is a module
wrapper parameter, not a global — per-app/per-tier environments need no new mechanism
(the detached-task runner on the pipeline branch already applies environments this
way); callbacks already cross as `JsFunctionHandle`s.

**Nashorn shim:** shared lib JS must run on both engines during the deprecation
window. On Nashorn, `__.capability(name)` returns the same handle constructed with an
unrestricted grant — which is simply what trusted tier means. One lib codebase;
enforcement only where confinement is claimed.

## 7. The manifest

The app manifest declaring capabilities (which config, which repos, which APIs, which
egress) is the load-bearing artifact across all tiers, introduced before enforcement
exists:

1. first as review metadata and documentation;
2. then enforced at the bridge for the restricted tier;
3. then enforced as token scopes for the remote tier.

Same declaration, escalating teeth. Marketplace rule: restricted-tier listing (light
review) requires a manifest and Graal; Java-bearing or Nashorn apps are trusted-tier
listings reviewed as adopted code. Tier classification must be visible in tooling; a
restricted manifest combined with Nashorn engine selection is rejected at install,
never silently downgraded.

## 8. Nashorn end-of-life

- Frozen "as is": no new features, no WinterTC surface, no ESM, no capability
  enforcement, no hardening.
- All new platform capabilities land Graal-only; migration pressure comes from
  features (debugging, fetch, ESM) and Market rules, not breakage.
- Engine selection is per app (`X-Script-Engine`), so customers migrate app by app.
- Removal at a later major release, per the pipeline branch's flip plan.
- Consequence for this document: enforcement machinery, door-audit tests and the
  capability matrix are single-engine (Graal), halving the ongoing test surface.

## 9. Delivery phases

Prerequisite: the GraalJS pipeline branch merges first (context factory seam, engine
parity test infrastructure, `JsFunctionHandle`, module-environment application).

**Pre-gate — usage scan (~50k):** scan Enonic Market and known customer code for
direct `__.newBean` and `Java.type` use. Findings set the deprecation timeline and
are the largest estimate variable.

### Stage 1 — closed resolution and config isolation (~1.0–1.2M output tokens)

| Gate | Deliverable | Est. |
|---|---|---|
| S1-A | Bean registry + scoped `newBean` (registration convention, resolution swap, app-own allowance, loud diagnostics, lib-module sweep; Graal only per §8) | ~350k |
| S1-B | Context policy parameter on the Graal context factory (tier enum, host lookup/access per policy, `Java.type` deprecation path, parity tests) | ~250k |
| S1-C | Config hardening + door-audit suite (frozen `app.config` injection; red-team tests that attempt every door and prove failure — the standing regression gate for the property) | ~200k |
| S1-D | Secret references (provider SPI, `${secret:…}` resolution at injection, local vault for self-hosted, rotation hook, docs) | ~350k |

### Stage 2 — capability registry and restricted tier MVP (~3.5–4.5M output tokens)

| Gate | Deliverable | Est. |
|---|---|---|
| S2-A | Registry/factory SPI, grant objects, manifest schema, context injection wiring | ~600k |
| S2-B..F | Core libs to scoped handles (content, node, event, task, io, http) — grant semantics designed per lib | ~350–500k each |
| S2-G | Restricted-tier context profile, reference sandboxed app, migration guide | ~600k |

Long tail (remaining libs, Market manifest tooling, review pipeline): additional,
deferrable, parallelizable.

Decisions with ecosystem blast radius (`Java.type` policy, manifest schema freeze,
per-capability scope semantics) get the same stop-and-review-with-the-team treatment
as Phase 4's wire-schema rule in the NoDB track.

## 10. Risks and open questions

1. **Usage-scan surprises:** a widely deployed app reaching across boundaries turns
   timeline decisions into stakeholder work.
2. **Grant semantics for impersonation:** `lib-context.run` with arbitrary principals
   is privilege escalation as a library call; as a capability it must be a declared,
   reviewable grant. Expect this to take longer to decide than to build.
3. **Oracle GraalVM licensing** for polyglot isolates (rung 3): gratis under GFTC,
   but redistribution inside a product needs a deliberate clearance; rung 4 (process
   pool) is the fallback.
4. **Dev-mode ergonomics:** registry and grants must reload cleanly on file change or
   developers will route around the model.
5. **Indirect leaks through granted handles:** any capability that accepts a path or
   dumps environment reopens the filesystem door at one remove — a standing review
   rule, enforced by the door-audit suite.
6. **Availability containment at rung 1/2** is partial (shared heap). Mitigations:
   statement limits, per-app context budgets (already on the pipeline branch), worker
   role placement; rung 3/4 where plans demand hard caps.

## 11. Definition of success

- An app's manifest is a complete, truthful statement of its authority; installing a
  restricted app adds zero bytecode to the JVM and grants nothing undeclared.
- No script can read another app's configuration or secrets on any tier where
  confinement is claimed; secrets are rotatable references everywhere.
- App-facing lib APIs are byte-compatible throughout; ecosystem apps migrate tiers
  without code changes beyond their manifest.
- Marketplace review cost for restricted apps is manifest + script review, measured
  in minutes; the trusted tier is an explicit, visible, priced-in adoption decision.
- The same capability contract runs in-process, in isolates, and out of process,
  chosen per plan without API change.
- Nashorn exits at a major release having cost zero sandbox investment.
