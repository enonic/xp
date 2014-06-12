module app.home {
    import Event2 = api.event.Event2;

    export class ReturnToAppEvent extends Event2 {

        constructor() {
            super();
        }

        static on(handler: (event: ReturnToAppEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ReturnToAppEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}