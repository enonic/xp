# Enonic XP lib-schema TS types

> TypeScript definitions for `lib-schema` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/lib-schema
```

## Use

### Require and custom imports

To make `require` work out of the box, install `@enonic-types/global` and add it to `types` together with `@enonic-types/lib-schema`.
The global types define XP global objects, e.g. `log`, `app`, `__`, etc, and listing the library makes requiring it by the default path
return a typed object. To add the types of all XP libraries at once, use `@enonic-types/xp` instead.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/global",
      "@enonic-types/lib-schema"
    ]
  }
}
```

`example.ts`

```ts
const { 
    createSchema, 
    createComponent, 
    createStyles,
    getSchema,
    getComponent,
    getSite,
    getStyles,
    deleteSchema,
    deleteComponent,
    deleteStyles, 
    updateSchema,
    updateComponent,
    updateSite,
    updateStyles,
    listComponents, 
    listSchemas
} = require('/lib/xp/schema');
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
      "/lib/xp/schema": ["./node_modules/@enonic-types/lib-schema"]
    }
  }
}
```

`example.ts`

```ts
import {
    createSchema,
    createComponent,
    createStyles,
    getSchema,
    getComponent,
    getSite,
    getStyles,
    deleteSchema,
    deleteComponent,
    deleteStyles,
    updateSchema,
    updateComponent,
    updateSite,
    updateStyles,
    listComponents,
    listSchemas
} from '/lib/xp/schema';
```

Setting `paths` will allow the `tsc` to resolve the types and keep the valid paths in the resulting JavaScript files.

With pnpm, also install `@enonic-types/core` directly, at the same version as `@enonic-types/lib-schema`. The types imported through
`paths` resolve their `@enonic-types/core` import from the top level of `node_modules`, and pnpm does not put it there.
