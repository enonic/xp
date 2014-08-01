module app.wizard {

    import Event = api.event.Event;

    export class MixinCreatedEvent extends Event {

        static on(handler: (event: MixinCreatedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: MixinCreatedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class MixinUpdatedEvent extends Event {

        static on(handler: (event: MixinUpdatedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: MixinUpdatedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

}
