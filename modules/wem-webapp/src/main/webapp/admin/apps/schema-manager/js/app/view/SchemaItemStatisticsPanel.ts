module app.view {

    export interface SchemaItemStatisticsPanelParams {
        editAction: api.ui.Action;
        deleteAction: api.ui.Action;
    }

    export class SchemaItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.schema.Schema> {

        constructor(params:SchemaItemStatisticsPanelParams) {
            super({
                actionMenu: new api.ui.menu.ActionMenu([
                    params.editAction,
                    params.deleteAction]
                )
            });
        }

    }

}
