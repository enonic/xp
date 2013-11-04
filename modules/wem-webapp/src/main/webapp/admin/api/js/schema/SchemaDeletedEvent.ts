module api_schema {

    export class SchemaDeletedEvent extends api_event.Event {

        private schemaType:api_schema.SchemaKind;
        private schemaNames:string[];

        constructor(schemaType:api_schema.SchemaKind, schemaNames:string[]) {
            super("SchemaDeletedEvent");
            this.schemaType = schemaType;
            this.schemaNames = schemaNames;
        }

        public getSchemaType():api_schema.SchemaKind {
            return this.schemaType;
        }

        public getSchemaNames():string[] {
            return this.schemaNames;
        }

        static on(handler:(event:SchemaDeletedEvent) => void) {
            api_event.onEvent('SchemaDeletedEvent', handler);
        }

    }
}