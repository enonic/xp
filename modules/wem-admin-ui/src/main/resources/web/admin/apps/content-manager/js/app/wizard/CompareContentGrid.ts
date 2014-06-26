module app.wizard {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeNode = api.app.browse.treegrid.TreeNode;
    import TreeGridBuilder = api.app.browse.treegrid.TreeGridBuilder;


    export class CompareContentGridBuilder extends TreeGridBuilder<ContentSummary> {

        content: api.content.Content;

        constructor(grid?: CompareContentGrid) {
            super(grid);

            if (grid) {
                this.classes = this.classes.split(" ").filter((elem) => {
                    return elem.length > 0 && elem !== "content-grid";
                }).join(" ");
                this.content = grid.getContent();
            } else {
                this.columns = this.buildDefaultColumns();
                this.showToolbar = false;
            }

            this.classes += " content-grid";
        }

        buildDefaultColumns(): GridColumn<TreeNode<ContentSummary>>[] {
            // GridColumn<TreeNode<ContentSummary>> is a valid type
            var column = new GridColumnBuilder<ContentSummary>().
                    setName("Name").
                    setId("displayName").
                    setField("displayName").
                    setFormatter(this.defaultNameFormatter).
                build();

            return [column];
        }

        private defaultNameFormatter(row: number, cell: number, value: any, columnDef: any, item: ContentSummary) {
            var contentSummaryViewer = new ContentSummaryViewer();
            contentSummaryViewer.setObject(item);
            return contentSummaryViewer.toString();
        }

        getContent(): api.content.Content {
            return this.content;
        }

        setContent(content: api.content.Content) {
            this.content = content;
            return this;
        }

        build(): CompareContentGrid {
            return new CompareContentGrid(this);
        }
    }

    export class CompareContentGrid extends api.app.browse.treegrid.TreeGrid<ContentSummary> {

        private content: api.content.Content;

        constructor(builder: CompareContentGridBuilder) {
            super(builder);

            this.content = builder.getContent();

            this.onLoaded(() => {
                this.selectAll();
            });
        }

        fetchChildren(parent?: ContentSummary): Q.Promise<ContentSummary[]> {
            var deferred = Q.defer<ContentSummary[]>();

            deferred.resolve(this.content ? [this.content] : []);
            return deferred.promise;
        }

        getContent(): api.content.Content {
            return this.content;
        }
    }
}
