module app.browse {

    import Element = api.dom.Element;
    import ElementHelper = api.dom.ElementHelper;

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;

    import ContentResponse = api.content.ContentResponse;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;
    import ChildOrder = api.content.ChildOrder;

    import CompareStatus = api.content.CompareStatus;

    export class SortContentTreeGrid extends TreeGrid<ContentSummaryAndCompareStatus> {

        private contentId: api.content.ContentId;

        private curChildOrder: ChildOrder;

        static MAX_FETCH_SIZE: number = 30;

        constructor() {
            var dragColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("Drag").
                setId("drag").
                setMinWidth(50).
                setMaxWidth(50).
                setField("contentSummary.order").
                setCssClass("drag-icon").
                setFormatter(this.dragFormatter).
                setBehavior("selectAndMove").
                build();
            var nameColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("Name").
                setId("displayName").
                setField("contentSummary.displayName").
                setMinWidth(130).
                setFormatter(this.nameFormatter).
                build();
            var compareStatusColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("CompareStatus").
                setId("compareStatus").
                setField("compareContentResult.compareStatus").
                setFormatter(this.statusFormatter).
                setCssClass("status").
                setMinWidth(75).
                setMaxWidth(75).
                build();

            super(new TreeGridBuilder<ContentSummaryAndCompareStatus>().
                    setColumns([
                        dragColumn,
                        nameColumn,
                        compareStatusColumn
                    ]).
                    setPartialLoadEnabled(true).
                    setCheckableRows(false).
                    setShowToolbar(false).
                    setDragAndDrop(true).
                    disableMultipleSelection(true).
                    prependClasses("content-tree-grid").
                    setSelectedCellCssClass("selected-sort-row selected")
            );

        }

        private statusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {

            if (!node.getData().getContentSummary()) {
                return "";
            }

            var compareLabel: string = api.content.CompareStatus[value];

            var compareStatus: CompareStatus = CompareStatus[compareLabel];

            switch (compareStatus) {
            case CompareStatus.NEW:
                return "New";
                break;
            case CompareStatus.NEWER:
                return "Modified";
                break;
            case CompareStatus.OLDER:
                return "Behind";
                break;
            case CompareStatus.UNKNOWN:
                return "Unknown";
                break;
            case CompareStatus.DELETED:
                return "Deleted";
                break;
            case CompareStatus.EQUAL:
                return "Online";
                break;
            default:
                return "Unknown"
            }
        }

        private dragFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
            var wrapper = new api.dom.SpanEl();
            wrapper.getEl().setTitle(value);
            var icon = new api.dom.DivEl("icon-menu3 drag-icon");
            wrapper.getEl().setInnerHtml(icon.toString());
            return wrapper.toString();
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
            if (!!node.getData().getContentSummary()) {  // default node

                var viewer: ContentSummaryViewer = <ContentSummaryViewer>node.getViewer("name");
                if (!viewer) {
                    viewer = new ContentSummaryViewer();
                    viewer.setObject(node.getData().getContentSummary(), node.calcLevel() > 1);
                    node.setViewer("name", viewer);
                }
                return viewer.toString();

            } else { // `load more` node
                var content = new api.dom.DivEl("children-to-load"),
                    parent = node.getParent();
                content.setHtml((parent.getMaxChildren() - parent.getChildren().length + 1) + " children left to load.");

                return content.toString();
            }
        }

        sortNodeChildren(node: TreeNode<ContentSummaryAndCompareStatus>) {
            this.initData(this.getRoot().getCurrentRoot().treeToList());
        }

        fetchChildren(): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
            var parentContentId: api.content.ContentId;
            var parentNode = this.getRoot().getCurrentRoot();
            if (parentNode.getData()) {
                parentContentId = parentNode.getData().getContentSummary().getContentId();
                this.contentId = parentContentId;
                parentNode.setData(null);
            } else {
                parentContentId = this.contentId;
            }

            var from = parentNode.getChildren().length;
            if (from > 0 && !parentNode.getChildren()[from - 1].getData().getContentSummary()) {
                parentNode.getChildren().pop();
                from--;
            }

            return ContentSummaryAndCompareStatusFetcher.fetchChildren(parentContentId, from, SortContentTreeGrid.MAX_FETCH_SIZE,
                this.curChildOrder).
                then((data: ContentResponse<ContentSummaryAndCompareStatus>) => {
                    var contents = parentNode.getChildren().map((el) => {
                        return el.getData();
                    }).slice(0, from).concat(data.getContents());
                    var meta = data.getMetadata();
                    parentNode.setMaxChildren(meta.getTotalHits());
                    if (from + meta.getHits() < meta.getTotalHits()) {
                        contents.push(new ContentSummaryAndCompareStatus());
                    }
                    return contents;
                });

        }

        hasChildren(data: ContentSummaryAndCompareStatus): boolean {
            return data.hasChildren();
        }

        getDataId(data: ContentSummaryAndCompareStatus): string {
            return data.getId();
        }

        getContentId() {
            return this.contentId;
        }

        getChildOrder(): ChildOrder {
            return this.curChildOrder;
        }

        setChildOrder(value: ChildOrder) {
            this.curChildOrder = value;
        }


    }
}