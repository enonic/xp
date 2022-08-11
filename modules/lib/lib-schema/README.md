# Enonic XP lib-schema TS types

> TypeScript definitions for `lib-schema` library of Enonic XP
## Install

```bash
npm i --save-dev @enonic/lib-schema
```

## Use

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code.

`tsconfig.json`
```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/lib-schema"
    ]
  }
}
```

### Require and custom imports

To make `require` work out of box, you must install and add the `@enonic-types/global` types. Aside from providing definitions for XP global objects, e.g. `log`, `app`, `__`, etc, requiring library by the default path will return typed object.

`tsconfig.json`
```diff
{
  "compilerOptions": {
    "types": [
+     "@enonic-types/global"
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

More detailed explanation on how it works and how to type custom import function can be found [here](https://github.com/enonic/xp/tree/master/modules/lib/typescript/README.md).

### ES6-style import

If you are planning to use `import` in your code and transpile it with the default `tsc` TypeScript compiler, you'll need to add proper types mapping to your configuration.

`tsconfig.json`
```diff
{
  "compilerOptions": {
    "types": [
      "@enonic-types/lib-schema"
    ]
+   "baseUrl": "./",
+   "paths": {
+     "/lib/xp/schema": ["node_modules/@enonic-types/lib-schema"],
+   }
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

Setting `baseUrl` and `paths` will allow the `tsc` to keep the valid paths in the resulting JavaScript files.
