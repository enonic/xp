module api.schema {

    export class SchemaCreatedEvent extends api.event.Event2 {

        private schema:api.schema.Schema;

        constructor( schema:api.schema.Schema ) {
            super();
            this.schema = schema;
        }

        public getSchema():Schema {
            return this.schema;
        }

        static on(handler: (event: SchemaCreatedEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: SchemaCreatedEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }

}