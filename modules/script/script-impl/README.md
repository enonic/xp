# Enonic XP global TS types

> TypeScript definitions for global variables of Enonic XP

## Install

```bash
npm i --save-dev @enonic-types/base
```

## Use

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code:

```json
{
  "compilerOptions": {
    "typeRoots": [
      "node_modules/@types",
      "node_modules/@enonic-types",
    ]
  }
}
```

> The paths to the `node_mosules` assume that your `tsconfig.json` is placed under your project root.

After that, all the global XP variables will be typed.

Adding not `"types": [...]` but `"typeRoots"` will allow to discover types for every other enonic library, that will be installed later.

Import functions, such as `require` and `__non_webpack_require__`, will return typed objects if the corresponding types for imported
libraries are also added to your `tsconfig.json`.

## Configuration

### Require

To add support for type resolution for the custom libraries via `require` or `__non_webpack_require__`, you can redeclare the `XpLibraries` interface in global scope, wich will lead to declaration merging:

```ts
declare global {
    interface XpLibraries {
        '/lib/custom/mylib': typeof import('./mylib');
    }
}
```

### Beans

To create a new bean, a `__.newBean()` function must be used. Making it return a proper type can be done in two ways. Say you have create an interface for that been somewhere in your project:

```ts
interface SomeHelper {
  help(text: string): void;
}
```

__Option 1__

You can pass the type argument explicitly. This option is a bit cleaner.

```ts
const helper = __.newBean<SomeHelper>('com.me.project.SomeHelper');
```

__Option 2__

Or you can map the bean name to bean interface. It may be a preferable way to do it, if the bean is used across multiple files:

```ts
declare global {
    interface XpBeans {
        'com.me.project.SomeHelper': SomeHelper;
    }
}

const helper = __.newBean('com.me.project.SomeHelper');
```