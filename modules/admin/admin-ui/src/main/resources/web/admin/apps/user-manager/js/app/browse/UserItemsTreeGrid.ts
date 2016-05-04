import "../../api.ts";

import GridColumn = api.ui.grid.GridColumn;
import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
import TreeGrid = api.ui.treegrid.TreeGrid;
import TreeNode = api.ui.treegrid.TreeNode;
import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;

import ListUserStoresRequest = api.security.ListUserStoresRequest;
import FindPrincipalsRequest = api.security.FindPrincipalsRequest;
import UserStoreListResult = api.security.UserStoreListResult;
import UserStoreJson = api.security.UserStoreJson;
import Principal = api.security.Principal;
import UserStore = api.security.UserStore;
import PrincipalType = api.security.PrincipalType;
import UserStoreKey = api.security.UserStoreKey;
import {UserTreeGridItem, UserTreeGridItemType, UserTreeGridItemBuilder} from "./UserTreeGridItem";
import {UserTreeGridActions} from "./UserTreeGridActions";
import {UserTreeGridItemViewer} from "./UserTreeGridItemViewer";
import {EditPrincipalEvent} from "./EditPrincipalEvent";
import {PrincipalBrowseResetEvent} from "./filter/PrincipalBrowseResetEvent";
import {PrincipalBrowseSearchEvent} from "./filter/PrincipalBrowseSearchEvent";

export class UserItemsTreeGrid extends TreeGrid<UserTreeGridItem> {

    private treeGridActions: UserTreeGridActions;

    constructor() {

        this.treeGridActions = new UserTreeGridActions(this);
        super(new TreeGridBuilder<UserTreeGridItem>().setColumns([
                this.getNameColumn(), this.getModifiedTimeColumn()
            ]).setShowContextMenu(new TreeGridContextMenu(this.treeGridActions)).setPartialLoadEnabled(true).setLoadBufferSize(
            20). // rows count
            prependClasses("user-tree-grid")
        );

        this.subscribeEvents();
    }

    private getNameColumn(): GridColumn<TreeNode<UserTreeGridItem>> {
        return new GridColumnBuilder<TreeNode<UserTreeGridItem>>().setName("Name").setId("name").setField("displayName").setFormatter(
            this.nameFormatter).setMinWidth(250).build();
    }

    private getModifiedTimeColumn(): GridColumn<TreeNode<UserTreeGridItem>> {
        return new GridColumnBuilder<TreeNode<UserTreeGridItem>>().setName("ModifiedTime").setId("modifiedTime").setField(
            "modifiedTime").setCssClass("modified").setMinWidth(150).setMaxWidth(170).setFormatter(DateTimeFormatter.format).build();
    }

    private subscribeEvents() {
        PrincipalBrowseSearchEvent.on((event) => {
            var items = event.getPrincipals().map((principal: Principal) => {
                return new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build();
            });
            this.filter(items);
            this.notifyLoaded();
        });

        PrincipalBrowseResetEvent.on((event) => {
            this.resetFilter();
        });

        api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
            this.getGrid().resizeCanvas();
        });

        this.getGrid().subscribeOnDblClick((event, data) => {

            if (this.isActive()) {
                var node = this.getGrid().getDataView().getItem(data.row);
                if (this.isUserItemEditable(node.getData())) {
                    new EditPrincipalEvent([node.getData()]).fire();
                }
            }
        });
    }

    private isUserItemEditable(userItem: UserTreeGridItem): boolean {

        var type: UserTreeGridItemType = userItem.getType();

        if (type == UserTreeGridItemType.ROLES || type == UserTreeGridItemType.GROUPS || type == UserTreeGridItemType.USERS) {
            return false;
        }

        return true;
    }

    private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<UserTreeGridItem>) {
        var viewer = <UserTreeGridItemViewer>node.getViewer("displayName");
        if (!viewer) {
            var viewer = new UserTreeGridItemViewer();
            viewer.setObject(node.getData(), node.calcLevel() > 1);
            node.setViewer("displayName", viewer);
        }
        return viewer.toString();
    }

    getTreeGridActions(): UserTreeGridActions {
        return this.treeGridActions;
    }

    private resolveUserTreeGridItemType(principal: Principal) {
        if (!principal) {
            return UserTreeGridItemType.USER_STORE;
        } else {
            switch (principal.getType()) {
            case PrincipalType.USER:
                return UserTreeGridItemType.USERS;
            case PrincipalType.GROUP:
                return UserTreeGridItemType.GROUPS;
            case PrincipalType.ROLE:
                return UserTreeGridItemType.ROLES;
            default:
                return UserTreeGridItemType.PRINCIPAL;
            }
        }
    }

    updateUserNode(principal: api.security.Principal, userStore: api.security.UserStore) {
        var userTreeGridItem,
            builder = new UserTreeGridItemBuilder();

        if (!principal) { // UserStore type
            userTreeGridItem = builder.setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build();
        } else {         // Principal type
            userTreeGridItem = builder.setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build();
        }

        var nodeList = this.getRoot().getCurrentRoot().treeToList();

        nodeList.forEach((node) => {
            if (node.getDataId() === userTreeGridItem.getDataId()) {
                node.setData(userTreeGridItem);
                node.clearViewers();
            }
        });

        this.initData(nodeList);
        this.invalidate();
    }

    appendUserNode(principal: api.security.Principal, userStore: api.security.UserStore, parentOfSameType?: boolean) {
        if (!principal) { // UserStore type

            var userTreeGridItem = new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build();

            // Remove roles from the end to add them lately
            var children = this.getRoot().getDefaultRoot().getChildren(),
                roles = children.pop();

            this.appendNode(userTreeGridItem, true, false);

            children.push(roles);

            if (!this.getRoot().isFiltered()) {
                this.initData(this.getRoot().getDefaultRoot().treeToList());
                this.invalidate();
            }

        } else { // Principal type

            var userTreeGridItem = new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build();

            this.appendNode(userTreeGridItem, parentOfSameType, false);

        }
    }

    deleteUserNodes(principals: api.security.Principal[], userStores: api.security.UserStore[]) {
        if (principals) {
            var principalItems = principals.map((principal) => {
                return new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build();
            });

            this.deleteNodes(principalItems);
        }
        if (userStores) {
            var userStoreItems = userStores.map((userStore) => {
                return new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build();
            });

            this.deleteNodes(userStoreItems);
        }
    }

    getDataId(item: UserTreeGridItem): string {
        return item.getDataId();
    }


    hasChildren(item: UserTreeGridItem): boolean {
        return item.hasChildren();
    }

    fetchChildren(parentNode?: TreeNode<UserTreeGridItem>): wemQ.Promise<UserTreeGridItem[]> {
        var gridItems: UserTreeGridItem[] = [];

        var deferred = wemQ.defer<UserTreeGridItem[]>();
        var level = parentNode ? parentNode.calcLevel() : 0;

        // Creating a role with parent node pointing to another role may cause fetching to fail
        // We need to select a parent node first
        if (level !== 0 && parentNode.getData().getPrincipal() &&
            parentNode.getData().getType() === UserTreeGridItemType.PRINCIPAL &&
            parentNode.getData().getPrincipal().isRole() && !!parentNode.getParent()) {

            parentNode = parentNode.getParent();
            level--;
        }

        if (level === 0) {
            // at root level, fetch user stores, and add 'Roles' folder
            new ListUserStoresRequest().sendAndParse().then((userStores: UserStore[]) => {
                userStores.forEach((userStore: UserStore) => {
                    gridItems.push(new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build());
                });

                gridItems.push(new UserTreeGridItemBuilder().setType(UserTreeGridItemType.ROLES).build());

                deferred.resolve(gridItems);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

        } else if (parentNode.getData().getType() === UserTreeGridItemType.ROLES) {
            // fetch roles, if parent node 'Roles' was selected
            new FindPrincipalsRequest().setAllowedTypes([PrincipalType.ROLE]).sendAndParse().then((principals: Principal[]) => {
                principals.forEach((principal: Principal) => {
                    gridItems.push(new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build());
                });
                deferred.resolve(gridItems);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

        } else if (level === 1) {
            // add parent folders 'Users' and 'Groups' to the selected UserStore
            var userStoreNode: UserTreeGridItem = parentNode.getData();
            deferred.resolve(this.addUsersGroupsToUserStore(userStoreNode));

        } else if (level === 2) {
            // fetch principals from the user store, if parent node 'Groups' or 'Users' was selected
            var userStoreNode: UserTreeGridItem = parentNode.getParent().getData();
            var userStoreKey: UserStoreKey = userStoreNode.getUserStore().getKey();

            var folder: UserTreeGridItem = <UserTreeGridItem>parentNode.getData();
            var principalType = this.getPrincipalTypeForFolderItem(folder.getType());

            new FindPrincipalsRequest().setUserStoreKey(userStoreKey).setAllowedTypes([principalType]).sendAndParse().then(
                (principals: Principal[]) => {
                    principals.forEach((principal: Principal) => {
                        gridItems.push(
                            new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build());
                    });
                    deferred.resolve(gridItems);
                }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

        }
        return deferred.promise;
    }

    refreshNodeData(parentNode: TreeNode<UserTreeGridItem>): wemQ.Promise<TreeNode<UserTreeGridItem>> {
        var deferred = Q.defer<TreeNode<UserTreeGridItem>>();
        deferred.resolve(parentNode);

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

    private addUsersGroupsToUserStore(parentItem: UserTreeGridItem): UserTreeGridItem[] {
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
