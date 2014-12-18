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
    import FindPrincipalsRequest = api.security.FindPrincipalsRequest;
    import UserStoreListResult = api.security.UserStoreListResult;
    import UserStoreJson = api.security.UserStoreJson;
    import UserTreeGridItem = app.browse.UserTreeGridItem;
    import Principal = api.security.Principal;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;
    import UserStore = api.security.UserStore;
    import PrincipalType = api.security.PrincipalType;
    import UserStoreKey = api.security.UserStoreKey;
    import UserItemBrowseSearchEvent = app.browse.filter.PrincipalBrowseSearchEvent;
    import UserItemBrowseResetEvent = app.browse.filter.PrincipalBrowseResetEvent;


    export class UserItemsTreeGrid extends TreeGrid<UserTreeGridItem> {

        private treeGridActions: UserTreeGridActions;

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
                setField("modifiedTime").
                setCssClass("modified").
                setMinWidth(150).
                setMaxWidth(170).
                setFormatter(DateTimeFormatter.format).
                build();

            this.treeGridActions = new UserTreeGridActions(this);
            super(new TreeGridBuilder<UserTreeGridItem>().
                    setColumns([
                        nameColumn, modifiedTimeColumn
                    ]).
                    setShowContextMenu(new TreeGridContextMenu(this.treeGridActions)).
                    setPartialLoadEnabled(true).
                    setLoadBufferSize(20). // rows count
                    prependClasses("user-tree-grid")
            );

            UserItemBrowseSearchEvent.on((event) => {
                var principals = event.getPrincipals();

                var items = event.getPrincipals().map((principal: Principal) => {
                    return new app.browse.UserTreeGridItemBuilder().
                        setPrincipal(principal).
                        setType(UserTreeGridItemType.PRINCIPAL).
                        build();
                });
                this.filter(items);
                this.notifyLoaded();
            });

            UserItemBrowseResetEvent.on((event) => {
                this.resetFilter();
            });

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

        getTreeGridActions(): UserTreeGridActions {
            return this.treeGridActions;
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
            var level = parentNode ? parentNode.calcLevel() : 0;
            if (level === 0) {
                // at root level, fetch user stores, and add 'Roles' folder
                new ListUserStoresRequest().sendAndParse().then((userStores: UserStore[]) => {
                    userStores.forEach((userStore: UserStore) => {
                        gridItems.push(new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build());
                    });

                    gridItems.push(new UserTreeGridItemBuilder().setType(UserTreeGridItemType.ROLES).build());

                    deferred.resolve(gridItems);
                });

            } else if (parentNode.getData().getType() === UserTreeGridItemType.ROLES) {
                // fetch roles, if parent node 'Roles' was selected
                new FindPrincipalsRequest().setAllowedTypes([PrincipalType.ROLE]).sendAndParse().
                    then((principals: Principal[]) => {
                        principals.forEach((principal: Principal) => {
                            gridItems.push(new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build());
                        });
                        deferred.resolve(gridItems);
                    });

            } else if (level === 1) {
                // add parent folders 'Users' and 'Groups' to the selected UserStore
                var userStoreNode: app.browse.UserTreeGridItem = parentNode.getData();
                deferred.resolve(this.addUsersGroupsToUserStore(userStoreNode));

            } else if (level === 2) {
                // fetch principals from the user store, if parent node 'Groups' or 'Users' was selected
                var userStoreNode: app.browse.UserTreeGridItem = parentNode.getParent().getData();
                var userStoreKey: UserStoreKey = userStoreNode.getUserStore().getKey();

                var folder: app.browse.UserTreeGridItem = <UserTreeGridItem>parentNode.getData();
                var principalType = this.getPrincipalTypeForFolderItem(folder.getType());

                new FindPrincipalsRequest().setUserStoreKey(userStoreKey).setAllowedTypes([principalType]).sendAndParse().
                    then((principals: Principal[]) => {
                        principals.forEach((principal: Principal) => {
                            gridItems.push(new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build());
                        });
                        deferred.resolve(gridItems);
                    });

            }
            return deferred.promise;
        }

        private getPrincipalTypeForFolderItem(itemType: UserTreeGridItemType): PrincipalType {
            if (itemType === UserTreeGridItemType.GROUPS) {
                return PrincipalType.GROUP;
            } else if (itemType === UserTreeGridItemType.USERS) {
                return PrincipalType.USER;
            } else {
                throw new Error("Invalid item type for folder with principals: " + UserTreeGridItemType[itemType]);
            }
        }

        private addUsersGroupsToUserStore(parentItem: app.browse.UserTreeGridItem): UserTreeGridItem[] {
            var items: UserTreeGridItem[] = [];
            if (parentItem.getType() == UserTreeGridItemType.USER_STORE) {
                var userStore = parentItem.getUserStore();
                var userFolderItem = new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USERS).build();
                var groupFolderItem = new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.GROUPS).build();
                items.push(userFolderItem);
                items.push(groupFolderItem);
            }
            return items;
        }

    }
}
