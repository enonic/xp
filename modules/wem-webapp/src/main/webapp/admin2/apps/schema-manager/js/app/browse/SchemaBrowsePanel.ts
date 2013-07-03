module app_browse {

    export class SchemaBrowsePanel extends api_app_browse.BrowsePanel {

        constructor() {
            var toolbar = new SchemaBrowseToolbar();
            var grid = null /*components.gridPanel = new SchemaTreeGridPanel('schemaTreeGrid')*/;
            var detail = null /*components.detailPanel = new SchemaDetailPanel()*/;

            var filterPanel = null;

            super(toolbar, grid, detail, filterPanel);
        }
    }
}