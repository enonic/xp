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

            this.filterPanel = new app_browse.SchemaBrowseFilterPanel();

            super(this.toolbar, this.gridPanel, this.detailPanel, this.filterPanel);

            this.handleEvents();
        }

        private handleEvents() {

            GridSelectionChangeEvent.on((event) => {

                if (event.getModels().length == 0) {
                    this.detailPanel.setItems([]);
                } else {

                    var model:api_model.SchemaModel = event.getModels()[0];
                    var item = new api_app_browse.BrowseItem(model).
                        setDisplayName(model.data.displayName).
                        setIconUrl(model.data.iconUrl);
                    this.detailPanel.setItems([item]);

                }

            });

        }
    }


    export function createLoadContentParams(filterPanelValues:any) {
        var params:any = {types: [], modules: []};
        var paramTypes = params.types;
        var paramModules = params.modules;
        var typeFilter = filterPanelValues.Type;
        var moduleFilter = filterPanelValues.Module;
        if (typeFilter) {
            if (typeFilter.some(function (item) {
                return item == 'Relationship Type'
            })) {
                paramTypes.push('RELATIONSHIP_TYPE');
            }
            if (typeFilter.some(function (item) {
                return item == 'Content Type'
            })) {
                paramTypes.push('CONTENT_TYPE');
            }
            if (typeFilter.some(function (item) {
                return item == 'Mixin'
            })) {
                paramTypes.push('MIXIN');
            }
        }
        moduleFilter.forEach(function (moduleName) {
            paramModules.push(moduleName);
        });
        params.search = filterPanelValues.query;
        return params;
    }
}