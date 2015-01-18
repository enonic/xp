module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import Module = api.module.Module;
    import ModuleViewer = api.module.ModuleViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;

    export class ModuleTreeGrid extends TreeGrid<Module> {

        constructor() {
            super(new TreeGridBuilder<Module>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<Module>>().
                            setName("Name").
                            setId("displayName").
                            setField("displayName").
                            setFormatter(this.nameFormatter).
                            setMinWidth(250).
                            build(),

                        new GridColumnBuilder<TreeNode<Module>>().
                            setName("Version").
                            setId("version").
                            setField("version").
                            setCssClass("version").
                            setMinWidth(50).
                            setMaxWidth(70).
                            build(),

                        new GridColumnBuilder<TreeNode<Module>>().
                            setName("State").
                            setId("state").
                            setField("state").
                            setCssClass("state").
                            setMinWidth(80).
                            setMaxWidth(100).
                            build(),

                        new GridColumnBuilder<TreeNode<Module>>().
                            setName("ModifiedTime").
                            setId("modifiedTime").
                            setField("modifiedTime").
                            setCssClass("modified").
                            setMinWidth(150).
                            setMaxWidth(170).
                            setFormatter(DateTimeFormatter.format).
                            build()

                    ]).prependClasses("module-grid")
            );
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<Module>) {
            var viewer = <ModuleViewer>node.getViewer("name");
            if (!viewer) {
                var viewer = new ModuleViewer();
                viewer.setObject(node.getData());
                node.setViewer("name", viewer);
            }
            return viewer.toString();
        }

        getDataId(data: Module): string {
            return data.getId();
        }

        fetchRoot(): wemQ.Promise<Module[]> {
            return new api.module.ListModulesRequest().sendAndParse();
        }

        fetch(node: TreeNode<Module>): wemQ.Promise<api.module.Module> {
            return this.fetchByKey(node.getData().getModuleKey());
        }

        private fetchByKey(moduleKey: api.module.ModuleKey): wemQ.Promise<api.module.Module> {
            var deferred = wemQ.defer<api.module.Module>();
            new api.module.GetModuleRequest(moduleKey, true).sendAndParse().then((modulee: api.module.Module)=> {
                deferred.resolve(modulee);
            });

            return deferred.promise;
        }

        updateModuleNode(moduleKey: api.module.ModuleKey) {
            var root = this.getRoot().getCurrentRoot();
            root.getChildren().forEach((child: TreeNode<Module>) => {
                var curModule: Module = child.getData();
                if (curModule.getModuleKey().toString() == moduleKey.toString()) {
                    this.updateNode(curModule);
                }
            });
        }

        deleteModuleNode(moduleKey: api.module.ModuleKey) {
            var root = this.getRoot().getCurrentRoot();
            root.getChildren().forEach((child: TreeNode<Module>) => {
                var curModule: Module = child.getData();
                if (curModule.getModuleKey().toString() == moduleKey.toString()) {
                    this.deleteNode(curModule);
                }
            });
        }

        appendModuleNode(moduleKey: api.module.ModuleKey) {

            this.fetchByKey(moduleKey)
                .then((data: api.module.Module) => {
               this.appendNode(data);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            });
        }

    }
}
