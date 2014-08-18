module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import TemplateSummary = api.content.TemplateSummary;
    import TemplateSummaryViewer = app.browse.TemplateSummaryViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;
    import TemplateBrowseActions = app.browse.action.TemplateBrowseActions;

    export class TemplateTreeGrid extends TreeGrid<TemplateSummary> {

        constructor(browseActions: app.browse.action.TemplateBrowseActions) {
            super(new TreeGridBuilder<TemplateSummary>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<TemplateSummary>>().
                            setName("Name").
                            setId("displayName").
                            setField("displayName").
                            setFormatter(this.nameFormatter).
                            setMinWidth(250).
                            build(),

                        new GridColumnBuilder<TreeNode<TemplateSummary>>().
                            setName("Version").
                            setId("version").
                            setField("version").
                            setMaxWidth(70).
                            setMinWidth(50).
                            build() ,

                        new GridColumnBuilder<TreeNode<TemplateSummary>>().
                            setName("Vendor Name").
                            setId("vendorName").
                            setField("vendorName").
                            setMinWidth(80).
                            build()

                    ]).
                    prependClasses("template-grid").
                    setShowContextMenu(new TreeGridContextMenu(browseActions))
            );
            browseActions.setTemplateTreeGrid(this);
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<TemplateSummary>) {
            var viewer = new TemplateSummaryViewer();
            viewer.setObject(node.getData());
            return viewer.toString();
        }

        hasChildren(elem: TemplateSummary): boolean {
            return elem.hasChildren();
        }

        fetchChildren(parentNode?: TreeNode<TemplateSummary>): Q.Promise<TemplateSummary[]> {
            var parentId = parentNode && parentNode.getData() ? parentNode.getData().getKey() : '';
            return new api.content.site.template.TemplateTreeRequest(parentId).sendAndParse();
        }
    }
}
