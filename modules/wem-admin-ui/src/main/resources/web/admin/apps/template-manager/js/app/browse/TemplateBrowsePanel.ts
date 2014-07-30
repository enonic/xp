module app.browse {

    import TemplateType = api.content.TemplateType;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TemplateSummary = api.content.TemplateSummary;
    import BrowseItem = api.app.browse.BrowseItem;

    export class TemplateBrowsePanel extends api.app.browse.BrowsePanel<app.browse.TemplateBrowseItem> {

        private browseActions: app.browse.action.TemplateBrowseActions;

        private toolbar: TemplateBrowseToolbar;

        private templateTreeGrid: TemplateTreeGrid;

        private pageTemplateIconUri: string;

        private siteTemplateIconUri: string;

        constructor(browseActions: app.browse.action.TemplateBrowseActions, templateTreeGrid: TemplateTreeGrid) {
            this.pageTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/newspaper.png');
            this.siteTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/earth.png');

            var treeGridContextMenu = new app.browse.TemplateTreeGridContextMenu();

            this.browseActions = browseActions;
            treeGridContextMenu.setActions(this.browseActions);
            this.templateTreeGrid = templateTreeGrid;

            this.toolbar = new TemplateBrowseToolbar(this.browseActions);
            var browseItemPanel = components.detailPanel = new TemplateBrowseItemPanel();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel2: this.templateTreeGrid,
                browseItemPanel: browseItemPanel,
                filterPanel: undefined
            });

            api.content.site.template.SiteTemplateDeletedEvent.on((event: api.content.site.template.SiteTemplateDeletedEvent) => {
                this.templateTreeGrid.reload();
            });

            this.templateTreeGrid.onRowSelectionChanged((selectedRows: TreeNode<TemplateSummary>[]) => {
                this.browseActions.updateActionsEnabledState(<TemplateSummary[]>selectedRows.map((elem) => {
                    return elem.getData();
                }));
            });

            api.content.site.template.SiteTemplateImportedEvent.on(() => {
                this.setRefreshNeeded(true);
                this.refreshFilterAndGrid();
            });
        }

        treeNodesToBrowseItems(nodes: TreeNode<TemplateSummary>[]): BrowseItem<TemplateSummary>[] {
            var browseItems: BrowseItem<TemplateSummary>[] = [];

            nodes.forEach((node: TreeNode<TemplateSummary>, index: number) => {
                for (var i = 0; i <= index; i++) {
                    if (nodes[i].getData().getId() === node.getData().getId()) {
                        break;
                    }
                }
                if (i === index) {
                    var templateSummary = node.getData();
                    var item = new BrowseItem<TemplateSummary>(templateSummary).
                        setId(templateSummary.getId()).
                        setDisplayName(templateSummary.getDisplayName()).
                        setPath(templateSummary.getKey()).
                        setIconUrl(templateSummary.getIconUrl());
                    browseItems.push(item);
                }
            });

            return browseItems;
        }

    }

}