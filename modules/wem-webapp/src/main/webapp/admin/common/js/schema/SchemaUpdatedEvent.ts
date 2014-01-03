module api.schema {

    export class SchemaUpdatedEvent extends api.event.Event {

        private schema:api.schema.Schema;

        constructor( schema:api.schema.Schema ) {
            super( 'SchemaUpdatedEvent' );
            this.schema = schema;
        }

        public getSchema():api.schema.Schema {
            return this.schema;
        }

        static on( handler:( event:SchemaUpdatedEvent ) => void ) {
            api.event.onEvent( 'SchemaUpdatedEvent', handler );
        }
    }

}