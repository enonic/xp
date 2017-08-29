import '../../api.ts';
import {UserTreeGridItem, UserTreeGridItemType, UserTreeGridItemBuilder} from './UserTreeGridItem';
import {UserTreeGridActions} from './UserTreeGridActions';
import {EditPrincipalEvent} from './EditPrincipalEvent';
import {UserItemsRowFormatter} from './UserItemsRowFormatter';

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
import BrowseFilterResetEvent = api.app.browse.filter.BrowseFilterResetEvent;
import BrowseFilterSearchEvent = api.app.browse.filter.BrowseFilterSearchEvent;
import i18n = api.util.i18n;
import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

export class UserItemsTreeGrid extends TreeGrid<UserTreeGridItem> {

    private treeGridActions: UserTreeGridActions;

    constructor() {

        const builder = new TreeGridBuilder<UserTreeGridItem>().setColumnConfig([{
                name: i18n('field.name'),
                id: 'name',
                field: 'displayName',
                formatter: UserItemsRowFormatter.nameFormatter,
            style: {minWidth: 200}
        }]).setPartialLoadEnabled(true).setLoadBufferSize(20).prependClasses('user-tree-grid');

        const columns = builder.getColumns().slice(0);
        const [nameColumn] = columns;

        const updateColumns = () => {
            let checkSelIsMoved = ResponsiveRanges._360_540.isFitOrSmaller(api.dom.Body.get().getEl().getWidth());

            const curClass = nameColumn.getCssClass();

            if (checkSelIsMoved) {
                nameColumn.setCssClass(curClass || 'shifted');
            } else if (curClass && curClass.indexOf('shifted') >= 0) {
                nameColumn.setCssClass(curClass.replace('shifted', ''));
            }

            this.setColumns(columns.slice(0), checkSelIsMoved);
        };

        builder.setColumnUpdater(updateColumns);

        super(builder);

        this.treeGridActions = new UserTreeGridActions(this);

        this.setContextMenu(new TreeGridContextMenu(this.treeGridActions));

        this.initEventHandlers();
    }

    private initEventHandlers() {
        BrowseFilterSearchEvent.on((event) => {
            let items = event.getData().map((principal: Principal) => {
                return new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build();
            });
            this.filter(items);
            this.notifyLoaded();
        });

        BrowseFilterResetEvent.on(() => {
            this.resetFilter();
        });

        this.getGrid().subscribeOnDblClick((event, data) => {

            if (this.isActive()) {
                let node = this.getGrid().getDataView().getItem(data.row);
                this.editItem(node);
            }
        });
    }

    protected editItem(node: TreeNode<UserTreeGridItem>) {
        if (this.isUserItemEditable(node.getData())) {
            new EditPrincipalEvent([node.getData()]).fire();
        }
    }

    private isUserItemEditable(userItem: UserTreeGridItem): boolean {

        let type: UserTreeGridItemType = userItem.getType();

        if (type === UserTreeGridItemType.ROLES || type === UserTreeGridItemType.GROUPS || type === UserTreeGridItemType.USERS) {
            return false;
        }

        return true;
    }

    isEmptyNode(node: TreeNode<UserTreeGridItem>): boolean {
        return !node.getDataId() || node.getDataId() === '';
    }

    getTreeGridActions(): UserTreeGridActions {
        return this.treeGridActions;
    }

    updateUserNode(principal: api.security.Principal, userStore: api.security.UserStore) {
        let userTreeGridItem;
        let builder = new UserTreeGridItemBuilder();

        if (!principal) { // UserStore type
            userTreeGridItem = builder.setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build();
        } else {         // Principal type
            userTreeGridItem = builder.setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build();
        }

        let nodeList = this.getRoot().getCurrentRoot().treeToList();

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

            const userTreeGridItem = new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USER_STORE).build();

            this.appendNode(userTreeGridItem, true, false, this.getRoot().isFiltered() ? this.getRoot().getDefaultRoot() : null);

            if (!this.getRoot().isFiltered()) {
                this.initData(this.getRoot().getDefaultRoot().treeToList());
                this.invalidate();
            }

        } else { // Principal type

            const userTreeGridItem = new UserTreeGridItemBuilder().setPrincipal(principal).setType(UserTreeGridItemType.PRINCIPAL).build();

            this.appendNode(userTreeGridItem, parentOfSameType, false);

            // If a new principal is added to the root it means it was added from the New Principal modal dialog,
            // in which case we have to reload the tree so that the new node is placed correctly
            if (this.getParentNode(parentOfSameType) == this.getRoot().getCurrentRoot()) {
                this.reload();
            }
        }
    }

    deleteNodes(userTreeGridItemsToDelete: UserTreeGridItem[]) {
        if (this.isSingleItemSelected() && this.isHighlightedItemIn(userTreeGridItemsToDelete)) {
            this.removeHighlighting();
        }

        super.deleteNodes(userTreeGridItemsToDelete);
    }

    getParentNode(nextToSelection: boolean = false, stashedParentNode?: TreeNode<UserTreeGridItem>, data?: UserTreeGridItem) {
        const parent = super.getParentNode(nextToSelection, stashedParentNode);
        if (parent.getData().getType() === UserTreeGridItemType.USER_STORE && parent.hasChildren()) {
            const parentType = (data && data.getPrincipal().isUser()) ? UserTreeGridItemType.USERS : UserTreeGridItemType.GROUPS;
            return parent.getChildren().filter(node => node.getData().getType() === parentType)[0] || parent;
        }

        return parent;
    }

    protected updateSelectedNode(node: TreeNode<UserTreeGridItem>) {
        // Highlighted nodes should remain as is, and must not be selected
        const firstSelectedOrHighlighted = this.getFirstSelectedOrHighlightedNode();
        const highlighted = this.getRoot().getFullSelection().length === 0 && !!firstSelectedOrHighlighted;

        const usersAndGroupsTypes = [UserTreeGridItemType.USERS, UserTreeGridItemType.GROUPS];
        const usersOrGroupsUpdating = usersAndGroupsTypes.some(type => type === node.getData().getType());
        const selectedData = this.getSelectedDataList()[0];
        const userStoreSelected = selectedData && selectedData.getType() === UserTreeGridItemType.USER_STORE;

        const nodeToUpdate = (usersOrGroupsUpdating && userStoreSelected) ? node.getParent() : node;
        if (highlighted) {
            this.refreshNode(nodeToUpdate);
        } else {
            super.updateSelectedNode(nodeToUpdate);
        }
    }

    getDataId(item: UserTreeGridItem): string {
        return item.getDataId();
    }

    hasChildren(item: UserTreeGridItem): boolean {
        return item.hasChildren();
    }

    fetchChildren(parentNode?: TreeNode<UserTreeGridItem>): wemQ.Promise<UserTreeGridItem[]> {
        let gridItems: UserTreeGridItem[] = [];

        parentNode = parentNode || this.getRoot().getCurrentRoot();

        let deferred = wemQ.defer<UserTreeGridItem[]>();
        let level = parentNode ? parentNode.calcLevel() : 0;

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
            let userStoreNode: UserTreeGridItem = parentNode.getData();
            deferred.resolve(this.addUsersGroupsToUserStore(userStoreNode));

        } else if (level === 2) {
            // fetch principals from the user store, if parent node 'Groups' or 'Users' was selected
            let folder: UserTreeGridItem = <UserTreeGridItem>parentNode.getData();
            let principalType = this.getPrincipalTypeForFolderItem(folder.getType());

            return this.loadChildren(parentNode, [principalType]);
        }
        return deferred.promise;
    }

    private loadChildren(parentNode: TreeNode<UserTreeGridItem>, allowedTypes: PrincipalType[]): wemQ.Promise<UserTreeGridItem[]> {

        let deferred = wemQ.defer<UserTreeGridItem[]>();

        let from = parentNode.getChildren().length;
        if (from > 0 && !parentNode.getChildren()[from - 1].getData().getDataId()) {
            parentNode.getChildren().pop();
            from--;
        }

        let gridItems: UserTreeGridItem[] = parentNode.getChildren().map((el) => {
            return el.getData();
        }).slice(0, from);

        let userStoreNode: UserTreeGridItem = null;
        let userStoreKey: UserStoreKey = null;
        // fetch principals from the user store, if parent node 'Groups' or 'Users' was selected
        if(parentNode.getData().getType() !== UserTreeGridItemType.ROLES) {
            userStoreNode = parentNode.getParent().getData();
            userStoreKey = userStoreNode.getUserStore().getKey();
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
        let deferred = Q.defer<TreeNode<UserTreeGridItem>>();
        deferred.resolve(parentNode);

        return deferred.promise;
    }

    private getPrincipalTypeForFolderItem(itemType: UserTreeGridItemType): PrincipalType {
        if (itemType === UserTreeGridItemType.GROUPS) {
            return PrincipalType.GROUP;
        } else if (itemType === UserTreeGridItemType.USERS) {
            return PrincipalType.USER;
        } else {
            throw new Error('Invalid item type for folder with principals: ' + UserTreeGridItemType[itemType]);
        }
    }

    private addUsersGroupsToUserStore(parentItem: UserTreeGridItem): UserTreeGridItem[] {
        let items: UserTreeGridItem[] = [];
        if (parentItem.getType() === UserTreeGridItemType.USER_STORE) {
            let userStore = parentItem.getUserStore();
            let userFolderItem = new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.USERS).build();
            let groupFolderItem = new UserTreeGridItemBuilder().setUserStore(userStore).setType(UserTreeGridItemType.GROUPS).build();
            items.push(userFolderItem);
            items.push(groupFolderItem);
        }
        return items;
    }

    private isSingleItemSelected(): boolean {
        return this.getSelectedDataList().length === 1;
    }

    private isHighlightedItemIn(userTreeGridItems: UserTreeGridItem[]): boolean {
        return userTreeGridItems.some((userTreeGridItem: UserTreeGridItem) => {
            return userTreeGridItem.getDataId() === this.getFirstSelectedOrHighlightedNode().getDataId();
        });
    }

}
