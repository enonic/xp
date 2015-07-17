module app.browse {

    import ApplicationKey = api.module.ApplicationKey;
    import Module = api.module.Module;
    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import UninstallModuleRequest = api.module.UninstallModuleRequest;
    import UpdateModuleRequest = api.module.UpdateModuleRequest;
    import StartModuleRequest = api.module.StartModuleRequest;
    import StopModuleRequest = api.module.StopModuleRequest;
    import ApplicationUpdatedEvent = api.module.ApplicationUpdatedEvent;
    import ApplicationUpdatedEventType = api.module.ApplicationUpdatedEventType;

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
                var applicationKeys = ApplicationKey.fromModules(event.getModules());
                new StopModuleRequest(applicationKeys).sendAndParse()
                    .then(() => {
                    }).done();
            });
            StartModuleEvent.on((event: StartModuleEvent) => {
                var applicationKeys = ApplicationKey.fromModules(event.getModules());
                new StartModuleRequest(applicationKeys).sendAndParse()
                    .then(() => {
                    }).done();
            });

            api.module.ApplicationUpdatedEvent.on((event: ApplicationUpdatedEvent) => {
                if (ApplicationUpdatedEventType.INSTALLED == event.getEventType()) {
                    this.moduleTreeGrid.appendModuleNode(event.getApplicationKey());
                } else if (ApplicationUpdatedEventType.UNINSTALLED == event.getEventType()) {
                    this.moduleTreeGrid.deleteModuleNode(event.getApplicationKey());
                } else if (event.isNeedToUpdateModule()){
                    this.moduleTreeGrid.updateModuleNode(event.getApplicationKey());
                }
            });

        }
    }

}