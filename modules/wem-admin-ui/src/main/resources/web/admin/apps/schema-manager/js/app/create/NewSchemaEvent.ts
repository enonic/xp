module app.create {

    import Event = api.event.Event;

    export class NewSchemaEvent extends Event {

        private schemaType:api.schema.SchemaKind;

        constructor(schemaType?:api.schema.SchemaKind) {
            super();

            this.schemaType = schemaType;
        }

        getSchemaKind():api.schema.SchemaKind {
            return this.schemaType;
        }

        static on(handler: (event: NewSchemaEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: NewSchemaEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

}