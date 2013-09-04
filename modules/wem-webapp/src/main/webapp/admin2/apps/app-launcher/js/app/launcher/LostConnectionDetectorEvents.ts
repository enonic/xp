module app_launcher {

    export class ConnectionLostEvent extends api_event.Event {

        constructor() {
            super('connectionLostEvent');
        }

        static on(handler:(event:ConnectionLostEvent) => void) {
            api_event.onEvent('connectionLostEvent', handler);
        }
    }

    export class ConnectionRestoredEvent extends api_event.Event {

        constructor() {
            super('connectionRestoredEvent');
        }

        static on(handler:(event:ConnectionRestoredEvent) => void) {
            api_event.onEvent('connectionRestoredEvent', handler);
        }

    }
}