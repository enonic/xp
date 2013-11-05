module api_content {

    export class ContentUpdatedEvent extends api_event.Event {

        private model:api_content.ContentSummary;

        constructor( model:api_content.ContentSummary ) {
            super( 'ContentUpdatedEvent' );
            this.model = model;
        }

        public getModel():api_content.ContentSummary {
            return this.model;
        }

        static on( handler:( event:api_content.ContentUpdatedEvent ) => void ) {
            api_event.onEvent( 'ContentUpdatedEvent', handler );
        }
    }

}