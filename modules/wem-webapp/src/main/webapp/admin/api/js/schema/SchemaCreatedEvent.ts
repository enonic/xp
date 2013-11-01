module api_schema {

    export class SchemaCreatedEvent extends api_event.Event {

        private type:string;
        private schemaName:string;

        constructor( type:string, schemaName:string ) {
            super( 'SchemaCreatedEvent' );
            this.type = type;
            this.schemaName = schemaName;
        }

        public getType():string {
            return this.type;
        }

        public getSchemaName():string{
            return this.schemaName;
        }

        static on( handler:( event:SchemaCreatedEvent ) => void ) {
            api_event.onEvent( 'SchemaCreatedEvent', handler );
        }
    }

}