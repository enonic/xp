module app_browse {

    export class SpaceDetailPanel extends api_app_browse.BrowseDetailPanel {

        fireGridDeselectEvent(model:api_model.SpaceModel) {
            var models:api_model.SpaceModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
        }
    }
}
