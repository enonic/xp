import '../../api.ts';
import {UserTypeTreeGridItem, UserTypeTreeGridItemBuilder} from './UserTypeTreeGridItem';
import {UserItemTypesRowFormatter} from './UserItemTypesRowFormatter';
import {NewPrincipalEvent} from '../browse/NewPrincipalEvent';
import {UserTreeGridItemBuilder, UserTreeGridItemType} from '../browse/UserTreeGridItem';

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
import PrincipalKey = api.security.PrincipalKey;
import UserStore = api.security.UserStore;
import PrincipalType = api.security.PrincipalType;
import UserStoreKey = api.security.UserStoreKey;
import BrowseFilterResetEvent = api.app.browse.filter.BrowseFilterResetEvent;
import BrowseFilterSearchEvent = api.app.browse.filter.BrowseFilterSearchEvent;
import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
import PrincipalBuilder = api.security.PrincipalBuilder;
import UserItemBuilder = api.security.UserItemBuilder;
import UserStoreBuilder = api.security.UserStoreBuilder;
import UserBuilder = api.security.UserBuilder;
import GroupBuilder = api.security.GroupBuilder;
import RoleBuilder = api.security.RoleBuilder;
import i18n = api.util.i18n;
import Role = api.security.Role;
import User = api.security.User;
import Group = api.security.Group;

export class UserItemTypesTreeGrid extends TreeGrid<UserTypeTreeGridItem> {

    private userStores: UserStore[];

    constructor() {

        const builder = new TreeGridBuilder<UserTypeTreeGridItem>().setColumnConfig([{
            name: i18n('field.name'),
            id: 'name',
            field: 'displayName',
            formatter: UserItemTypesRowFormatter.nameFormatter,
            style: {}
        }]).setPartialLoadEnabled(false)
            .setShowToolbar(false)
            .disableMultipleSelection(true)
            .setCheckableRows(false)
            .prependClasses('user-types-tree-grid');

        super(builder);

        this.initEventHandlers();
    }

    private initEventHandlers() {
        this.getGrid().subscribeOnClick((event, data) => {
            const node = this.getGrid().getDataView().getItem(data.row);
            const userItem = node.getData().getUserItem();
            if (node.getData().hasChildren()) {
                this.expandNode(node);
            } else {
                const isRootNode = node.calcLevel() === 1;
                if (userItem instanceof UserStore) {
                    if (isRootNode) {
                        new NewPrincipalEvent([new UserTreeGridItemBuilder().setType(UserTreeGridItemType.USER_STORE).build()]).fire();
                    } else if (node.getParent().getData().getUserItem() instanceof User) {
                        const item = new UserTreeGridItemBuilder().setUserStore(userItem).setType(UserTreeGridItemType.USERS).build();
                        new NewPrincipalEvent([item]).fire();
                    } else if (node.getParent().getData().getUserItem() instanceof Group) {
                        const item = new UserTreeGridItemBuilder().setUserStore(userItem).setType(UserTreeGridItemType.GROUPS).build();
                        new NewPrincipalEvent([item]).fire();
                    }
                } else if (userItem instanceof Role) {
                    new NewPrincipalEvent([new UserTreeGridItemBuilder().setType(UserTreeGridItemType.ROLES).build()]).fire();
                }
                // close
            }
        });
    }

    resetCache() {
        this.reload(null, null, false);
        this.userStores = null;
    }

    fetchUserStores(): wemQ.Promise<UserStore[]> {
        if (this.userStores) {
            return wemQ.resolve(this.userStores);
        }

        return new ListUserStoresRequest().sendAndParse().then((userStores: UserStore[]) => {
            this.userStores = userStores;
            return userStores;
        });
    }

    getDataId(data: UserTypeTreeGridItem): string {
        return data.getId();
    }

    hasChildren(item: UserTypeTreeGridItem): boolean {
        return item.hasChildren();
    }

    fetchRoot(): wemQ.Promise<UserTypeTreeGridItem[]> {
        return wemQ.resolve([
            new UserTypeTreeGridItemBuilder()
                .setUserItem(new RoleBuilder()
                    .setKey(new PrincipalKey(UserStoreKey.SYSTEM, PrincipalType.ROLE, 'role'))
                    .setDisplayName('Role')
                    .build()).build(),
            new UserTypeTreeGridItemBuilder()
                .setUserItem(new UserBuilder()
                    .setKey(new PrincipalKey(UserStoreKey.SYSTEM, PrincipalType.USER, 'user'))
                    .setDisplayName('User')
                    .build()).build(),
            new UserTypeTreeGridItemBuilder()
                .setUserItem(new GroupBuilder()
                    .setKey(new PrincipalKey(UserStoreKey.SYSTEM, PrincipalType.GROUP, 'user-group'))
                    .setDisplayName('User Group')
                    .build()).build(),
            new UserTypeTreeGridItemBuilder()
                .setUserItem(new UserStoreBuilder()
                    .setKey(UserStoreKey.SYSTEM.toString())
                    .setDisplayName('User Store')
                    .build()).build()
        ]);
    }

    fetchChildren(parentNode: TreeNode<UserTypeTreeGridItem>): wemQ.Promise<UserTypeTreeGridItem[]> {

        return this.fetchUserStores().then((userStores: UserStore[]) => {
            if (userStores.length > 1) {
                return userStores.map((userStore: UserStore) => new UserTypeTreeGridItemBuilder()
                    .setUserItem(new UserStoreBuilder()
                        .setKey(userStore.getKey().toString())
                        .setDisplayName(userStore.getDisplayName())
                        .build()).build());
            } else if (userStores.length === 1) {
                const userItem = parentNode.getData().getUserItem();
                if (userItem instanceof User) {
                    const item = new UserTreeGridItemBuilder().setUserStore(userStores[0]).setType(UserTreeGridItemType.USERS).build();
                    new NewPrincipalEvent([item]).fire();
                } else if (userItem instanceof Group) {
                    const item = new UserTreeGridItemBuilder().setUserStore(userStores[0]).setType(UserTreeGridItemType.GROUPS).build();
                    new NewPrincipalEvent([item]).fire();
                }
            }
            return [];
        });
    }
}
