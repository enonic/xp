module api_schema {

    export class SchemaUpdatedEvent extends api_event.Event {

        private schema:api_schema.Schema;

        constructor( schema:api_schema.Schema ) {
            super( 'SchemaUpdatedEvent' );
            this.schema = schema;
        }

        public getSchema():api_schema.Schema {
            return this.schema;
        }

        static on( handler:( event:SchemaUpdatedEvent ) => void ) {
            api_event.onEvent( 'SchemaUpdatedEvent', handler );
        }
    }

}