module app.wizard {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeNode = api.app.browse.treegrid.TreeNode;
    import TreeGridBuilder = api.app.browse.treegrid.TreeGridBuilder;

    export class CompareContentGrid extends api.app.browse.treegrid.TreeGrid<ContentSummary> {

        private content: api.content.Content;

        constructor(content: api.content.Content) {
            super(new TreeGridBuilder<ContentSummary>().
                setColumns([
                    new GridColumnBuilder<ContentSummary>().
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

        private defaultNameFormatter(row: number, cell: number, value: any, columnDef: any, item: ContentSummary) {
            var contentSummaryViewer = new ContentSummaryViewer();
            contentSummaryViewer.setObject(item);
            return contentSummaryViewer.toString();
        }

        fetchChildren(parent?: ContentSummary): Q.Promise<ContentSummary[]> {
            var deferred = Q.defer<ContentSummary[]>();

            deferred.resolve(this.content ? [this.content] : []);
            return deferred.promise;
        }
    }
}
