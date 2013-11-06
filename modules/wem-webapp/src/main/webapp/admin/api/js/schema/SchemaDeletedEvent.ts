module api_schema {

    export class SchemaDeletedEvent extends api_event.Event {

        private schemas:api_schema.Schema[];

        constructor(schemas:api_schema.Schema[]) {
            super("SchemaDeletedEvent");
            this.schemas = schemas;
        }

        public getSchemas():api_schema.Schema[] {
            return this.schemas;
        }

        static on(handler:(event:SchemaDeletedEvent) => void) {
            api_event.onEvent('SchemaDeletedEvent', handler);
        }

    }
}