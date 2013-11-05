module api_schema {

    export class SchemaCreatedEvent extends api_event.Event {

        private schemaKind:api_schema.SchemaKind;
        private schemaName:string;

        constructor( schemaKind:api_schema.SchemaKind, schemaName:string ) {
            super( 'SchemaCreatedEvent' );
            this.schemaKind = schemaKind;
            this.schemaName = schemaName;
        }

        public getSchemaKind():SchemaKind {
            return this.schemaKind;
        }

        public getSchemaName():string{
            return this.schemaName;
        }

        static on( handler:( event:SchemaCreatedEvent ) => void ) {
            api_event.onEvent( 'SchemaCreatedEvent', handler );
        }
    }

}