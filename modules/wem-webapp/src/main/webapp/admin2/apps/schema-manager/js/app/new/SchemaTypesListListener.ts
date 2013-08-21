module app_new {

    export interface SchemaTypesListListener extends api_event.Listener {

        onSelected(schemaType:SchemaTypeListItem);

    }

}