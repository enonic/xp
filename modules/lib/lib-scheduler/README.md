# Enonic XP lib-scheduler TS types

> TypeScript definitions for `lib-scheduler` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/lib-scheduler
```

## Use

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/lib-scheduler"
    ]
  }
}
```

### Require and custom imports

To make `require` work out of the box, you must install and add the `@enonic-types/global` types. Aside from providing definitions for XP
global objects, e.g. `log`, `app`, `__`, etc, requiring a library by the default path will return typed object.

`tsconfig.json`

```diff
{
  "compilerOptions": {
    "types": [
+     "@enonic-types/global"
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

```diff
{
  "compilerOptions": {
    "types": [
      "@enonic-types/lib-scheduler"
    ]
+   "baseUrl": "./",
+   "paths": {
+     "/lib/xp/scheduler": ["node_modules/@enonic-types/lib-scheduler"],
+   }
  }
}
```

`example.ts`

```ts
import {create, modify} from '/lib/xp/scheduler';
```

Setting `baseUrl` and `paths` will allow the `tsc` to keep the valid paths in the resulting JavaScript files.
