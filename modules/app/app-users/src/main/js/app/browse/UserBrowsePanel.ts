import '../../api.ts';
import {UserItemsTreeGrid} from './UserItemsTreeGrid';
import {UserBrowseToolbar} from './UserBrowseToolbar';
import {UserTreeGridItem, UserTreeGridItemType} from './UserTreeGridItem';
import {UserBrowseItemPanel} from './UserBrowseItemPanel';
import {UserTreeGridActions} from './UserTreeGridActions';
import {PrincipalBrowseFilterPanel} from './filter/PrincipalBrowseFilterPanel';
import {Router} from '../Router';

import TreeGrid = api.ui.treegrid.TreeGrid;
import TreeNode = api.ui.treegrid.TreeNode;
import BrowseItem = api.app.browse.BrowseItem;
import PrincipalType = api.security.PrincipalType;
import i18n = api.util.i18n;

export class UserBrowsePanel extends api.app.browse.BrowsePanel<UserTreeGridItem> {

    protected treeGrid: UserItemsTreeGrid;

    constructor() {
        super();

        api.security.UserItemCreatedEvent.on((event) => {
            this.treeGrid.appendUserNode(event.getPrincipal(), event.getUserStore(), event.isParentOfSameType());
            this.setRefreshOfFilterRequired();
        });

        api.security.UserItemUpdatedEvent.on((event) => {
            this.treeGrid.updateUserNode(event.getPrincipal(), event.getUserStore());
        });

        api.security.UserItemDeletedEvent.on((event) => {
            this.setRefreshOfFilterRequired();
            /*
             Deleting content won't trigger browsePanel.onShow event,
             because we are left on the same panel. We need to refresh manually.
             */
            const userTreeGridItems: UserTreeGridItem[] = this.convertUserItemsToUserTreeGridItems(event.getPrincipals(),
                event.getUserStores());

            this.treeGrid.deleteNodes(userTreeGridItems);
            this.refreshFilter();
        });

        const changeSelectionStatus = api.util.AppHelper.debounce((selection: TreeNode<UserTreeGridItem>[]) => {
            const noSelection = selection.length === 0;
            const newAction = this.treeGrid.getTreeGridActions().NEW;

            let label;

            if (noSelection || selection[0].getData().getType() === UserTreeGridItemType.USER_STORE) {
                label = `${i18n('action.new')}…`;
            } else {
                const userItem = selection[0].getData();
                let type;

                switch (userItem.getType()) {

                case UserTreeGridItemType.USERS:
                    type = i18n('field.user');
                    break;
                case UserTreeGridItemType.GROUPS:
                    type = i18n('field.userGroup');
                    break;
                case UserTreeGridItemType.ROLES:
                    type = i18n('field.role');
                    break;
                case UserTreeGridItemType.PRINCIPAL:
                    type = i18n(`field.${PrincipalType[userItem.getPrincipal().getType()].toLowerCase()}`);
                    break;
                default:
                    type = '';
                }

                label = [i18n('action.new'), type].join(' ');
            }
            newAction.setLabel(label);
        }, 10);

        this.treeGrid.onSelectionChanged((currentSelection: TreeNode<UserTreeGridItem>[]) => changeSelectionStatus(currentSelection));

        this.treeGrid.onHighlightingChanged((node: TreeNode<UserTreeGridItem>) => changeSelectionStatus(node ? [node] : []));

        this.onShown(() => {
            Router.setHash('browse');
        });
    }

    protected createToolbar(): UserBrowseToolbar {
        let browseActions = <UserTreeGridActions> this.treeGrid.getTreeGridActions();

        return new UserBrowseToolbar(browseActions);
    }

    protected createTreeGrid(): UserItemsTreeGrid {
        return new UserItemsTreeGrid();
    }

    protected createBrowseItemPanel(): UserBrowseItemPanel {
        return new UserBrowseItemPanel();
    }

    protected createFilterPanel(): PrincipalBrowseFilterPanel {
        return new PrincipalBrowseFilterPanel();
    }

    protected enableSelectionMode() {
        this.treeGrid.filter(this.treeGrid.getSelectedDataList());
    }

    treeNodesToBrowseItems(nodes: TreeNode<UserTreeGridItem>[]): BrowseItem<UserTreeGridItem>[] {
        let browseItems: BrowseItem<UserTreeGridItem>[] = [];

        // do not proceed duplicated content. still, it can be selected
        nodes.forEach((node: TreeNode<UserTreeGridItem>) => {
            let userGridItem = node.getData();

            let item = new BrowseItem<UserTreeGridItem>(userGridItem).setId(userGridItem.getDataId()).setDisplayName(
                userGridItem.getItemDisplayName()).setIconClass(this.selectIconClass(userGridItem));
            browseItems.push(item);

        });
        return browseItems;
    }

    private selectIconClass(item: UserTreeGridItem): string {

        let type: UserTreeGridItemType = item.getType();

        switch (type) {
        case UserTreeGridItemType.USER_STORE:
            return 'icon-address-book icon-large';

        case UserTreeGridItemType.PRINCIPAL:
            if (item.getPrincipal().isRole()) {
                return 'icon-masks icon-large';

            } else if (item.getPrincipal().isUser()) {
                return 'icon-user icon-large';

            } else if (item.getPrincipal().isGroup()) {
                return 'icon-users icon-large';
            }
            break;

        case UserTreeGridItemType.GROUPS:
            return 'icon-folder icon-large';

        case UserTreeGridItemType.ROLES:
            return 'icon-folder icon-large';

        case UserTreeGridItemType.USERS:
            return 'icon-folder icon-large';
        }

    }

    private convertUserItemsToUserTreeGridItems(principals: api.security.Principal[],
                                                userStores: api.security.UserStore[]): UserTreeGridItem[] {
        let userTreeGridItems: UserTreeGridItem[] = [];

        if (principals) {
            userTreeGridItems = userTreeGridItems.concat(principals.map((principal) => {
                return UserTreeGridItem.fromPrincipal(principal);
            }));
        }

        if (userStores) {
            userTreeGridItems = userTreeGridItems.concat(userStores.map((userStore) => {
                return UserTreeGridItem.fromUserStore(userStore);
            }));
        }

        return userTreeGridItems;
    }
}
