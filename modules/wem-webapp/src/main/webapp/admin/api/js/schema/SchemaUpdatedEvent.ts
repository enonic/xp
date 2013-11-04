module api_schema {

    export class SchemaUpdatedEvent extends api_event.Event {

        private schemaType:string;
        private schemaName:string;

        constructor( schemaType:string, schemaName:string ) {
            super( 'SchemaUpdatedEvent' );
            this.schemaType = schemaType;
            this.schemaName = schemaName;
        }

        public getSchemaType():string {
            return this.schemaType;
        }

        public getSchemaName():string{
            return this.schemaName;
        }

        static on( handler:( event:SchemaUpdatedEvent ) => void ) {
            api_event.onEvent( 'SchemaUpdatedEvent', handler );
        }
    }

}