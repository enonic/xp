module api.content {

    export class ContentCreatedEvent extends api.event.Event2 {

        private content:api.content.Content;

        constructor(content:api.content.Content ) {
            super();
            this.content = content;
        }

        public getContent():api.content.Content {
            return this.content;
        }

        static on(handler: (event: ContentCreatedEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentCreatedEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }

}