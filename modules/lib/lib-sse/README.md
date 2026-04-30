# Enonic XP lib-sse TS types

> TypeScript definitions for `lib-sse` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/lib-sse
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
const {
    send,
    sendToGroup,
    close,
    isOpen,
    addToGroup,
    removeFromGroup,
    getGroupSize
} = require('/lib/xp/sse');
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
      "/lib/xp/sse": ["node_modules/@enonic-types/lib-sse"]
    }
  }
}
```

`example.ts`

```ts
import {
    send,
    sendToGroup,
    close,
    isOpen,
    addToGroup,
    removeFromGroup,
    getGroupSize
} from '/lib/xp/sse';
```

Setting `baseUrl` and `paths` will allow the `tsc` to keep the valid paths in the resulting JavaScript files.
