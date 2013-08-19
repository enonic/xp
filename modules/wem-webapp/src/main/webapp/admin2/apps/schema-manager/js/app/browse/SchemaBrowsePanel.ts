module app_browse {

    export class SchemaBrowsePanel extends api_app_browse.BrowsePanel {

        private browseActions:app_browse.SchemaBrowseActions;

        private toolbar:SchemaBrowseToolbar;

        private treeGridPanel:app_browse.SchemaTreeGridPanel;

        private filterPanel:app_browse_filter.SchemaBrowseFilterPanel;

        private browseItemPanel:SchemaBrowseItemPanel;

        constructor() {
            var treeGridContextMenu = new app_browse.SchemaTreeGridContextMenu();
            this.treeGridPanel = components.gridPanel = new SchemaTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = SchemaBrowseActions.init(this.treeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new SchemaBrowseToolbar(this.browseActions);
            this.browseItemPanel = components.detailPanel = new SchemaBrowseItemPanel({actionMenuActions: [
                this.browseActions.NEW_SCHEMA,
                this.browseActions.EDIT_SCHEMA,
                this.browseActions.OPEN_SCHEMA,
                this.browseActions.DELETE_SCHEMA,
                this.browseActions.REINDEX_SCHEMA,
                this.browseActions.EXPORT_SCHEMA]});

            this.filterPanel = new app_browse_filter.SchemaBrowseFilterPanel();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.treeGridPanel,
                browseItemPanel: this.browseItemPanel,
                filterPanel: this.filterPanel});

            this.treeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    this.browseActions.updateActionsEnabledState(<any[]>event.selectedModels);
                }
            });
        }

        extModelsToBrowseItems(models:api_model.SchemaExtModel[]) {

            var browseItems:api_app_browse.BrowseItem[] = [];
            models.forEach((model:api_model.SchemaExtModel, index:number) => {
                var item = new api_app_browse.BrowseItem(models[index]).
                    setDisplayName(model.data.displayName).
                    setPath(model.data.name).
                    setIconUrl(model.data.iconUrl);
                browseItems.push(item);
            });
            return browseItems;
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