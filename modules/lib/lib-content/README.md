# Enonic XP lib-content TS types

> TypeScript definitions for `lib-content` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/lib-content
```

## Use

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/lib-content"
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
      "@enonic-types/lib-content"
    ]
  }
}
```

`example.ts`

```ts
const {get, query, getChildren} = require('/lib/xp/content');
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
      "@enonic-types/lib-content"
    ]
+   "baseUrl": "./",
+   "paths": {
+     "/lib/xp/content": ["node_modules/@enonic-types/lib-content"],
+   }
  }
}
```

`example.ts`

```ts
import {get, query, getChildren} from '/lib/xp/content';
```

Setting `baseUrl` and `paths` will allow the `tsc` to keep the valid paths in the resulting JavaScript files.

### Additional types

Note that not all types are available for import, but can easily be retrieved from the `Content`: 

```ts
import type {Content} from '/lib/xp/content';

type Attachments = Content['attachments'];

type ContentInheritType = Content['inherit'];

type Workflow = Content['workflow'];
```
