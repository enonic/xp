# Enonic XP Core TS types

> TypeScript definitions that are shared between libraries and represent core 
> data structures.

## Install

```bash
npm i --save-dev @enonic-types/core
```

## Use

> It is not necessary to include this dependency into your project, as all
> libraries export related core types, e.g. Content library exports `Content`
> type.

All core types can be imported using ES6-style import:

```ts
import type {Content} from '@enonic-types/core';
```

Such an import needs `@enonic-types/core` as a direct dependency, at the same
version as the libraries or `@enonic-types/xp`. With pnpm, the copy that comes
with them is not reachable, and the import fails with TS2307. Two different
versions of this package in one project can break type-checking.

### Content

`Content` contains the `x` property, that has a very special `XpMixin` type.

`XpMixin` is an interface, that is added to the global scope, so it can be
modified using the [declaration merging](https://www.typescriptlang.org/docs/handbook/declaration-merging.html#merging-interfaces).
This allows you to set the shape of the Mixin in your project, simply by
declaring the `XpMixin` like this:

```ts
declare global {
    interface XpMixin {
        'com-mysite-app': {
            metadata: {
                metaTagTitle: string;
                metaTagImageId: string;
            }
        }
    }
}
```

### Additional types

`Content` is a complex type that contains unions and maps, that are not exported, but may be needed during the development. Actually, these types can easily be retrieved from the `Content` itself:

```ts
import type {Content} from '@enonic-types/core';

type Attachments = Content['attachments'];

type ContentInheritType = Content['inherit'];

type Workflow = Content['workflow'];
```
