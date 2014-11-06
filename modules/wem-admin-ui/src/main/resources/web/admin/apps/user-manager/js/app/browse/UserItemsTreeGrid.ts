module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
    import UserTreeGridItemViewer = api.security.UserTreeGridItemViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import UserTreeGridItemBuilder = api.security.UserTreeGridItemBuilder;
    import UserTreeGridItemType = api.security.UserTreeGridItemType;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import ListUserStoresRequest = api.security.ListUserStoresRequest;
    import GetPrincipalsByUserStoreRequest = api.security.GetPrincipalsByUserStoreRequest;
    import UserStoreListResult = api.security.UserStoreListResult;
    import UserStoreJson = api.security.UserStoreJson;
    import UserTreeGridItem = api.security.UserTreeGridItem;
    import Principal = api.security.Principal;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;
    import UserStore = api.security.UserStore;
    import PrincipalType = api.security.PrincipalType;
    import UserStoreKey = api.security.UserStoreKey;


    export class UserItemsTreeGrid extends TreeGrid<UserTreeGridItem> {

        constructor() {

            var nameColumn = new GridColumnBuilder<TreeNode<UserTreeGridItem>>().
                setName("Name").
                setId("name").
                setField("displayName").
                setFormatter(this.nameFormatter).
                setMinWidth(250).
                build();
            var modifiedTimeColumn = new GridColumnBuilder<TreeNode<UserTreeGridItem>>().
                setName("ModifiedTime").
                setId("modifiedTime").
                setField("userItem.modifiedTime").
                setCssClass("modified").
                setMinWidth(150).
                setMaxWidth(170).
                setFormatter(DateTimeFormatter.format).
                build();

            super(new TreeGridBuilder<UserTreeGridItem>().
                    setColumns([
                        nameColumn, modifiedTimeColumn
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
            return item.getDataId();
        }


        hasChildren(item: UserTreeGridItem): boolean {
            //return item.hasChildren(); ?
            //TODO implement it
            return true;
        }

        fetchChildren(parentNode?: TreeNode<UserTreeGridItem>): wemQ.Promise<UserTreeGridItem[]> {
            var gridItems: UserTreeGridItem[] = [];

            var deferred = wemQ.defer<UserTreeGridItem[]>();
            //if there are no parents so need to call a getUserStoresRequest
            if (!parentNode) {
                (new ListUserStoresRequest()).sendAndParse().then((userStores: UserStore[]) => {
                    //  var userStores:UserTreeGridItem[] = [];
                    userStores.forEach((userStore: UserStore) => {
                        gridItems.push(new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build());
                    });

                    deferred.resolve(gridItems);
                });

                // fetch principals if selected a parent node.
            } else if (parentNode.calcLevel() == 2) {       //
                // add user, group folders
                var userStore: UserStoreKey = new UserStoreKey('system');
                var principalType: PrincipalType = PrincipalType.GROUP;
                (new GetPrincipalsByUserStoreRequest(userStore, principalType)).sendAndParse().then((principals: Principal[]) => {
                    principals.forEach((principal: Principal) => {
                        gridItems.push(new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build());
                    });
                    deferred.resolve(gridItems);
                });

            }
            return deferred.promise;
        }

    }
}
