module app_browse {

    export interface SpaceBrowseItemPanelParams {

        actionMenuActions:api_ui.Action[];
    }

    export class SpaceBrowseItemPanel extends api_app_browse.BrowseItemPanel {

        constructor(params:SpaceBrowseItemPanelParams) {
            super(<api_app_browse.BrowseItemPanelParams>{
                actionMenuActions: params.actionMenuActions
            });
        }

    }
}
