module api.schema {

    export class SchemaDeletedEvent extends api.event.Event {

        private schemas:api.schema.Schema[];

        constructor(schemas:api.schema.Schema[]) {
            super("SchemaDeletedEvent");
            this.schemas = schemas;
        }

        public getSchemas():api.schema.Schema[] {
            return this.schemas;
        }

        static on(handler:(event:SchemaDeletedEvent) => void) {
            api.event.onEvent('SchemaDeletedEvent', handler);
        }

    }
}