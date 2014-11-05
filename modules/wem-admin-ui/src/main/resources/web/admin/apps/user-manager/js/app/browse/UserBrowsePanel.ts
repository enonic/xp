module app.browse {
    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import UserTreeGridItem = api.security.UserTreeGridItem;

    export class UserBrowsePanel extends api.app.browse.BrowsePanel<api.security.UserTreeGridItem> {

        private browseActions: app.browse.UserBrowseActions;

        private userTreeGrid: UserItemTreeGrid;

        private userFilterPanel: app.browse.filter.PrincipalBrowseFilterPanel;

        private toolbar: UserBrowseToolbar;

        private moduleIconUrl: string;

        constructor() {
            var treeGridContextMenu = new app.browse.UserTreeGridContextMenu();
            this.userTreeGrid = new UserItemTreeGrid();

            this.browseActions = UserBrowseActions.init(this.userTreeGrid);
            treeGridContextMenu.setActions(this.browseActions);
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
                // this.browseActions.updateActionsEnabledState(<any[]>selectedRows.map((elem) => {
                //     return elem.getData();
                // }));
            });

            this.moduleIconUrl = api.util.UriHelper.getAdminUri('common/images/icons/icoMoon/128x128/puzzle.png');
        }

        treeNodesToBrowseItems(nodes: TreeNode<UserTreeGridItem>[]): BrowseItem<UserTreeGridItem>[] {
            var browseItems: BrowseItem<UserTreeGridItem>[] = [];

            // do not proceed duplicated content. still, it can be selected
            nodes.forEach((node: TreeNode<UserTreeGridItem>) => {
                var userGridItem = node.getData();
                if (userGridItem instanceof api.security.UserStore) {
                    //TODO implement for  system userstore ...
                }
                var item = new BrowseItem<UserTreeGridItem>(userGridItem).
                    // setId(userGridItem.getId()).
                    setDisplayName(userGridItem.getDisplayName()).
                    setIconUrl(this.moduleIconUrl);
                browseItems.push(item);

            });
            return browseItems;
        }

    }

}