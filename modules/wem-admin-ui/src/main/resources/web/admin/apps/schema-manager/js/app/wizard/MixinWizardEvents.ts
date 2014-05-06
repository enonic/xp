module app.wizard {

    export class MixinCreatedEvent extends api.event.Event {

        constructor() {
            super('mixinCreated');
        }

        static on(handler:(event:app.wizard.MixinCreatedEvent) => void) {
            api.event.onEvent('mixinCreated', handler);
        }
    }

    export class MixinUpdatedEvent extends api.event.Event {

        constructor() {
            super('mixinUpdated');
        }

        static on(handler:(event:app.wizard.MixinUpdatedEvent) => void) {
            api.event.onEvent('mixinUpdated', handler);
        }
    }

}
