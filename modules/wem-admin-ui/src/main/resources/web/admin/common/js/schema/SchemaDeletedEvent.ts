module api.schema {

    export class SchemaDeletedEvent extends api.event.Event {

        private schemas: api.schema.Schema[];

        constructor(schemas: api.schema.Schema[]) {
            super();
            this.schemas = schemas;
        }

        public getSchemas(): api.schema.Schema[] {
            return this.schemas;
        }

        static on(handler: (event: SchemaDeletedEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: SchemaDeletedEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }
}