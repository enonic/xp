module app_view {

    export interface ContentItemStatisticsPanelParams {
        editAction: api_ui.Action;
        deleteAction: api_ui.Action;
    }

    export class ContentItemStatisticsPanel extends api_app_view.ItemStatisticsPanel {

        constructor(params:ContentItemStatisticsPanelParams) {
            super({
                actionMenu: new api_ui_menu.ActionMenu([
                    params.editAction,
                    params.deleteAction]
                )
            });
        }

    }

}
