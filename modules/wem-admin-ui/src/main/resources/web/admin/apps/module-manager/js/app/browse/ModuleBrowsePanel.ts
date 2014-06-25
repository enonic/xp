module app.browse {

    import ModuleTreeGrid = app.browse.ModuleTreeGrid;
    import ModuleSummary = api.module.ModuleSummary;

    export class ModuleBrowsePanel extends api.app.browse.BrowsePanel<api.module.ModuleSummary> {

        private browseActions: app.browse.ModuleBrowseActions;

        private moduleTreeGridPanel: ModuleTreeGrid;

        private toolbar: ModuleBrowseToolbar;

        constructor() {
            var treeGridContextMenu = new app.browse.ModuleTreeGridContextMenu();
            this.moduleTreeGridPanel = new ModuleTreeGrid(); // TODO add contextMenu

            this.browseActions = ModuleBrowseActions.init(this.moduleTreeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new ModuleBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new ModuleBrowseItemPanel();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel2: this.moduleTreeGridPanel,
                browseItemPanel: browseItemPanel,
                filterPanel: undefined
            });

            api.module.ModuleDeletedEvent.on((event: api.module.ModuleDeletedEvent) => {
                var moduleKey = event.getModuleKey();
//                this.moduleTreeGridPanel.removeItem(moduleKey.toString());
            });

            this.moduleTreeGridPanel.onRowSelectionChanged((selectedRows: ModuleSummary[]) => {
                this.browseActions.updateActionsEnabledState(<any[]>selectedRows);
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