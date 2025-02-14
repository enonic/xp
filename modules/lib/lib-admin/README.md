# Enonic XP lib-admin TS types

> TypeScript definitions for `lib-admin` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/lib-admin
```

## Use

### Require and custom imports

To make `require` work out of the box, you must install and add the `@enonic-types/global` types. Aside from providing definitions for XP
global objects, e.g. `log`, `app`, `__`, etc, requiring a library by the default path will return typed object.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/global"
    ]
  }
}
```

`example.ts`

```ts
const {getBaseUri, getHomeToolUrl, HomeToolUrlType} = require('/lib/xp/admin');
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
    "baseUrl": "./",
    "paths": {
      "/lib/xp/admin": ["node_modules/@enonic-types/lib-admin"]
    }
  }
}
```

`example.ts`

```ts
import {getBaseUri, getHomeToolUrl, HomeToolUrlType} from '/lib/xp/admin';
```

Setting `baseUrl` and `paths` will allow the `tsc` to keep the valid paths in the resulting JavaScript files.
