module app_new {

    export class NewSchemaEvent extends api_event.Event {

        private schemaType:api_schema.SchemaKind;

        constructor(schemaType?:api_schema.SchemaKind) {
            super('newSchema');

            this.schemaType = schemaType;
        }

        static on(handler:(event:NewSchemaEvent) => void) {
            api_event.onEvent('newSchema', handler);
        }

        getSchemaType():api_schema.SchemaKind {
            return this.schemaType;
        }
    }

}