# Enonic XP lib-scheduler TS types

> TypeScript definitions for `lib-scheduler` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/lib-scheduler
```

## Use

### Require and custom imports

To make `require` work out of the box, install `@enonic-types/global` and add it to `types` together with `@enonic-types/lib-scheduler`.
The global types define XP global objects, e.g. `log`, `app`, `__`, etc, and listing the library makes requiring it by the default path
return a typed object. To add the types of all XP libraries at once, use `@enonic-types/xp` instead.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/global",
      "@enonic-types/lib-scheduler"
    ]
  }
}
```

`example.ts`

```ts
const {create, modify} = require('/lib/xp/scheduler');
```

More detailed explanation on how it works and how to type custom import function can be
found [here](https://developer.enonic.com/docs/xp/stable/api).

### ES6-style import

If you are planning to use `import` in your code and transpile it with the default `tsc` TypeScript compiler, you'll need to add proper
types mapping to your configuration.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "paths": {
      "/lib/xp/scheduler": ["./node_modules/@enonic-types/lib-scheduler"]
    }
  }
}
```

`example.ts`

```ts
import {create, modify} from '/lib/xp/scheduler';
```

Setting `paths` will allow the `tsc` to resolve the types and keep the valid paths in the resulting JavaScript files.

With pnpm, also install `@enonic-types/core` directly, at the same version as `@enonic-types/lib-scheduler`. The types imported through
`paths` resolve their `@enonic-types/core` import from the top level of `node_modules`, and pnpm does not put it there.
