module app.view {

    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentId = api.content.ContentId;
    import CompareStatus = api.content.CompareStatus;
    import ContentVersion = api.content.ContentVersion;
    import ContentVersionViewer = api.content.ContentVersionViewer;

    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
    import GridOptionsBuilder = api.ui.grid.GridOptionsBuilder;

    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;

    export class ContentVersionsTreeGrid extends api.ui.treegrid.TreeGrid<ContentVersion> {

        contentId: ContentId;
        private status: api.content.CompareStatus;
        private static branchMaster = "master";

        constructor() {
            var descColumn = new GridColumnBuilder<TreeNode<ContentVersion>>().
                setName("DisplayName").
                setField("displayName").
                setCssClass("description").
                setMinWidth(160).
                setFormatter(this.descriptionFormatter).
                build();

            var statusColumn = new GridColumnBuilder<TreeNode<ContentVersion>>().
                setName("Status").
                setField("workspaces").
                setCssClass("status").
                setFormatter(this.statusFormatter.bind(this)).
                setMinWidth(80).
                build();

            super(new TreeGridBuilder<ContentVersion>().
                setAutoLoad(false).
                setAutoHeight(true).
                setCheckableRows(false).
                setHotkeysEnabled(false).
                setSelectedCellCssClass("").
                setShowToolbar(false).
                setRowHeight(70).
                setColumns([
                    descColumn,
                    statusColumn
                ]).
                prependClasses("content-versions-tree-grid"));
        }

        public setItem(item: ContentSummaryAndCompareStatus) {
            this.contentId = item.getContentId();
            this.status = item.getCompareStatus();
        }

        getDataId(data: ContentVersion): string {
            return data.id;
        }

        private descriptionFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentVersion>) {
            var description = "";

            if (node.getData()) {  // default node

                var viewer = <ContentVersionViewer> node.getViewer("description");
                if (!viewer) {
                    viewer = new ContentVersionViewer();
                    var length = node.getRoot().getChildren().length;
                    viewer.setObject(node.getData(), length - row);
                    node.setViewer("description", viewer);
                }

                description = viewer.toString();
            }

            return description;

        }

        private getState(workspace): string {
            if (workspace == ContentVersionsTreeGrid.branchMaster) {
                return api.content.CompareStatusFormatter.formatStatus(api.content.CompareStatus.EQUAL);
            }
            else {
                return api.content.CompareStatusFormatter.formatStatus(this.status);
            }
        }

        private statusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentVersion>) {
            if (this.status == undefined) {
                return "";
            }
            var badges: string[] = [];
            var hasMaster = value.some((workspace) => {
                return workspace == ContentVersionsTreeGrid.branchMaster;
            });

            value.forEach((workspace: string) => {
                if (!hasMaster || workspace == ContentVersionsTreeGrid.branchMaster) {
                    badges.push(new api.dom.PEl('badge ' + workspace).setHtml(this.getState(workspace)).toString());
                }
            });

            return badges.join("");
        }

    }

}