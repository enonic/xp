module api.schema {

    export class SchemaCreatedEvent extends api.event.Event {

        private schema:api.schema.Schema;

        constructor( schema:api.schema.Schema ) {
            super( 'SchemaCreatedEvent' );
            this.schema = schema;
        }

        public getSchema():Schema {
            return this.schema;
        }

        static on( handler:( event:SchemaCreatedEvent ) => void ) {
            api.event.onEvent( 'SchemaCreatedEvent', handler );
        }
    }

}