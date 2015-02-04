module app.browse {

    import ModuleKey = api.module.ModuleKey;
    import Module = api.module.Module;
    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import UninstallModuleRequest = api.module.UninstallModuleRequest;
    import UpdateModuleRequest = api.module.UpdateModuleRequest;
    import StartModuleRequest = api.module.StartModuleRequest;
    import StopModuleRequest = api.module.StopModuleRequest;
    import ModuleUpdatedEvent = api.module.ModuleUpdatedEvent;

    export class ModuleBrowsePanel extends api.app.browse.BrowsePanel<api.module.Module> {

        private browseActions: app.browse.ModuleBrowseActions;

        private moduleTreeGrid: ModuleTreeGrid;

        private toolbar: ModuleBrowseToolbar;

        private moduleIconUrl: string;

        constructor() {

            this.moduleTreeGrid = new ModuleTreeGrid();

            this.browseActions = <app.browse.ModuleBrowseActions> this.moduleTreeGrid.getContextMenu().getActions();

            this.toolbar = new ModuleBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new ModuleBrowseItemPanel();

            super({
                browseToolbar: this.toolbar,
                treeGrid: this.moduleTreeGrid,
                browseItemPanel: browseItemPanel,
                filterPanel: undefined
            });

            this.moduleTreeGrid.onSelectionChanged((currentSelection: TreeNode<Module>[], fullSelection: TreeNode<Module>[]) => {
                this.browseActions.updateActionsEnabledState(<any[]>fullSelection.map((elem) => {
                    return elem.getData();
                }));
            });

            this.moduleIconUrl = api.util.UriHelper.getAdminUri('common/images/icons/icoMoon/128x128/puzzle.png');

            this.registerEvents();
        }

        treeNodesToBrowseItems(nodes: TreeNode<Module>[]): BrowseItem<Module>[] {
            var browseItems: BrowseItem<Module>[] = [];

            // do not proceed duplicated content. still, it can be selected
            nodes.forEach((node: TreeNode<Module>, index: number) => {
                for (var i = 0; i <= index; i++) {
                    if (nodes[i].getData().getId() === node.getData().getId()) {
                        break;
                    }
                }
                if (i === index) {
                    var moduleEl = node.getData();
                    var item = new BrowseItem<Module>(moduleEl).
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
            StopModuleEvent.on((event: StopModuleEvent) => {
                var moduleKeys = ModuleKey.fromModules(event.getModules());
                new StopModuleRequest(moduleKeys).sendAndParse()
                    .then(() => {
                    }).done();
            });
            StartModuleEvent.on((event: StartModuleEvent) => {
                var moduleKeys = ModuleKey.fromModules(event.getModules());
                new StartModuleRequest(moduleKeys).sendAndParse()
                    .then(() => {
                    }).done();
            });
            UpdateModuleEvent.on((event: UpdateModuleEvent) => {
                var moduleKeys = ModuleKey.fromModules(event.getModules());
                new UpdateModuleRequest(moduleKeys).sendAndParse().done();
            });
            UninstallModuleEvent.on((event: UninstallModuleEvent) => {
                var moduleKeys = ModuleKey.fromModules(event.getModules());
                new UninstallModuleRequest(moduleKeys).sendAndParse()
                    .then(() => {
                        this.moduleTreeGrid.reload();
                    }).done();
            });

            var installModuleDialog = new InstallModuleDialog(this.moduleTreeGrid);
            InstallModuleEvent.on((event: InstallModuleEvent) => {
                installModuleDialog.open();
            });

            api.module.ModuleUpdatedEvent.on((event: ModuleUpdatedEvent) => {
                if (event.getEventType() === 'INSTALLED') {
                    this.moduleTreeGrid.appendModuleNode(event.getModuleKey());
                } else if (event.getEventType() === 'UNINSTALLED') {
                    this.moduleTreeGrid.deleteModuleNode(event.getModuleKey());
                } else {
                    this.moduleTreeGrid.updateModuleNode(event.getModuleKey());
                }
            });

        }
    }

}