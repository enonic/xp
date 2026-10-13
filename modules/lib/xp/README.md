# Enonic XP TS types

> TypeScript definitions for Enonic XP global objects and all XP libraries in one package

The package depends on `@enonic-types/global`, `@enonic-types/core`, and every `@enonic-types/lib-*` package of the same XP version. It
covers the libraries shipped with XP only; types for other libraries under the `@enonic-types` scope, like `lib-asset` or `lib-react4xp`,
have to be installed separately.

## Install

```bash
npm i --save-dev @enonic-types/xp
```

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
either hoist the scope with `public-hoist-pattern[]=@enonic-types/*` in `.npmrc`, or install the imported libraries together with
`@enonic-types/core` directly (`pnpm add -D @enonic-types/core @enonic-types/lib-content`).
