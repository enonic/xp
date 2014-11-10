module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
    import UserTreeGridItemViewer = app.browse.UserTreeGridItemViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import UserTreeGridItemBuilder = app.browse.UserTreeGridItemBuilder;
    import UserTreeGridItemType = app.browse.UserTreeGridItemType;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import ListUserStoresRequest = api.security.ListUserStoresRequest;
    import GetPrincipalsByUserStoreRequest = api.security.GetPrincipalsByUserStoreRequest;
    import UserStoreListResult = api.security.UserStoreListResult;
    import UserStoreJson = api.security.UserStoreJson;
    import UserTreeGridItem = app.browse.UserTreeGridItem;
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


        getDataId(item: app.browse.UserTreeGridItem): string {
            return item.getDataId();
        }


        hasChildren(item: app.browse.UserTreeGridItem): boolean {
            return item.hasChildren();
        }

        fetchChildren(parentNode?: TreeNode<UserTreeGridItem>): wemQ.Promise<UserTreeGridItem[]> {
            var gridItems: UserTreeGridItem[] = [];

            var deferred = wemQ.defer<UserTreeGridItem[]>();
            //1. if there are no parents so need to call a getUserStoresRequest and fetch all UserStores
            if (!parentNode) {
                (new ListUserStoresRequest()).sendAndParse().then((userStores: UserStore[]) => {
                    //  var userStores:UserTreeGridItem[] = [];
                    userStores.forEach((userStore: UserStore) => {
                        gridItems.push(new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build());
                    });

                    deferred.resolve(gridItems);
                });

                // 2. add parent folders (Users, Roles and Groups) to the selected UserStore
            } else if (parentNode.calcLevel() == 1) {
                var userStoreNode: app.browse.UserTreeGridItem = <UserTreeGridItem>parentNode.getData();
                deferred.resolve(this.addUsersGroupsRolesToUserStore(userStoreNode));
            }
            // 3. fetch principals from the userStore, if parent node(Groups or Users or Roles) was selected
            else if (parentNode.calcLevel() == 2) {
                var userStoreNode: app.browse.UserTreeGridItem = <UserTreeGridItem>parentNode.getParent().getData();
                var userStoreKey: UserStoreKey = userStoreNode.getUserStore().getKey();

                var folder: app.browse.UserTreeGridItem = <UserTreeGridItem>parentNode.getData();
                (new GetPrincipalsByUserStoreRequest(userStoreKey,
                    this.selectPrincipalType(folder.getItemDisplayName()))).sendAndParse().then((principals: Principal[]) => {
                    principals.forEach((principal: Principal) => {
                        gridItems.push(new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build());
                    });
                    deferred.resolve(gridItems);
                });

            }
            return deferred.promise;
        }

        private selectPrincipalType(itemDisaplayName: string): PrincipalType {
            if (itemDisaplayName == 'Groups') {
                return PrincipalType.GROUP;
            } else if (itemDisaplayName == "Users") {
                return PrincipalType.USER;
            } else if (itemDisaplayName == "Roles") {
                return PrincipalType.ROLE;
            } else {
                throw new Error("Wrong item-display name for folder with principals: " + itemDisaplayName);
            }
        }

        private addUsersGroupsRolesToUserStore(parentItem: app.browse.UserTreeGridItem): UserTreeGridItem[] {
            var items: UserTreeGridItem[] = [];
            if (parentItem.getType() == UserTreeGridItemType.USER_STORE) {
                items.push(new app.browse.UserTreeGridItemBuilder().setUserStore(parentItem.getUserStore()).setType(UserTreeGridItemType.GROUPS).build());
                items.push(new UserTreeGridItemBuilder().setUserStore(parentItem.getUserStore()).setType(UserTreeGridItemType.USERS).build());
                if (parentItem.getUserStore().getKey().toString() == "system") {
                    items.push(new UserTreeGridItemBuilder().setUserStore(parentItem.getUserStore()).setType(UserTreeGridItemType.ROLES).build());
                }
            }
            return items;
        }

    }
}
