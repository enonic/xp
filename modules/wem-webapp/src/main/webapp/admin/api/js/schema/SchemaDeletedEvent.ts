module api_schema {

    export class SchemaDeletedEvent extends api_event.Event {

        private schemaKind:api_schema.SchemaKind;
        private schemaNames:string[];

        constructor(schemaType:api_schema.SchemaKind, schemaNames:string[]) {
            super("SchemaDeletedEvent");
            this.schemaKind = schemaType;
            this.schemaNames = schemaNames;
        }

        public getSchemaKind():api_schema.SchemaKind {
            return this.schemaKind;
        }

        public getSchemaNames():string[] {
            return this.schemaNames;
        }

        static on(handler:(event:SchemaDeletedEvent) => void) {
            api_event.onEvent('SchemaDeletedEvent', handler);
        }

    }
}