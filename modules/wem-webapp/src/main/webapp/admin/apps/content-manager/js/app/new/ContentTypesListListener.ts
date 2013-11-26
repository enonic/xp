module app_new {

    export interface ContentTypesListListener extends api_event.Listener {

        onSelected(contentTypeListItem:ContentTypeListItem);

    }

}