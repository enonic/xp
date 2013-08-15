module app_browse {

    export class SchemaBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        private actionMenu:SchemaActionMenu;

        constructor() {
            this.actionMenu = new SchemaActionMenu();

            super({
                actionMenu: this.actionMenu,
            });
        }

    }
}