# XP Script Debugging — DevTools and DAP on GraalJS

**Status:** Draft v0.1

**Date:** 2026-08-03

**Companion documents:** [`SCRIPT-SECURITY.md`](SCRIPT-SECURITY.md),
[`SCRIPT-ESM.md`](SCRIPT-ESM.md),
`docs/design/graaljs-execution-pipeline.md` (on the GraalJS pipeline branch)

---

## 1. Purpose

Debugging has been the missing link of the Nashorn world: no breakpoints, no
stepping, no inspection — print-statement archaeology. GraalJS is the first script
engine XP has shipped that comes with production-grade debugger instruments. This
document defines how they are wired in, what the pool architecture means for
debugging semantics, and the security posture.

Positioning matters as much as the feature: "the engine that finally gives you a
real debugger" is the single most persuasive migration message for Nashorn-era
developers, and it should be true on day one of the default engine flip. This work
is therefore sequenced **before** the flip, not after.

## 2. Current state

The GraalJS pipeline branch contains **no debugger wiring** — verified against the
branch; the only inspection-related decision is pinning the dev-mode pool to one
context "to keep today's debugging behavior", i.e. logging. Everything below is new
work, but the branch has built the prerequisites: a single context-construction
seam, thread-confined context ownership, and the planned `Source` cleanup.

## 3. What Graal provides

- **Chrome DevTools Protocol** (`inspect` engine option, chromeinspector
  instrument): breakpoints, stepping, watches, call stacks, console evaluation in
  the paused scope — from Chrome/Edge DevTools or any CDP client.
- **Debug Adapter Protocol** (DAP instrument): the same capabilities speaking VS
  Code's native debugging protocol — attach from the editor where the developer's
  TypeScript lives.

Both are polyglot tool artifacts enabled via builder options; no engine surgery.
Both are supported on the GraalJS-as-library configuration XP uses.

## 4. Architectural decisions

1. **Both protocols, dev profile only.** CDP for browser DevTools users, DAP for
   VS Code. The inspector is remote code execution by design: it is enabled only in
   the dev runtime profile, bound to localhost, with the option structurally absent
   from production profiles — a profile capability, not a config knob that exists
   everywhere and defaults off.
2. **Source identity is a debugging feature, not just cleanup.** Breakpoints bind
   only if the debugger sees real files. The pipeline branch already plans to
   replace string-concatenation module wrapping with proper `Source` objects (it
   broke `sourceMappingURL` comments, #9339); this work completes it **with
   app-resource URIs**, so DevTools shows `/site/parts/foo/foo.js` rather than
   anonymous eval blobs, and TypeScript source maps resolve to the TS the developer
   wrote.
3. **The main context is debuggable.** Listeners, timers and `main.js` bootstrap
   live on the dedicated main context; the inspector attaches to it as well as the
   request context — otherwise startup logic stays undebuggable, which is ironic
   given the history of #7821.
4. **Dev-mode pool semantics.** Pool of one context (already the branch's dev-mode
   rule) keeps request debugging deterministic: one context, sequential stepping.
   Thread confinement means a paused context blocks only its own slot, not a global
   monitor.
5. **Ephemeral task contexts get a dev-mode rule.** Detached tasks run in
   per-execution contexts in production; breakpoints there need either
   `inspect.Suspend` applied at context creation in dev mode, or a dev-mode rule
   that tasks reuse a persistent debug context. Decide one; a few lines either way,
   but it must be chosen, not inherited.
6. **CLI ergonomics mirror `nodb dev`.** `enonic sandbox start --inspect` prints
   the DevTools URL / DAP port; docs show the VS Code `launch.json` attach recipe.
   Zero-config attach is the acceptance bar.

## 5. Non-goals

- Production debugging. Cloud-side debugging of dev/clone tenants may become a
  control-plane feature later (token-scoped, audited, time-boxed — break-glass
  shaped); it is out of scope here and must not leak inspector capability into
  production profiles in the meantime.
- Nashorn debugging. Frozen tier (`SCRIPT-SECURITY.md` §8); the debugger is
  deliberately part of the migration incentive.
- Profiling/heap tooling. CDP exposes some of it and it may come along for free,
  but the committed scope is breakpoint debugging.

## 6. Delivery

One gate, on top of the pipeline branch, before the default engine flip.

| Gate | Deliverable | Verification | Est. |
|---|---|---|---|
| D-A | Inspector wiring (CDP + DAP artifacts, dev-profile-only enablement, localhost bind); `Source` URIs completed for app resources incl. source-map resolution; main-context attach; ephemeral-task dev-mode rule; `--inspect` CLI + docs | Breakpoint set in editor TS binds and hits in a controller, a `main.js` listener, and a task; step/watch/eval work in both DevTools and VS Code; production profile provably cannot enable the inspector (test); sandbox attach is zero-config | ~120–180k |

## 7. Risks and open questions

1. **Shared-engine scope:** the inspector instrument attaches at engine level while
   XP shares one engine across apps — verify session/context filtering presents the
   developer with *their* app's contexts, not the whole engine's.
2. **Pause semantics under the pool:** a paused request context in a pool of one
   halts the app's other requests in dev mode — acceptable and familiar
   (single-threaded dev servers), but document it.
3. **Dev-mode reload interaction:** context invalidation on file change must
   detach/reattach inspector sessions cleanly rather than leaving dead sessions.
4. **Remote dev tenants** (cloud): demand will arrive ("attach to my dev tenant in
   the cloud"). The answer is a control-plane feature with token scoping and audit,
   not an open port; park it explicitly so it is not improvised under pressure.

## 8. Definition of success

- A developer sets a breakpoint in their TypeScript in VS Code or DevTools, hits it
  in a controller, a `main.js` listener, and a task, steps and inspects — with
  zero configuration beyond `--inspect`.
- Stack traces and breakpoints reference real app resource paths, source-mapped.
- No production runtime can have an inspector enabled, provably, by construction.
- The Nashorn→Graal migration pitch includes a working debugger demo on flip day.
