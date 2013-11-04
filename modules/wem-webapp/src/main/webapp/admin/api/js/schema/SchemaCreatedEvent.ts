module api_schema {

    export class SchemaCreatedEvent extends api_event.Event {

        private schemaType:string;
        private schemaName:string;

        constructor( schemaType:string, schemaName:string ) {
            super( 'SchemaCreatedEvent' );
            this.schemaType = schemaType;
            this.schemaName = schemaName;
        }

        public getSchemaType():string {
            return this.schemaType;
        }

        public getSchemaName():string{
            return this.schemaName;
        }

        static on( handler:( event:SchemaCreatedEvent ) => void ) {
            api_event.onEvent( 'SchemaCreatedEvent', handler );
        }
    }

}