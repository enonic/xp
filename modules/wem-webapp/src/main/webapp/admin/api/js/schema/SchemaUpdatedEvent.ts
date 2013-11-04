module api_schema {

    export class SchemaUpdatedEvent extends api_event.Event {

        private schemaKind:api_schema.SchemaKind;
        private schemaName:string;

        constructor( schemaKind:api_schema.SchemaKind, schemaName:string ) {
            super( 'SchemaUpdatedEvent' );
            this.schemaKind = schemaKind;
            this.schemaName = schemaName;
        }

        public getSchemaKind():api_schema.SchemaKind {
            return this.schemaKind;
        }

        public getSchemaName():string{
            return this.schemaName;
        }

        static on( handler:( event:SchemaUpdatedEvent ) => void ) {
            api_event.onEvent( 'SchemaUpdatedEvent', handler );
        }
    }

}