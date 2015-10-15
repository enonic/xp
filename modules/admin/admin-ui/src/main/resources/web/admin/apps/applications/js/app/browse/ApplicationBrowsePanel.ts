module app.browse {

    import ApplicationKey = api.application.ApplicationKey;
    import Application = api.application.Application;
    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import UninstallApplicationRequest = api.application.UninstallApplicationRequest;
    import UpdateApplicationRequest = api.application.UpdateApplicationRequest;
    import StartApplicationRequest = api.application.StartApplicationRequest;
    import StopApplicationRequest = api.application.StopApplicationRequest;
    import ApplicationEvent = api.application.ApplicationEvent;
    import ApplicationEventType = api.application.ApplicationEventType;

    export class ApplicationBrowsePanel extends api.app.browse.BrowsePanel<api.application.Application> {

        private browseActions: app.browse.ApplicationBrowseActions;

        private applicationTreeGrid: ApplicationTreeGrid;

        private toolbar: ApplicationBrowseToolbar;

        private applicationIconUrl: string;

        constructor() {

            this.applicationTreeGrid = new ApplicationTreeGrid();

            this.browseActions = <app.browse.ApplicationBrowseActions> this.applicationTreeGrid.getContextMenu().getActions();

            this.toolbar = new ApplicationBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new ApplicationBrowseItemPanel();

            super({
                browseToolbar: this.toolbar,
                treeGrid: this.applicationTreeGrid,
                browseItemPanel: browseItemPanel,
                filterPanel: undefined
            });

            this.applicationIconUrl = api.util.UriHelper.getAdminUri('common/images/icons/icoMoon/128x128/puzzle.png');

            this.registerEvents();
        }

        treeNodesToBrowseItems(nodes: TreeNode<Application>[]): BrowseItem<Application>[] {
            var browseItems: BrowseItem<Application>[] = [];

            // do not proceed duplicated content. still, it can be selected
            nodes.forEach((node: TreeNode<Application>, index: number) => {
                for (var i = 0; i <= index; i++) {
                    if (nodes[i].getData().getId() === node.getData().getId()) {
                        break;
                    }
                }
                if (i === index) {
                    var applicationEl = node.getData();
                    var item = new BrowseItem<Application>(applicationEl).
                        setId(applicationEl.getId()).
                        setDisplayName(applicationEl.getDisplayName()).
                        setPath(applicationEl.getName()).
                        setIconUrl(this.applicationIconUrl);
                    browseItems.push(item);
                }
            });
            return browseItems;
        }

        private registerEvents() {
            StopApplicationEvent.on((event: StopApplicationEvent) => {
                var applicationKeys = ApplicationKey.fromApplications(event.getApplications());
                new StopApplicationRequest(applicationKeys).sendAndParse()
                    .then(() => {
                    }).done();
            });
            StartApplicationEvent.on((event: StartApplicationEvent) => {
                var applicationKeys = ApplicationKey.fromApplications(event.getApplications());
                new StartApplicationRequest(applicationKeys).sendAndParse()
                    .then(() => {
                    }).done();
            });

            api.application.ApplicationEvent.on((event: ApplicationEvent) => {
                if (ApplicationEventType.INSTALLED == event.getEventType()) {
                    this.applicationTreeGrid.appendApplicationNode(event.getApplicationKey());
                } else if (ApplicationEventType.UNINSTALLED == event.getEventType()) {
                    this.applicationTreeGrid.deleteApplicationNode(event.getApplicationKey());
                } else if (event.isNeedToUpdateApplication()) {
                    this.applicationTreeGrid.updateApplicationNode(event.getApplicationKey());
                }
            });

        }
    }

}