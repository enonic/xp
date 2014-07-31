module app.browse.event {

    import Event2 = api.event.Event2;

    export class NewTemplateEvent extends Event2 {

        constructor() {
            super('newTemplate');
        }

        static on(handler: (event: NewTemplateEvent) => void, contextWindow: Window = window) {
            Event2.bind("newTemplate", handler, contextWindow);
        }

        static un(handler: (event: NewTemplateEvent) => void, contextWindow: Window = window) {
            Event2.unbind("newTemplate", handler, contextWindow);
        }
    }
}