module api_schema {

    export class SchemaDeletedEvent extends api_event.Event {

        private schemaType:string;
        private schemaNames:string[];

        constructor(schemaType:string, schemaNames:string[]) {
            super("SchemaDeletedEvent");
            this.schemaType = schemaType;
            this.schemaNames = schemaNames;
        }

        public getSchemaType():string {
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