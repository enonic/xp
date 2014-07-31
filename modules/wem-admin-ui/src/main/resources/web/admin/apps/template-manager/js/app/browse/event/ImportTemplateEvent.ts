module app.browse.event {

    import Event = api.event.Event;

    export class ImportTemplateEvent extends Event {

        constructor() {
            super('importTemplate');
        }

        static on(handler: (event: ImportTemplateEvent) => void, contextWindow: Window = window) {
            Event.bind("importTemplate", handler, contextWindow);
        }

        static un(handler: (event: ImportTemplateEvent) => void, contextWindow: Window = window) {
            Event.unbind("importTemplate", handler, contextWindow);
        }
    }
}