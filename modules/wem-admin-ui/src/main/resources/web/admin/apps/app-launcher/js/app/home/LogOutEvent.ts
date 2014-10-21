module app.home {
    import Event = api.event.Event;

    export class LogOutEvent extends Event {

        constructor() {
            super();
        }

        static on(handler: (event: LogOutEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: LogOutEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}