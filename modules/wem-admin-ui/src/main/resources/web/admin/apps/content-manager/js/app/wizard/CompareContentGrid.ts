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

            var viewer = <ContentSummaryViewer>node.getViewer("name");
            if (!viewer) {
                viewer = new ContentSummaryViewer();
                viewer.setObject(node.getData().getContentSummary());
                node.setViewer("name", viewer);
            }
            return viewer.toString();
        }

        fetchChildren(parentNode?: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
            var parentContentId = parentNode && parentNode.getData() ? parentNode.getData().getContentId() : null;
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

        refreshNodeData(parentNode: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<TreeNode<ContentSummaryAndCompareStatus>> {
            return ContentSummaryAndCompareStatusFetcher.fetch(parentNode.getData().getContentId()).then((content: ContentSummaryAndCompareStatus) => {
                parentNode.setData(content);
                this.refreshNode(parentNode);
                return parentNode;
            });
        }

        sortNodeChildren(node: TreeNode<ContentSummaryAndCompareStatus>) {
            var comparator: api.Comparator<TreeNode<ContentSummaryAndCompareStatus>>;
            if (this.getRoot().getCurrentRoot() == node) {
                comparator = new api.content.ContentByDisplayNameComparator();
            } else {
                comparator = new api.content.ContentByModifiedTimeComparator();
            }
            var children: TreeNode<ContentSummaryAndCompareStatus>[] = node.getChildren().sort(comparator.compare);
            node.setChildren(children);
            this.initData(this.getRoot().getCurrentRoot().treeToList());
        }
    }
}
