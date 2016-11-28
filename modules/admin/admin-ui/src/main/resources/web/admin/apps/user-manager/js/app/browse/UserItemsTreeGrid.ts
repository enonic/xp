import "../../api.ts";
import {UserTreeGridItem, UserTreeGridItemType, UserTreeGridItemBuilder} from "./UserTreeGridItem";
import {UserTreeGridActions} from "./UserTreeGridActions";
import {EditPrincipalEvent} from "./EditPrincipalEvent";
import {PrincipalBrowseResetEvent} from "./filter/PrincipalBrowseResetEvent";
import {PrincipalBrowseSearchEvent} from "./filter/PrincipalBrowseSearchEvent";
import {UserItemsRowFormatter} from "./UserItemsRowFormatter";

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

export class UserItemsTreeGrid extends TreeGrid<UserTreeGridItem> {

    private treeGridActions: UserTreeGridActions;

    constructor() {

        super(new TreeGridBuilder<UserTreeGridItem>().
                setColumnConfig([{
                    name: "Name",
                    id: "name",
                    field:  "displayName",
                    formatter: UserItemsRowFormatter.nameFormatter,
                    style: {minWidth: 250}
                }, {
                    name: "ModifiedTime",
                    id: "modifiedTime",
                    field:  "modifiedTime",
                    formatter: DateTimeFormatter.format,
                    style: {cssClass: "modified", minWidth: 150, maxWidth: 170}
                }]).
                setPartialLoadEnabled(true).
                setLoadBufferSize(20). // rows count
                prependClasses("user-tree-grid")
        );

        this.treeGridActions = new UserTreeGridActions(this);

        this.setContextMenu(new TreeGridContextMenu(this.treeGridActions));

        this.initEventHandlers();
    }

    private initEventHandlers() {
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

    isEmptyNode(node: TreeNode<UserTreeGridItem>): boolean {
        return !node.getDataId() || node.getDataId() == "";
    }

    getTreeGridActions(): UserTreeGridActions {
        return this.treeGridActions;
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

            this.appendNode(userTreeGridItem, true, false, this.getRoot().isFiltered() ? this.getRoot().getDefaultRoot() : null);

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

        parentNode = parentNode || this.getRoot().getCurrentRoot();

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
            return this.loadChildren(parentNode, [PrincipalType.ROLE]);

        } else if (level === 1) {
            // add parent folders 'Users' and 'Groups' to the selected UserStore
            var userStoreNode: UserTreeGridItem = parentNode.getData();
            deferred.resolve(this.addUsersGroupsToUserStore(userStoreNode));

        } else if (level === 2) {
            // fetch principals from the user store, if parent node 'Groups' or 'Users' was selected
            var folder: UserTreeGridItem = <UserTreeGridItem>parentNode.getData();
            var principalType = this.getPrincipalTypeForFolderItem(folder.getType());

            return this.loadChildren(parentNode, [principalType]);
        }
        return deferred.promise;
    }

    private loadChildren(parentNode, allowedTypes): wemQ.Promise<UserTreeGridItem[]> {

        var deferred = wemQ.defer<UserTreeGridItem[]>();

        var from = parentNode.getChildren().length;
        if (from > 0 && !parentNode.getChildren()[from - 1].getData().getDataId()) {
            parentNode.getChildren().pop();
            from--;
        }

        var gridItems: UserTreeGridItem[] = parentNode.getChildren().map((el) => {
            return el.getData();
        }).slice(0, from);

        // fetch principals from the user store, if parent node 'Groups' or 'Users' was selected
        if(parentNode.getData().getType() != UserTreeGridItemType.ROLES) {
            var userStoreNode: UserTreeGridItem = parentNode.getParent().getData();
            var userStoreKey: UserStoreKey = userStoreNode.getUserStore().getKey();
        }

        new FindPrincipalsRequest().setUserStoreKey(userStoreKey).
            setAllowedTypes(allowedTypes).
            setFrom(from).
            setSize(10).
            sendAndParse().then(
            (result) => {
                let principals = result.getPrincipals();

                principals.forEach((principal: Principal) => {
                    gridItems.push(
                        new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build());
                });

                if (from + principals.length < result.getTotalSize()) {
                    gridItems.push(UserTreeGridItem.create().build());
                }

                deferred.resolve(gridItems);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

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
