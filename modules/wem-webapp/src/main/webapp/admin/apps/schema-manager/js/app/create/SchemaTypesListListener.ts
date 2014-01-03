module app.create {

    export interface SchemaTypesListListener extends api.event.Listener {

        onSelected(schemaType:SchemaTypeListItem);

    }

}