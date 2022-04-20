# Enonic XP global TS types

> TypeScript definitions for global variables of Enonic XP

## Install

```bash
npm i --save-dev @enonic/script-impl
```

## Use

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code:

```json
{
  "compilerOptions": {
    "types": [
      "@enonic/script-impl"
    ]
  }
}
```

After that, all the global XP variables will be typed.

Import functions, such as `require` and `__non_webpack_require__`, will return typed objects, if the corresponding types for imported
libraries are also added to your `tsconfig.json`. 
