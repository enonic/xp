module LiveEdit.event {
    export class ComponentDeselectedEvent extends api.event.Event {
        constructor() {
            super('componentDeselectedEvent');
        }

        static on(handler:(event:ComponentDeselectedEvent) => void) {
            api.event.onEvent('componentDeselectedEvent', handler);
        }
    }
}
