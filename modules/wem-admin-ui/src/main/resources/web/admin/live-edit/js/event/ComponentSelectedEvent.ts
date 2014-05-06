module LiveEdit.event {
    export class ComponentSelectedEvent extends api.event.Event {
        constructor() {
            super('componentSelectedEvent');
        }

        static on(handler:(event:ComponentSelectedEvent) => void) {
            api.event.onEvent('componentSelectedEvent', handler);
        }
    }
}
