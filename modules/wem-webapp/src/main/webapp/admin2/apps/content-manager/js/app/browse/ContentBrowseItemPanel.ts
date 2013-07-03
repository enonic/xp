module app_browse {

    export class ContentBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        private actionMenu:ContentActionMenu;

        constructor() {
            this.actionMenu = new ContentActionMenu();
            super({
                actionMenu: this.actionMenu,
                fireGridDeselectEvent: this.fireGridDeselectEvent
            });
        }

        fireGridDeselectEvent(model:api_model.ContentModel) {
            var models:api_model.ContentModel[] = [];
            models.push(model);
            new GridDeselectEvent(models).fire();
        }
    }
}
