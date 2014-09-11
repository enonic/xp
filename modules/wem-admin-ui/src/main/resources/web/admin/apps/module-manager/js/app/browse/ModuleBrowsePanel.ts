module app.browse {

    import ModuleSummary = api.module.ModuleSummary;
    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;

    export class ModuleBrowsePanel extends api.app.browse.BrowsePanel<api.module.ModuleSummary> {

        private browseActions: app.browse.ModuleBrowseActions;

        private moduleTreeGridPanel: ModuleTreeGrid;

        private toolbar: ModuleBrowseToolbar;
        
        private moduleIconUrl: string;

        private flag : boolean;

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

            this.moduleTreeGridPanel.onRowSelectionChanged((selectedRows: TreeNode<ModuleSummary>[]) => {
                this.browseActions.updateActionsEnabledState(<any[]>selectedRows.map((elem) => {
                    return elem.getData();
                }));
            });

            this.moduleIconUrl = api.util.getAdminUri('common/images/icons/icoMoon/128x128/puzzle.png');

            this.registerEvents();
        }

        treeNodesToBrowseItems(nodes: TreeNode<ModuleSummary>[]): api.app.browse.BrowseItem<ModuleSummary>[] {
            var browseItems: BrowseItem<ModuleSummary>[] = [];

            // do not proceed duplicated content. still, it can be selected
            nodes.forEach((node: TreeNode<ModuleSummary>, index: number) => {
                for (var i = 0; i <= index; i++) {
                    if (nodes[i].getData().getId() === node.getData().getId()) {
                        break;
                    }
                }
                if (i === index) {
                    var moduleEl = node.getData();
                    var item = new BrowseItem<ModuleSummary>(moduleEl).
                        setId(moduleEl.getId()).
                        setDisplayName(moduleEl.getDisplayName()).
                        setPath(moduleEl.getName()).
                        setIconUrl(this.moduleIconUrl);
                    browseItems.push(item);
                }
            });
            return browseItems;
        }

        private registerEvents() {
            app.browse.StopModuleEvent.on((event: app.browse.StopModuleEvent) => {
                var moduleKeys: string[] = event.getModules().map<string>((mod: ModuleSummary) => {
                    return mod.getModuleKey().toString();
                });
                new api.module.StopModuleRequest(moduleKeys).sendAndParse()
                    .then(() => {
                        this.moduleTreeGridPanel.reload();
                    }).done();
            });
            app.browse.StartModuleEvent.on((event: app.browse.StartModuleEvent) => {
                var moduleKeys: string[] = event.getModules().map<string>((mod: ModuleSummary) => {
                    return mod.getModuleKey().toString();
                });
                new api.module.StartModuleRequest(moduleKeys).sendAndParse()
                    .then(() => {
                        this.moduleTreeGridPanel.reload();
                    }).done();
            });
            app.browse.UpdateModuleEvent.on((event: app.browse.UpdateModuleEvent) => {
                var moduleKeys: string[] = event.getModules().map<string>((mod: ModuleSummary) => {
                    return mod.getModuleKey().toString();
                });
                new api.module.UpdateModuleRequest(moduleKeys).sendAndParse().done();
            });
            app.browse.UninstallModuleEvent.on((event: app.browse.UninstallModuleEvent) => {
                var moduleKeys: string[] = event.getModules().map<string>((mod: ModuleSummary) => {
                    return mod.getModuleKey().toString();
                });
                new api.module.UninstallModuleRequest(moduleKeys).sendAndParse()
                    .then(() => {
                        this.moduleTreeGridPanel.reload();
                    }).done();
            });

            var installModuleDialog: app.browse.InstallModuleDialog = new app.browse.InstallModuleDialog(this.moduleTreeGridPanel);
            app.browse.InstallModuleEvent.on((event: app.browse.InstallModuleEvent) => {
                installModuleDialog.open();
            });

            api.module.ModuleUpdatedEvent.on((event: api.module.ModuleUpdatedEvent) => {
                // TODO reload just the module updated
                this.moduleTreeGridPanel.updateModuleNode(event.getModuleKey());
            })

        }
    }

}