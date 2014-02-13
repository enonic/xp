module app.contextwindow {

    export class LiveEditDragStartEvent extends api.event.Event {
        constructor() {
            super('liveEditDragStart');
        }

        static on(handler: (event: LiveEditDragStartEvent) => void) {
            api.event.onEvent('liveEditDragStart', handler);
        }
    }

    export class LiveEditDragStopEvent extends api.event.Event {
        constructor() {
            super('liveEditDragStop');
        }

        static on(handler: (event: LiveEditDragStopEvent) => void) {
            api.event.onEvent('liveEditDragStop', handler);
        }
    }
}