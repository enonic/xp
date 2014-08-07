module api.schema {

    export class SchemaCreatedEvent extends api.event.Event {

        private schema: api.schema.Schema;

        constructor(schema: api.schema.Schema) {
            super();
            this.schema = schema;
        }

        public getSchema(): Schema {
            return this.schema;
        }

        static on(handler: (event: SchemaCreatedEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: SchemaCreatedEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }
    }

}