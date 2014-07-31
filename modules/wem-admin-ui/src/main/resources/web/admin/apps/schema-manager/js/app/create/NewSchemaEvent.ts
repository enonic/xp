module app.create {

    import Event2 = api.event.Event2;

    export class NewSchemaEvent extends Event2 {

        private schemaType:api.schema.SchemaKind;

        constructor(schemaType?:api.schema.SchemaKind) {
            super();

            this.schemaType = schemaType;
        }

        getSchemaKind():api.schema.SchemaKind {
            return this.schemaType;
        }

        static on(handler: (event: NewSchemaEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: NewSchemaEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

}