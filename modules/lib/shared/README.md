# Enonic XP shared TS types

> TypeScript definitions that are shared between core libraries

## Install

```bash
npm i --save-dev @enonic-types/shared
```

## Use

> It is not necessary to include this dependency to your project, as all core libraries export related shared types, e.g. Content library exports `Content` type.

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code.

`tsconfig.json`
```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/shared"
    ]
  }
}
```

### Import
 
All shared types can be imported using ES6-style import:
```ts
// All shared types are available for export from the root
import {Content} from '@enonic-types/shared';

// Any type can also be imported from it's own file
import {LiteralUnion} from '@enonic-types/shared/utils';
```

## Modules

* `auth` — principal-related types.
* `content` — `Content`, `Workflow`, and different states.
* `portal` — `Component` and `Region` types.
* `utils` — contains a bunch of utility types that simplify custom types creation.

### Content

`Content` contains the `x` property, that have a very special `XpXData` type.

`XpXData` is an interface, that is added to the global scope, so it can be modified using the [declaration merging](https://www.typescriptlang.org/docs/handbook/declaration-merging.html#merging-interfaces).
This allows you to set the shape of the XData in your project, simply by declaring the `XpXData` like this:
```ts
declare global {
    interface XpXData {
        myProperty: 'myValue';
    }
}
```
