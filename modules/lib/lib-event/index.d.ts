// declare module "*/lib/xp/event" {
//   namespace eventLib {
//     type ListenerParams = import('./event').ListenerParams;
//   }


//   const eventLib: typeof import('./event');
//   export = eventLib;
// }

declare module '*/lib/xp/event' {
  import event = require('lib/event');
  export = event;
}
