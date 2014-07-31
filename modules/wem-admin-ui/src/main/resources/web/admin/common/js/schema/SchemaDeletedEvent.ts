module api.schema {

    export class SchemaDeletedEvent extends api.event.Event2 {

        private schemas:api.schema.Schema[];

        constructor(schemas:api.schema.Schema[]) {
            super();
            this.schemas = schemas;
        }

        public getSchemas():api.schema.Schema[] {
            return this.schemas;
        }

        static on(handler: (event: SchemaDeletedEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: SchemaDeletedEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}