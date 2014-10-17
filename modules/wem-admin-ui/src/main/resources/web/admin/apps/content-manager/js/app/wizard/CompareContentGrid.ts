module app.wizard {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ContentResponse = api.content.ContentResponse;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;

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
                    ]).
                    setPartialLoadEnabled(true).
                    setLoadBufferSize(20). // rows count
                    prependClasses("compare-content-grid")
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

        fetchChildren(parentNode?: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
            var parentContentId = parentNode && parentNode.getData() ? parentNode.getData().getId() : "";
            return api.content.ContentSummaryAndCompareStatusFetcher.fetchChildren(parentContentId).
                then((data: ContentResponse<ContentSummaryAndCompareStatus>) => {
                    return data.getContents();
                });
        }

        hasChildren(elem: ContentSummaryAndCompareStatus): boolean {
            return elem.hasChildren();
        }

        getDataId(data: ContentSummaryAndCompareStatus): string {
            return data.getId();
        }

        updateDataChildrenStatus(parentNode: TreeNode<ContentSummaryAndCompareStatus>) {
            ContentSummaryAndCompareStatusFetcher.fetchChildren(parentNode.getData().getId()).then((result: ContentResponse<ContentSummaryAndCompareStatus>) => {
                var hasChildren = (result.getMetadata().getTotalHits() > 0);
                if (parentNode.getData()) {
                    parentNode.getData().setContentSummary(new ContentSummaryBuilder(parentNode.getData().getContentSummary()).
                        setHasChildren(hasChildren).
                        setDeletable(!hasChildren).
                        build());
                    this.refresh();
                }
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            });

        }
    }
}
