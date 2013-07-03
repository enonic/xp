module app_browse {

    export class SpaceBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        constructor() {
            super({actionMenuActions: [SpaceBrowseActions.EDIT_SPACE, SpaceBrowseActions.OPEN_SPACE, SpaceBrowseActions.DELETE_SPACE]});
        }

        fireGridDeselectEvent(model:api_model.SpaceModel) {
            var models:api_model.SpaceModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
        }
    }
}
