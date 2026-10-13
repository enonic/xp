# Enonic XP lib-vhost TS types

> TypeScript definitions for `lib-vhost` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/lib-vhost
```

## Use

### Require and custom imports

To make `require` work out of the box, install `@enonic-types/global` and add it to `types` together with `@enonic-types/lib-vhost`.
The global types define XP global objects, e.g. `log`, `app`, `__`, etc, and listing the library makes requiring it by the default path
return a typed object. To add the types of all XP libraries at once, use `@enonic-types/xp` instead.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/global",
      "@enonic-types/lib-vhost"
    ]
  }
}
```

`example.ts`

```ts
const {isEnabled, list} = require('/lib/xp/vhost');
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
      "/lib/xp/vhost": ["node_modules/@enonic-types/lib-vhost"]
    }
  }
}
```

`example.ts`

```ts
import {isEnabled, list} from '/lib/xp/vhost';
```

Setting `baseUrl` and `paths` will allow the `tsc` to keep the valid paths in the resulting JavaScript files.
