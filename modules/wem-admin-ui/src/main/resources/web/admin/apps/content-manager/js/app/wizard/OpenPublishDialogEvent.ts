module app.wizard {

    export class OpenPublishDialogEvent extends api.event.Event2 {
        private content: any;

        constructor(content: any) {
            super();
            this.content = content;
        }

        getContent(): any {
            return this.content;
        }

        static on(handler: (event: OpenPublishDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: OpenPublishDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}