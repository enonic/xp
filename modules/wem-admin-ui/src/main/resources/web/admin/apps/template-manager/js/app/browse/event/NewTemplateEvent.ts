module app.browse.event {

    import Event = api.event.Event;

    export class NewTemplateEvent extends Event {

        constructor() {
            super('newTemplate');
        }

        static on(handler: (event: NewTemplateEvent) => void, contextWindow: Window = window) {
            Event.bind("newTemplate", handler, contextWindow);
        }

        static un(handler: (event: NewTemplateEvent) => void, contextWindow: Window = window) {
            Event.unbind("newTemplate", handler, contextWindow);
        }
    }
}