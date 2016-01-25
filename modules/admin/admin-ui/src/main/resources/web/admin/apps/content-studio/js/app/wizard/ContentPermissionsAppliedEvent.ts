module app.wizard {

    export class ContentPermissionsAppliedEvent extends api.event.Event {
        private content: api.content.Content;

        constructor(content: api.content.Content) {
            super();
            this.content = content;
        }

        getContent(): api.content.Content {
            return this.content;
        }

        static on(handler: (event: api.content.OpenEditPermissionsDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: api.content.OpenEditPermissionsDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}