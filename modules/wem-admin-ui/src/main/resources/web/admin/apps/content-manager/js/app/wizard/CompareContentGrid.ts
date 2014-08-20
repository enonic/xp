module app.wizard {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;

    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class CompareContentGrid extends TreeGrid<ContentSummaryAndCompareStatus> {

        private content: api.content.Content;

        constructor(content: api.content.Content) {
            super(new TreeGridBuilder<ContentSummaryAndCompareStatus>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                            setName("Name").
                            setId("displayName").
                            setField("displayName").
                            setFormatter(this.nameFormatter).
                            build()
                    ]).prependClasses("content-tree-grid")
            );

            this.content = content;

            this.onLoaded(() => {
                this.selectAll();
            });
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
            var contentSummaryViewer = new ContentSummaryViewer();
            contentSummaryViewer.setObject(node.getData().getContentSummary());
            return contentSummaryViewer.toString();
        }

        fetchChildren(parent?: ContentSummaryAndCompareStatus): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
            var parentContentId = parent ? parent.getId() : "";
            return api.content.ContentSummaryAndCompareStatusFetcher.fetchChildren(parentContentId);
        }

        hasChildren(elem: ContentSummaryAndCompareStatus): boolean {
            return elem.hasChildren();
        }
    }
}
