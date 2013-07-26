module app_wizard {

    export class MixinCreatedEvent extends api_event.Event {

        constructor() {
            super('mixinCreated');
        }

        static on(handler:(event:app_wizard.MixinCreatedEvent) => void) {
            api_event.onEvent('mixinCreated', handler);
        }
    }

    export class MixinUpdatedEvent extends api_event.Event {

        constructor() {
            super('mixinUpdated');
        }

        static on(handler:(event:app_wizard.MixinUpdatedEvent) => void) {
            api_event.onEvent('mixinUpdated', handler);
        }
    }

}
