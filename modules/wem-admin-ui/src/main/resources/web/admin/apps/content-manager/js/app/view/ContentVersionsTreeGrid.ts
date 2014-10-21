module app.view {

    import ContentId = api.content.ContentId;
    import ContentVersion = api.content.ContentVersion;
    import ContentVersionViewer = api.content.ContentVersionViewer;

    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;

    export class ContentVersionsTreeGrid extends api.ui.treegrid.TreeGrid<ContentVersion> {

        contentId: ContentId;

        constructor() {
            var descColumn = new GridColumnBuilder<TreeNode<ContentVersion>>().
                setName("DisplayName").
                setField("displayName").
                setCssClass("description").
                setMinWidth(240).
                setFormatter(this.descriptionFormatter).
                build();

            var statusColumn = new GridColumnBuilder<TreeNode<ContentVersion>>().
                setName("Status").
                setField("workspaces").
                setCssClass("status").
                setFormatter(this.statusFormatter).
                setMinWidth(80).
                build();

            super(new TreeGridBuilder<ContentVersion>().
                setAutoLoad(false).
                setCheckableRows(false).
                setHotkeysEnabled(false).
                setShowToolbar(false).
                setColumns([
                    descColumn,
                    statusColumn
                ]).
                prependClasses("content-versions-tree-grid"));
        }

        public setContentId(contentId: ContentId) {
            this.contentId = contentId;
            this.reload();
        }

        public getContentId(): ContentId {
            return this.contentId;
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

        private statusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentVersion>) {
            var badges: string[] = [];

            value.forEach((workspace: string) => {
                badges.push(new api.dom.SpanEl('badge ' + workspace).setHtml(workspace).toString());
            });

            return badges.join("");
        }

    }

}