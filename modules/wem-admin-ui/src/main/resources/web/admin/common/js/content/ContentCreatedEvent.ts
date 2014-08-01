module api.content {

    export class ContentCreatedEvent extends api.event.Event {

        private content:api.content.Content;

        constructor(content:api.content.Content ) {
            super();
            this.content = content;
        }

        public getContent():api.content.Content {
            return this.content;
        }

        static on(handler: (event: ContentCreatedEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentCreatedEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }

}