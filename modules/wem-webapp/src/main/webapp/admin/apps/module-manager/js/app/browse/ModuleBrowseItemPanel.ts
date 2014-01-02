module app.browse {

    export interface ModuleBrowseItemPanelParams {

        actionMenuActions:api.ui.Action[];
    }

    export class ModuleBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.module.ModuleSummary> {

        constructor(params:ModuleBrowseItemPanelParams) {
            super(<api.app.browse.BrowseItemPanelParams>{
                actionMenuActions: params.actionMenuActions
            });
        }

    }
}