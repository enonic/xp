module app_browse {

    export class SpaceItemStatisticsPanel extends api_app_browse.ItemStatisticsPanel {

        constructor() {
            super({
                actionMenu: new api_ui_menu.ActionMenu(
                    SpaceBrowseActions.EDIT_SPACE,
                    SpaceBrowseActions.DELETE_SPACE
                )
            });
        }

    }

}