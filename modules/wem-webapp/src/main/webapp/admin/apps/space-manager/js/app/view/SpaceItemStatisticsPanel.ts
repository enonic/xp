module app_view {

    export interface SpaceItemStatisticsPanelParams {
        editAction: api_ui.Action;
        deleteAction: api_ui.Action;
    }

    export class SpaceItemStatisticsPanel extends api_app_view.ItemStatisticsPanel {

        constructor(params:SpaceItemStatisticsPanelParams) {
            super({
                actionMenu: new api_ui_menu.ActionMenu(
                    [params.editAction,
                    params.deleteAction]
                )
            });
        }

    }

}