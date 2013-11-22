module app_browse {

    export interface TemplateBrowseItemPanelParams {

        actionMenuActions: api_ui.Action[];

    }

    export class TemplateBrowseItemPanel extends api_app_browse.BrowseItemPanel<app_browse.TemplateBrowseItem> {

        constructor(params:TemplateBrowseItemPanelParams) {
            super(<api_app_browse.BrowseItemPanelParams>{
                actionMenuActions: params.actionMenuActions
            });
        }

    }
}