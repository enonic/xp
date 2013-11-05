module api_content {

    export class ContentCreatedEvent extends api_event.Event {

        private path:api_content.ContentPath;

        constructor( path:api_content.ContentPath ) {
            super( 'ContentCreatedEvent' );
            this.path = path;
        }

        public getPath():api_content.ContentPath {
            return this.path;
        }

        static on( handler:( event:api_content.ContentCreatedEvent ) => void ) {
            api_event.onEvent( 'ContentCreatedEvent', handler );
        }
    }

}