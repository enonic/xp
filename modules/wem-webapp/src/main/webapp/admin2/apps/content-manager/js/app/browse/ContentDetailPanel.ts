module app_browse {

    export class ContentDetailPanel extends api_app_browse.BrowseDetailPanel {

        fireGridDeselectEvent(model:api_model.ContentModel) {
            var models:api_model.ContentModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
        }
    }
}
