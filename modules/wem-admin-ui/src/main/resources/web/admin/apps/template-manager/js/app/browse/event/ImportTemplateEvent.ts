module app.browse.event {

    import Event2 = api.event.Event2;

    export class ImportTemplateEvent extends Event2 {

        constructor() {
            super('importTemplate');
        }

        static on(handler: (event: ImportTemplateEvent) => void, contextWindow: Window = window) {
            Event2.bind("importTemplate", handler, contextWindow);
        }

        static un(handler: (event: ImportTemplateEvent) => void, contextWindow: Window = window) {
            Event2.unbind("importTemplate", handler, contextWindow);
        }
    }
}