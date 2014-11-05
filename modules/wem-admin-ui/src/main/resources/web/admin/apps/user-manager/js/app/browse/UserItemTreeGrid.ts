module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
    import UserTreeGridItemViewer = api.security.UserTreeGridItemViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import ListUserStoresRequest = api.security.ListUserStoresRequest;
    import UserStoreListResult = api.security.UserStoreListResult;
    import UserStoreJson = api.security.UserStoreJson;
    import UserTreeGridItem = api.security.UserTreeGridItem;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;


    export class UserItemTreeGrid extends TreeGrid<UserTreeGridItem> {

        constructor() {

            var nameColumn = new GridColumnBuilder<TreeNode<UserTreeGridItem>>().
                setName("Name").
                setId("name").
                setField("displayName").
                setFormatter(this.nameFormatter).
                setMinWidth(250).
                build();

            super(new TreeGridBuilder<UserTreeGridItem>().
                    setColumns([
                        nameColumn
                    ]).
                    setShowContextMenu(new TreeGridContextMenu(new UserBrowseActions(this))).
                    setPartialLoadEnabled(true).
                    setLoadBufferSize(20). // rows count
                    prependClasses("user-tree-grid")
            );

        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<UserTreeGridItem>) {
            var viewer = <UserTreeGridItemViewer>node.getViewer("displayName");
            if (!viewer) {
                var viewer = new UserTreeGridItemViewer();
                viewer.setObject(node.getData());
                node.setViewer("displayName", viewer);
            }
            return viewer.toString();
        }


        getDataId(item: UserTreeGridItem): string {
            return item.getId();
        }


        hasChildren(item: UserTreeGridItem): boolean {
            //return item.hasChildren(); ?
            //TODO implement it
            return true;
        }

        fetchChildren(parentNode?: TreeNode<UserTreeGridItem>): wemQ.Promise<UserTreeGridItem[]> {

            //getUserStoresRequest
            if (!parentNode) {
                return new ListUserStoresRequest().sendAndParse();

            } else if (parentNode.calcLevel()) {
                // add user, group folders
            } else if (parentNode.calcLevel() == 2) {

            }
        }

    }
}
