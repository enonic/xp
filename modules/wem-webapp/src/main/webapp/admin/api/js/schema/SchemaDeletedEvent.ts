module api_schema {

    export class SchemaDeletedEvent extends api_event.Event {

        private type:string;
        private schemaNames:string[];

        constructor(type:string, schemaNames:string[]) {
            super("SchemaDeletedEvent");
            this.type = type;
            this.schemaNames = schemaNames;
        }

        public getType():string {
            return this.type;
        }

        public getSchemaNames():string[] {
            return this.schemaNames;
        }

        static on(handler:(event:SchemaDeletedEvent) => void) {
            api_event.onEvent('SchemaDeletedEvent', handler);
        }

    }
}