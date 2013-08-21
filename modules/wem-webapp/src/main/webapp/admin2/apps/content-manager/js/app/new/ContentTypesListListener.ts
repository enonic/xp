module app_new {

    export interface ContentTypesListListener extends api_event.Listener {

        onSelected(contentType:api_remote_contenttype.ContentType);

    }

}