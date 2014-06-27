module app.wizard {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeNode = api.app.browse.treegrid.TreeNode;
    import TreeGridBuilder = api.app.browse.treegrid.TreeGridBuilder;

    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class CompareContentGrid extends api.app.browse.treegrid.TreeGrid<ContentSummaryAndCompareStatus> {

        private content: api.content.Content;

        constructor(content: api.content.Content) {
            super(new TreeGridBuilder<ContentSummaryAndCompareStatus>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                            setName("Name").
                            setId("displayName").
                            setField("displayName").
                            setFormatter(this.defaultNameFormatter).
                            build()
                    ]).prependClasses("content-grid")
            )

            this.content = content;

            this.onLoaded(() => {
                this.selectAll();
            });
        }

        private defaultNameFormatter(row: number, cell: number, value: any, columnDef: any, item: ContentSummaryAndCompareStatus) {
            var contentSummaryViewer = new ContentSummaryViewer();
            contentSummaryViewer.setObject(item.getContentSummary());
            return contentSummaryViewer.toString();
        }

        fetchChildren(parent?: ContentSummaryAndCompareStatus): Q.Promise<ContentSummaryAndCompareStatus[]> {
            var parentContentId = parent ? parent.getId() : "";
            return new api.content.ContentSummaryAndCompareStatusFetcher(parentContentId).fetch(parentContentId);
        }
    }
}
