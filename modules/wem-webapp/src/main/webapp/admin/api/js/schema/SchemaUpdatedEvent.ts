module api_schema {

    export class SchemaUpdatedEvent extends api_event.Event {

        private type:string;
        private schemaName:string;

        constructor( type:string, schemaName:string ) {
            super( 'SchemaUpdatedEvent' );
            this.type = type;
            this.schemaName = schemaName;
        }

        public getType():string {
            return this.type;
        }

        public getSchemaName():string{
            return this.schemaName;
        }

        static on( handler:( event:SchemaUpdatedEvent ) => void ) {
            api_event.onEvent( 'SchemaUpdatedEvent', handler );
        }
    }

}