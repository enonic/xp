module app.browse.grid {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeGrid = api.app.browse.treegrid.TreeGrid;
    import TreeNode = api.app.browse.treegrid.TreeNode;
    import TreeGridBuilder = api.app.browse.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.app.browse.treegrid.DateTimeFormatter;

    export class ContentGridPanel2Builder extends TreeGridBuilder<ContentSummary> {
        constructor(grid?: ContentGridPanel2) {
            super(grid);

            if (grid) {
                this.classes = this.classes.split(" ").filter((elem) => {
                    return elem.length > 0 && elem !== "content-grid";
                }).join(" ");
            } else {
                this.columns = this.buildDefaultColumns();
            }

            this.classes += " content-grid";
        }

        buildDefaultColumns(): GridColumn<TreeNode<ContentSummary>>[] {
            // GridColumn<TreeNode<ContentSummary>> is a valid type
            var column1 = new GridColumnBuilder<TreeNode<ContentSummary>>().
                    setName("Name").
                    setId("displayName").
                    setField("displayName").
                    setFormatter(this.defaultNameFormatter).
                build();

            var column2 = new GridColumnBuilder<TreeNode<ContentSummary>>().
                    setName("ModifiedTime").
                    setId("modifiedTime").
                    setField("modifiedTime").
                    setCssClass("modified").
                    setMinWidth(150).
                    setMaxWidth(170).
                    setFormatter(DateTimeFormatter.format).
                build();

            return [column1, column2];
        }

        private defaultNameFormatter(row: number, cell: number, value: any, columnDef: any, item: ContentSummary) {
            var contentSummaryViewer = new ContentSummaryViewer();
            contentSummaryViewer.setObject(item);
            return contentSummaryViewer.toString();
        }

        build(): ContentGridPanel2 {
            return new ContentGridPanel2(this);
        }
    }


    export class ContentGridPanel2 extends TreeGrid<ContentSummary> {

        constructor(builder: ContentGridPanel2Builder) {
            super(builder);

            this.getGrid().subscribeOnDblClick((event, data) => {
                if (this.isActive()) {
                    new EditContentEvent([this.getGrid().getDataView().getItem(data.row).getData()]).fire();
                }
            });
        }

        fetchChildren(parent?: ContentSummary): Q.Promise<ContentSummary[]> {
            var parentContentId = parent ? parent.getId() : "";
            return new api.content.ListContentByIdRequest(parentContentId).sendAndParse();
        }
    }
}
