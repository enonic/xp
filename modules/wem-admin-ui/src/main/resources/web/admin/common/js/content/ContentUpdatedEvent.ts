module api.content {

    export class ContentUpdatedEvent extends api.event.Event {

        private model:api.content.ContentSummary;

        constructor( model:api.content.ContentSummary ) {
            super( 'ContentUpdatedEvent' );
            this.model = model;
        }

        public getModel():api.content.ContentSummary {
            return this.model;
        }

        static on( handler:( event:api.content.ContentUpdatedEvent ) => void ) {
            api.event.onEvent( 'ContentUpdatedEvent', handler );
        }
    }

}