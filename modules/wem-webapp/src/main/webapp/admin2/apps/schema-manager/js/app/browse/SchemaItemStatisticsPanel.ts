module app_browse {

    export interface SchemaItemStatisticsPanelParams {
        editAction: api_ui.Action;
        deleteAction: api_ui.Action;
    }

    export class SchemaItemStatisticsPanel extends api_app_browse.ItemStatisticsPanel {

        constructor(params:SchemaItemStatisticsPanelParams) {
            super({
                actionMenu: new api_ui_menu.ActionMenu(
                    params.editAction,
                    params.deleteAction
                )
            });
        }

    }

}
