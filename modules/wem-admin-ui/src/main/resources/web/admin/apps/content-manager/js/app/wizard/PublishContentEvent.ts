module app.wizard {

    export class PublishContentEvent extends api.event.Event {
        private content: any;

        constructor(content: any) {
            super();
            this.content = content;
        }

        getContent(): any {
            return this.content;
        }

        static on(handler: (event: PublishContentEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PublishContentEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}