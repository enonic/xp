module api.content {

    export class ContentUpdatedEvent extends api.event.Event2 {

        private model:api.content.ContentSummary;

        constructor( model:api.content.ContentSummary ) {
            super();
            this.model = model;
        }

        public getModel():api.content.ContentSummary {
            return this.model;
        }

        static on(handler: (event: ContentUpdatedEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentUpdatedEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }

}