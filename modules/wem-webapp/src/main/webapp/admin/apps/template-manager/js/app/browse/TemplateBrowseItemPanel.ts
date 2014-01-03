module app.browse {

    export interface TemplateBrowseItemPanelParams {

        actionMenuActions: api.ui.Action[];

    }

    export class TemplateBrowseItemPanel extends api.app.browse.BrowseItemPanel<app.browse.TemplateBrowseItem> {

        constructor(params:TemplateBrowseItemPanelParams) {
            super(<api.app.browse.BrowseItemPanelParams>{
                actionMenuActions: params.actionMenuActions
            });
        }

    }
}