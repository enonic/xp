module app_browse {

    export class SchemaBrowsePanel extends api_app_browse.BrowsePanel {

        private toolbar:SchemaBrowseToolbar;
        private gridPanel:SchemaTreeGridPanel;
        private detailPanel:SchemaBrowseItemPanel;
        private filterPanel;

        constructor() {
            this.toolbar = new SchemaBrowseToolbar();
            this.gridPanel = components.gridPanel = new SchemaTreeGridPanel('schemaTreeGrid');
            this.detailPanel = components.detailPanel = new SchemaBrowseItemPanel();

            this.filterPanel = new api_app_browse.BrowseFilterPanel();

            super(this.toolbar, this.gridPanel, this.detailPanel, this.filterPanel);

            this.handleEvents();
        }

        private handleEvents() {

            GridSelectionChangeEvent.on((event) => {

                if (event.getModels().length == 0) {
                    this.detailPanel.setItems([]);
                } else {

                    var model:api_model.SchemaExtModel = event.getModels()[0];
                    var item = new api_app_browse.BrowseItem(model).
                        setDisplayName(model.data.displayName).
                        setIconUrl(model.data.iconUrl);
                    this.detailPanel.setItems([item]);

                }

            });

        }
    }
}