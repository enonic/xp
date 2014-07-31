module app.wizard {

    import Event2 = api.event.Event2;

    export class MixinCreatedEvent extends Event2 {

        static on(handler: (event: MixinCreatedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: MixinCreatedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class MixinUpdatedEvent extends Event2 {

        static on(handler: (event: MixinUpdatedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: MixinUpdatedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

}
