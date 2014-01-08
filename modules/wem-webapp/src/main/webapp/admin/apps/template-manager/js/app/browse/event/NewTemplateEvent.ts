module app.browse.event {
    export class NewTemplateEvent extends api.event.Event {
        constructor() {
            super('newTemplate');
        }

        static on(handler:(event:NewTemplateEvent) => void) {
            api.event.onEvent('newTemplate', handler);
        }
    }
}