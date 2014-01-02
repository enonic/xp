module app.view {

    export interface ContentItemStatisticsPanelParams {
        editAction: api.ui.Action;
        deleteAction: api.ui.Action;
    }

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        constructor(params:ContentItemStatisticsPanelParams) {
            super({
                actionMenu: new api.ui.menu.ActionMenu([
                    params.editAction,
                    params.deleteAction]
                )
            });
        }

    }

}
