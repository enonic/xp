module api_schema {

    export class SchemaCreatedEvent extends api_event.Event {

        private schema:api_schema.Schema;

        constructor( schema:api_schema.Schema ) {
            super( 'SchemaCreatedEvent' );
            this.schema = schema;
        }

        public getSchema():Schema {
            return this.schema;
        }

        static on( handler:( event:SchemaCreatedEvent ) => void ) {
            api_event.onEvent( 'SchemaCreatedEvent', handler );
        }
    }

}