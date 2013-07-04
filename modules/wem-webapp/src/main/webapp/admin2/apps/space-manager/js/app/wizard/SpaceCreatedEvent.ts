module app_wizard {

    export class SpaceCreatedEvent extends api_event.Event {

        constructor() {
            super('spaceCreated');
        }

        static on(handler:(event:app_wizard.SpaceCreatedEvent) => void) {
            api_event.onEvent('spaceCreated', handler);
        }
    }

}
