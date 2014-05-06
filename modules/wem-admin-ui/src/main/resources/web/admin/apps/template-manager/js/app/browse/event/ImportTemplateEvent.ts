module app.browse.event {
    export class ImportTemplateEvent extends api.event.Event {

        constructor() {
            super('importTemplate');
        }

        static on(handler:(event:ImportTemplateEvent) => void) {
            api.event.onEvent('importTemplate', handler);
        }
    }
}