module app.browse {
    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import UserTreeGridItem = app.browse.UserTreeGridItem;
    import UserTreeGridItemType = app.browse.UserTreeGridItemType;

    export class UserBrowsePanel extends api.app.browse.BrowsePanel<app.browse.UserTreeGridItem> {

        private browseActions: app.browse.UserTreeGridActions;

        private userTreeGrid: UserItemsTreeGrid;

        private userFilterPanel: app.browse.filter.PrincipalBrowseFilterPanel;

        private toolbar: UserBrowseToolbar;

        constructor() {
            this.userTreeGrid = new UserItemsTreeGrid();

            this.browseActions = this.userTreeGrid.getTreeGridActions();
            this.userFilterPanel = new app.browse.filter.PrincipalBrowseFilterPanel();
            this.toolbar = new UserBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new UserBrowseItemPanel();

            super({
                browseToolbar: this.toolbar,
                treeGrid: this.userTreeGrid,
                browseItemPanel: browseItemPanel,
                filterPanel: this.userFilterPanel
            });

            this.userTreeGrid.onSelectionChanged((selectedRows: TreeNode<UserTreeGridItem>[]) => {
                this.browseActions.updateActionsEnabledState(<any[]>selectedRows.map((elem) => {
                    return elem.getData();
                }));
            });
        }

        treeNodesToBrowseItems(nodes: TreeNode<UserTreeGridItem>[]): BrowseItem<UserTreeGridItem>[] {
            var browseItems: BrowseItem<UserTreeGridItem>[] = [];

            // do not proceed duplicated content. still, it can be selected
            nodes.forEach((node: TreeNode<UserTreeGridItem>) => {
                var userGridItem = node.getData();

                var item = new BrowseItem<UserTreeGridItem>(userGridItem).
                    setId(userGridItem.getDataId()).
                    setDisplayName(userGridItem.getItemDisplayName()).
                    setIconClass(this.selectIconClass(userGridItem));
                browseItems.push(item);

            });
            return browseItems;
        }

        private selectIconClass(item: app.browse.UserTreeGridItem): string {
            var type: UserTreeGridItemType = item.getType();
            switch (type) {
            case UserTreeGridItemType.USER_STORE:
            {
                return "icon-address-book icon-large";
            }
            case UserTreeGridItemType.PRINCIPAL:
            {
                if (item.getPrincipal().isRole()) {
                    return "icon-user7 icon-large";

                } else if (item.getPrincipal().isUser()) {
                    return "icon-user icon-large";

                } else if (item.getPrincipal().isGroup()) {
                    return "icon-users icon-large";
                }
                break;
            }
            case UserTreeGridItemType.GROUPS:
            {
                return "icon-folder icon-large"
            }
            case UserTreeGridItemType.ROLES:
            {
                return "icon-folder icon-large";
            }
            case UserTreeGridItemType.USERS:
            {
                return "icon-folder icon-large";
            }
            }

        }
    }

}