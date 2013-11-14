module app_browse {

    export interface ModuleBrowseItemPanelParams {

        actionMenuActions:api_ui.Action[];
    }

    export class ModuleBrowseItemPanel extends api_app_browse.BrowseItemPanel<api_module.Module> {

        constructor(params:ModuleBrowseItemPanelParams) {
            super(<api_app_browse.BrowseItemPanelParams>{
                actionMenuActions: params.actionMenuActions
            });
        }

    }
}