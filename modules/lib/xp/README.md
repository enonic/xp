# Enonic XP TS types

> TypeScript definitions for Enonic XP global objects and all XP libraries in one package

The package depends on `@enonic-types/global`, `@enonic-types/core`, and every `@enonic-types/lib-*` package of the same XP version. It
covers the libraries shipped with XP only; types for other libraries under the `@enonic-types` scope, like `lib-asset` or `lib-react4xp`,
have to be installed separately.

## Install

```bash
npm i --save-dev @enonic-types/xp
```

Use it instead of the individual packages, not alongside them. When switching to it, remove the direct `@enonic-types/global`,
`@enonic-types/core` and `@enonic-types/lib-*` dependencies, or keep them at exactly the version of `@enonic-types/xp`. Either way,
`npm ls @enonic-types/core` (`pnpm why @enonic-types/core` with pnpm) must show one version. See [Version mismatches](#version-mismatches).

## Use

### Require

Add the package to the `types` of the `tsconfig.json` used for the application's server-side code:

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/xp"
    ]
  }
}
```

The entry is required: TypeScript includes only `@types/*` packages automatically, and since TypeScript 6, `types` defaults to `[]`.

After that, the global XP objects, e.g. `log`, `app`, `__`, are typed, and requiring any XP library by its default path returns a typed
object:

```ts
const {get, query} = require('/lib/xp/content');
```

### ES6-style import

To use `import` with the default `tsc` compiler, map the library paths to the installed packages:

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/xp"
    ],
    "paths": {
      "/lib/xp/*": ["./node_modules/@enonic-types/lib-*"]
    }
  }
}
```

```ts
import {get, query} from '/lib/xp/content';
```

The mapping points at the top level of `node_modules`. npm and Yarn Classic hoist the libraries there, but pnpm does not, so with pnpm
either install the imported libraries and `@enonic-types/core` directly, at the version of `@enonic-types/xp`, or hoist the scope in
`pnpm-workspace.yaml`:

```yaml
publicHoistPattern:
  - '@enonic-types/*'
```

pnpm 11 and later ignore `public-hoist-pattern` in `.npmrc`.

### Core types

To import from `@enonic-types/core` directly, e.g. `import type {Content} from '@enonic-types/core'`, add it as a direct dependency at the
version of `@enonic-types/xp`. The copy that comes with `@enonic-types/xp` is reachable with npm only because npm hoists it; with pnpm the
import fails with TS2307.

## Version mismatches

Different versions of the packages in one project can break type-checking, with errors reported inside `node_modules/@enonic-types`:

- TS2717 "Subsequent property declarations must have the same type", when a library listed in `types` next to `@enonic-types/xp` has
  another version than the one `@enonic-types/xp` depends on.
- TS2374 "Duplicate index signature", TS2578 "Unused '@ts-expect-error' directive" or TS2300 "Duplicate identifier", when more than one
  copy of `@enonic-types/core` ends up in the compilation.

Align the versions of the direct dependencies. When another package pulls in a different version, force the one `@enonic-types/xp`
uses with `overrides` in `package.json` for npm:

```json
{
  "overrides": {
    "@enonic-types/core": "8.2.0"
  }
}
```

If the package is also a direct dependency, use `"$@enonic-types/core"` as the value, since npm rejects an override that differs from the
direct dependency. For pnpm, set `overrides` in `pnpm-workspace.yaml`:

```yaml
overrides:
  '@enonic-types/core': 8.2.0
```

`skipLibCheck` silences these errors, but it also turns off checking of every declaration file and leaves the duplicate copies installed.
