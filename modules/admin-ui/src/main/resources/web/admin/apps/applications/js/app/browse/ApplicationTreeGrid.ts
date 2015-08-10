module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import Application = api.application.Application;
    import ApplicationViewer = api.application.ApplicationViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;

    export class ApplicationTreeGrid extends TreeGrid<Application> {

        constructor() {
            super(new TreeGridBuilder<Application>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<Application>>().
                            setName("Name").
                            setId("displayName").
                            setField("displayName").
                            setFormatter(this.nameFormatter).
                            setMinWidth(250).
                            build(),

                        new GridColumnBuilder<TreeNode<Application>>().
                            setName("Version").
                            setId("version").
                            setField("version").
                            setCssClass("version").
                            setMinWidth(50).
                            setMaxWidth(70).
                            build(),

                        new GridColumnBuilder<TreeNode<Application>>().
                            setName("State").
                            setId("state").
                            setField("state").
                            setCssClass("state").
                            setMinWidth(80).
                            setMaxWidth(100).
                            build(),

                        new GridColumnBuilder<TreeNode<Application>>().
                            setName("ModifiedTime").
                            setId("modifiedTime").
                            setField("modifiedTime").
                            setCssClass("modified").
                            setMinWidth(150).
                            setMaxWidth(170).
                            setFormatter(DateTimeFormatter.format).
                            build()

                    ]).
                    prependClasses("application-grid").
                    setShowContextMenu(new TreeGridContextMenu(new ApplicationBrowseActions(this)))
            );

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                this.getGrid().resizeCanvas();
            });
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<Application>) {
            var viewer = <ApplicationViewer>node.getViewer("name");
            if (!viewer) {
                var viewer = new ApplicationViewer();
                viewer.setObject(node.getData());
                node.setViewer("name", viewer);
            }
            return viewer.toString();
        }

        getDataId(data: Application): string {
            return data.getId();
        }

        fetchRoot(): wemQ.Promise<Application[]> {
            return new api.application.ListApplicationsRequest().sendAndParse();
        }

        fetch(node: TreeNode<Application>): wemQ.Promise<api.application.Application> {
            return this.fetchByKey(node.getData().getApplicationKey());
        }

        private fetchByKey(applicationKey: api.application.ApplicationKey): wemQ.Promise<api.application.Application> {
            var deferred = wemQ.defer<api.application.Application>();
            new api.application.GetApplicationRequest(applicationKey,
                true).sendAndParse().then((application: api.application.Application)=> {
                    deferred.resolve(application);
                });

            return deferred.promise;
        }

        updateApplicationNode(applicationKey: api.application.ApplicationKey) {
            var root = this.getRoot().getCurrentRoot();
            root.getChildren().forEach((child: TreeNode<Application>) => {
                var curApplication: Application = child.getData();
                if (curApplication.getApplicationKey().toString() == applicationKey.toString()) {
                    this.updateNode(curApplication);
                }
            });
        }

        deleteApplicationNode(applicationKey: api.application.ApplicationKey) {
            var root = this.getRoot().getCurrentRoot();
            root.getChildren().forEach((child: TreeNode<Application>) => {
                var curApplication: Application = child.getData();
                if (curApplication.getApplicationKey().toString() == applicationKey.toString()) {
                    this.deleteNode(curApplication);
                }
            });
        }

        appendApplicationNode(applicationKey: api.application.ApplicationKey) {

            this.fetchByKey(applicationKey)
                .then((data: api.application.Application) => {
                    this.appendNode(data, true);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                });
        }

        refreshNodeData(parentNode: TreeNode<Application>): wemQ.Promise<TreeNode<Application>> {
            return this.fetchByKey(parentNode.getData().getApplicationKey()).then((curApplication: Application) => {
                parentNode.setData(curApplication);
                this.refreshNode(parentNode);
                return parentNode;
            });
        }

    }
}
