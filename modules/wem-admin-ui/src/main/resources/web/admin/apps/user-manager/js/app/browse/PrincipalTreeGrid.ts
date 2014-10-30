module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import Principal = api.security.Principal;
    import PrincipalViewer = api.security.PrincipalViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;

    export class PrincipalTreeGrid extends TreeGrid<Principal> {

        constructor() {
            super(new TreeGridBuilder<Principal>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<Principal>>().
                            setName("Name").
                            setId("name").
                            setField("displayName").
                            setFormatter(this.nameFormatter).
                            setMinWidth(250).
                            build(),

                        new GridColumnBuilder<TreeNode<Principal>>().
                            setName("key").
                            setId("key").
                            setField("key").
                            setCssClass("version").
                            setMinWidth(50).
                            setMaxWidth(70).
                            build(),


                    ]).prependClasses("principal-grid")
            );
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<Principal>) {
            var viewer = <PrincipalViewer>node.getViewer("displayName");
            if (!viewer) {
                var viewer = new PrincipalViewer();
                viewer.setObject(node.getData());
                node.setViewer("displayName", viewer);
            }
            return viewer.toString();
        }

        getKey(data: Principal): string {
            return data.getKey().toString();
        }

    }
}
