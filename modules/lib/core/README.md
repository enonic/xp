# Enonic XP Core TS types

> TypeScript definitions that are shared between libraries and represent core data structures.

## Install

```bash
npm i --save-dev @enonic-types/core
```

## Use

> It is not necessary to include this dependency into your project, as all libraries export related core types,
> e.g. Content library exports `Content` type.

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code.

`tsconfig.json`

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/core"
    ]
  }
}
```

### Import

All shared types can be imported using ES6-style import:

```ts
import {Content} from 'core';
```

### Content

`Content` contains the `x` property, that has a very special `XpXData` type.

`XpXData` is an interface, that is added to the global scope, so it can be modified using the
[declaration merging](https://www.typescriptlang.org/docs/handbook/declaration-merging.html#merging-interfaces).
This allows you to set the shape of the XData in your project, simply by declaring the `XpXData` like this:

```ts
declare global {
    interface XpXData {
        myProperty: 'myValue';
    }
}
```
