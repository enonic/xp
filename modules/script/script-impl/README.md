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

To add support for type resolution for the custom libraries via `require` or `__non_webpack_require__`, you can redeclare the `XpLibraries` interface in global scope, wich will lead to declaration merging:

```ts
declare global {
    interface XpLibraries {
        '/lib/custom/mylib': typeof import('./mylib');
    }
}
```