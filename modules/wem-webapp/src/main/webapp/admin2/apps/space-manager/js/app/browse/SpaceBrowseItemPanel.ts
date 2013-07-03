module app_browse {

    export class SpaceBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        private actionMenu:SpaceActionMenu;

        constructor() {
            this.actionMenu = new SpaceActionMenu();
            super({
                actionMenu: this.actionMenu,
                fireGridDeselectEvent: this.fireGridDeselectEvent
            });
        }

        fireGridDeselectEvent(model:api_model.SpaceModel) {
            var models:api_model.SpaceModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
        }
    }
}
