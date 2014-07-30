module app.wizard {

    export class PublishContentEvent extends api.event.Event2 {
        private content: any;

        constructor(content: any) {
            super();
            this.content = content;
        }

        getContent(): any {
            return this.content;
        }

        static on(handler: (event: PublishContentEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PublishContentEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}