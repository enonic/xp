module app.browse {

    export class ModuleBrowsePanel extends api.app.browse.BrowsePanel<api.module.ModuleSummary> {

        private browseActions: app.browse.ModuleBrowseActions;

        private moduleTreeGridPanel: ModuleTreeGridPanel;

        private toolbar: ModuleBrowseToolbar;

        constructor() {
            var treeGridContextMenu = new app.browse.ModuleTreeGridContextMenu();
            this.moduleTreeGridPanel = components.gridPanel = new ModuleTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = ModuleBrowseActions.init();
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new ModuleBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new ModuleBrowseItemPanel();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.moduleTreeGridPanel,
                browseItemPanel: browseItemPanel,
                filterPanel: undefined
            });

            api.module.ModuleImportedEvent.on((event: api.module.ModuleImportedEvent) => {
                this.setRefreshNeeded(true);
                this.refreshFilterAndGrid();
            });

            api.module.ModuleDeletedEvent.on((event: api.module.ModuleDeletedEvent) => {
                var moduleKey = event.getModuleKey();
                this.moduleTreeGridPanel.removeItem(moduleKey.toString());
            });

            this.moduleTreeGridPanel.onTreeGridSelectionChanged((event: api.app.browse.grid.TreeGridSelectionChangedEvent) => {
                this.browseActions.updateActionsEnabledState(<any[]>event.getSelectedModels());
            });
        }

        extModelsToBrowseItems(models: Ext_data_Model[]): api.app.browse.BrowseItem<api.module.ModuleSummary>[] {

            var browseItems: api.app.browse.BrowseItem<api.module.ModuleSummary>[] = [];

            models.forEach((model: Ext_data_Model, index: number) => {

                var moduleModel: api.module.ModuleSummary = api.module.ModuleSummary.fromExtModel(model);

                var item = new api.app.browse.BrowseItem<api.module.ModuleSummary>(moduleModel).
                    setDisplayName(moduleModel.getDisplayName()).
                    setPath(moduleModel.getName()).
                    setIconUrl(api.util.getAdminUri('common/images/icons/icoMoon/32x32/puzzle.png'));

                browseItems.push(item);
            });
            return browseItems;
        }
    }

}