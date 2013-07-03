module app_browse {

    export class ContentBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        fireGridDeselectEvent(model:api_model.ContentModel) {
            var models:api_model.ContentModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
        }
    }
}
