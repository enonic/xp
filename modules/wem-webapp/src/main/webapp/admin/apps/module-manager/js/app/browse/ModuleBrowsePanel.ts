module app_browse {

    export class ModuleBrowsePanel extends api_app_browse.BrowsePanel<api_module.Module> {

        private browseActions:app_browse.ModuleBrowseActions;

        private moduleTreeGridPanel:ModuleTreeGridPanel;

        private toolbar:ModuleBrowseToolbar;

        constructor() {
            var treeGridContextMenu = new app_browse.ModuleTreeGridContextMenu();
            this.moduleTreeGridPanel = components.gridPanel = new ModuleTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = ModuleBrowseActions.init();
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new ModuleBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new ModuleBrowseItemPanel({actionMenuActions: [
                this.browseActions.IMPORT_MODULE,
                this.browseActions.EXPORT_MODULE,
                this.browseActions.DELETE_MODULE]});

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.moduleTreeGridPanel,
                browseItemPanel: browseItemPanel,
                filterPanel: undefined
            });

            api_module.ModuleImportedEvent.on((event:api_module.ModuleImportedEvent) => {
                this.setRefreshNeeded(true);
                this.refreshFilterAndGrid();
            });

            api_module.ModuleDeletedEvent.on((event:api_module.ModuleDeletedEvent) => {
                var moduleKey = event.getModuleKey();
                this.moduleTreeGridPanel.remove(moduleKey.toString());
            });

            this.moduleTreeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    this.browseActions.updateActionsEnabledState(<any[]>event.selectedModels);
                }
            });
        }

        extModelsToBrowseItems(models:Ext_data_Model[]):api_app_browse.BrowseItem<api_module.Module>[] {

            var browseItems:api_app_browse.BrowseItem<api_module.Module>[] = [];

            models.forEach((model:Ext_data_Model, index:number) => {

                var moduleModel:api_module.Module = api_module.Module.fromExtModel(model);

                var item = new api_app_browse.BrowseItem<api_module.Module>(moduleModel ).
                    setDisplayName(moduleModel.getDisplayName()).
                    setPath(moduleModel.getName()).
                    setIconUrl(api_util.getAdminUri('common/images/icons/icoMoon/32x32/folder.png'));

                browseItems.push(item);
            });
            return browseItems;
        }
    }

}