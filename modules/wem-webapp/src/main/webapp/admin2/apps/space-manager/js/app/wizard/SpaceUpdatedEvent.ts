module app_wizard {

    export class SpaceUpdatedEvent extends api_event.Event {

        constructor() {
            super('spaceUpdated');
        }

        static on(handler:(event:app_wizard.SpaceUpdatedEvent) => void) {
            api_event.onEvent('spaceUpdated', handler);
        }
    }

}
