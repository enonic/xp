module app.create {

    export interface SiteTemplatesListListener extends api.event.Listener {

        onSelected(item:SiteTemplateListItem);

    }

}