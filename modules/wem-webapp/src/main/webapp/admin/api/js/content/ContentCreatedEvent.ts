module api_content {

    export class ContentCreatedEvent extends api_event.Event {

        private content:api_content.Content;

        constructor(content:api_content.Content ) {
            super( 'ContentCreatedEvent' );
            this.content = content;
        }

        public getContent():api_content.Content {
            return this.content;
        }

        static on( handler:( event:api_content.ContentCreatedEvent ) => void ) {
            api_event.onEvent( 'ContentCreatedEvent', handler );
        }
    }

}