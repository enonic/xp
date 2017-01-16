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
            this.treeGrid.deleteUserNodes(event.getPrincipals(), event.getUserStores());
            this.refreshFilter();
        });

        this.onShown((event) => {
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
}
