module app_new {

    export interface SiteTemplatesListListener extends api_event.Listener {

        onSelected(item:SiteTemplateListItem);

    }

}