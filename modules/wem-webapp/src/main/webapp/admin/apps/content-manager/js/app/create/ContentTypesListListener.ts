module app.create {

    export interface ContentTypesListListener extends api.event.Listener {

        onSelected(item:ContentTypeListItem);

    }

}