module app.browse {

    export interface SchemaBrowseItemPanelParams {

        actionMenuActions:api.ui.Action[];
    }

    export class SchemaBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.schema.Schema> {

        constructor(params:SchemaBrowseItemPanelParams) {
            super(<api.app.browse.BrowseItemPanelParams>{
                actionMenuActions: params.actionMenuActions
            });
        }

    }
}