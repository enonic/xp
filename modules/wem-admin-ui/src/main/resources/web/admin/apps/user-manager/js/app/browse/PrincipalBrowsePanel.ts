module app.browse {

    import ModuleKey = api.module.ModuleKey;
    import Principal = api.security.Principal;
    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;

    export class PrincipalBrowsePanel extends api.app.browse.BrowsePanel<api.security.Principal> {

        private browseActions: app.browse.PrincipalBrowseActions;

        private principalTreeGrid: PrincipalTreeGrid;

        private principalFilterPanel: app.browse.filter.PrincipalBrowseFilterPanel;
        private contentFilterPanel: app.browse.filter.PrincipalBrowseFilterPanel;

        private toolbar: PrincipalBrowseToolbar;

        private moduleIconUrl: string;

        constructor() {
            var treeGridContextMenu = new app.browse.PrincipalTreeGridContextMenu();
            this.principalTreeGrid = new PrincipalTreeGrid();

            this.browseActions = PrincipalBrowseActions.init(this.principalTreeGrid);
            treeGridContextMenu.setActions(this.browseActions);
            this.principalFilterPanel = new app.browse.filter.PrincipalBrowseFilterPanel();
            this.toolbar = new PrincipalBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new PrincipalBrowseItemPanel();

            super({
                browseToolbar: this.toolbar,
                treeGrid: this.principalTreeGrid,
                browseItemPanel: browseItemPanel,
                filterPanel: this.principalFilterPanel
            });

            this.principalTreeGrid.onSelectionChanged((selectedRows: TreeNode<Principal>[]) => {
                // this.browseActions.updateActionsEnabledState(<any[]>selectedRows.map((elem) => {
                //     return elem.getData();
                // }));
            });

            this.moduleIconUrl = api.util.UriHelper.getAdminUri('common/images/icons/icoMoon/128x128/pencil.png');
        }

        treeNodesToBrowseItems(nodes: TreeNode<Principal>[]): BrowseItem<Principal>[] {
            var browseItems: BrowseItem<Principal>[] = [];

            // do not proceed duplicated content. still, it can be selected
            nodes.forEach((node: TreeNode<Principal>, index: number) => {
                for (var i = 0; i <= index; i++) {
                    if (nodes[i].getData().getKey() === node.getData().getKey()) {
                        break;
                    }
                }
                if (i === index) {
                    var principalEl = node.getData();
                    var item = new BrowseItem<Principal>(principalEl).
                        setId(principalEl.getKey().toString()).
                        setDisplayName(principalEl.getDisplayName()).
                        setPath(principalEl.getDisplayName()).
                        setIconUrl(this.moduleIconUrl);
                    browseItems.push(item);
                }
            });
            return browseItems;
        }

    }

}