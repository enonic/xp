# Enonic XP lib-event TS types

> TypeScript definitions for `lib-event` library of Enonic XP

## Install

```bash
npm i --save-dev @enonic/lib-event
```

## Use

__1. Update tsconfig.json__

Add the corresponding types to your `tsconfig.json` file that is used for application's server-side TypeScript code:

```json
{
  "compilerOptions": {
    "types": [
      "@enonic/lib-event"
    ]
  }
}
```

### Global XP types

You may also want to add the `@enonic/script-impl` types, since it provides a definitions for XP global objects, e.g. `log`, `app`, etc. The
documentation can be found [here](https://github.com/enonic/xp/tree/master/modules/script/script-impl/README.md).

## Example

```typescript
const eventLib, {EnonicEvent} = __non_webpack_require__('/lib/xp/event');

export function initNodeEventsLogger() {
    eventLib.listener({
        type: 'node.*',
        localOnly: true,
        callback: (event: EnonicEvent) => {
            if (event.data && event.data.nodes) {
                event.data.nodes.forEach((node) => {
                    log.info(`Event [${event.type}] for node: ${JSON.stringify(node)}`);
                });
            }
        }
    });
}
```