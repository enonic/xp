# Enonic XP lib-task TS types

> TypeScript definitions for `lib-task` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/lib-task
```

## Use

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/lib-task"
    ]
  }
}
```

### Require and custom imports

To make `require` work out of box, you must install and add the `@enonic-types/global` types. Aside from providing definitions for XP global
objects, e.g. `log`, `app`, `__`, etc, requiring library by the default path will return typed object.

`tsconfig.json`

```diff
{
  "compilerOptions": {
    "types": [
+     "@enonic-types/global"
      "@enonic-types/lib-task"
    ]
  }
}
```

`example.ts`

```ts
const {submit, executeFunction, submitNamed, submitTask, list, get, sleep, progress, isRunning} = require('/lib/xp/task');
```

More detailed explanation on how it works and how to type custom import function can be
found [here](https://github.com/enonic/xp/tree/master/modules/lib/typescript/README.md).

### ES6-style import

If you are planning to use `import` in your code and transpile it with the default `tsc` TypeScript compiler, you'll need to add proper
types mapping to your configuration.

`tsconfig.json`

```diff
{
  "compilerOptions": {
    "types": [
      "@enonic-types/lib-task"
    ]
+   "baseUrl": "./",
+   "paths": {
+     "/lib/xp/task": ["node_modules/@enonic-types/lib-task"],
+   }
  }
}
```

`example.ts`

```ts
import {submit, executeFunction, submitNamed, submitTask, list, get, sleep, progress, isRunning} from '/lib/xp/task';
```

Setting `baseUrl` and `paths` will allow the `tsc` to keep the valid paths in the resulting JavaScript files.
