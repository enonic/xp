module app.create {

    export class ItemSelectedEvent {

        private schemaType: SchemaTypeListItem;

        constructor(schemaType: SchemaTypeListItem) {
            this.schemaType = schemaType;
        }

        getSchemaType(): SchemaTypeListItem {
            return this.schemaType;
        }
    }
}