# Enonic XP lib-project TS types

> TypeScript definitions for `lib-project` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/lib-project
```

## Use

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/lib-project"
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
      "@enonic-types/lib-project"
    ]
  }
}
```

`example.ts`

```ts
const {create, modify, get, list, addPermissions, removePermissions, modifyReadAccess} = require('/lib/xp/project');
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
      "@enonic-types/lib-project"
    ]
+   "baseUrl": "./",
+   "paths": {
+     "/lib/xp/project": ["node_modules/@enonic-types/lib-project"],
+   }
  }
}
```

`example.ts`

```ts
import {create, modify, delete as deleteProject, get, list, addPermissions, removePermissions, modifyReadAccess} from '/lib/xp/project';
```

Setting `baseUrl` and `paths` will allow the `tsc` to keep the valid paths in the resulting JavaScript files.
