module api.content {

    export class ContentCreatedEvent extends api.event.Event {

        private content:api.content.Content;

        constructor(content:api.content.Content ) {
            super( 'ContentCreatedEvent' );
            this.content = content;
        }

        public getContent():api.content.Content {
            return this.content;
        }

        static on( handler:( event:api.content.ContentCreatedEvent ) => void ) {
            api.event.onEvent( 'ContentCreatedEvent', handler );
        }
    }

}