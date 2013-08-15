module app_browse {

    export class SpaceBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        private actionMenu:SpaceActionMenu;

        constructor() {
            this.actionMenu = new SpaceActionMenu();
            super({
                actionMenu: this.actionMenu
            });
        }

    }
}
