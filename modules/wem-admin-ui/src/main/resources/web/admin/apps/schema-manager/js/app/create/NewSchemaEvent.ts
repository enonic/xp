module app.create {

    export class NewSchemaEvent extends api.event.Event {

        private schemaType:api.schema.SchemaKind;

        constructor(schemaType?:api.schema.SchemaKind) {
            super('newSchema');

            this.schemaType = schemaType;
        }

        static on(handler:(event:NewSchemaEvent) => void) {
            api.event.onEvent('newSchema', handler);
        }

        getSchemaKind():api.schema.SchemaKind {
            return this.schemaType;
        }
    }

}