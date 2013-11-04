module app_browse {

    export interface SchemaBrowseItemPanelParams {

        actionMenuActions:api_ui.Action[];
    }

    export class SchemaBrowseItemPanel extends api_app_browse.BrowseItemPanel<api_schema.Schema> {

        constructor(params:SchemaBrowseItemPanelParams) {
            super(<api_app_browse.BrowseItemPanelParams>{
                actionMenuActions: params.actionMenuActions
            });
        }

    }
}