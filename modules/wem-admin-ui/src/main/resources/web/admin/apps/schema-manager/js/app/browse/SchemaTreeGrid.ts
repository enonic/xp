module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import Schema = api.schema.Schema;
    import SchemaViewer = app.browse.SchemaViewer;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;
    import SchemaBrowseActions = app.browse.SchemaBrowseActions;

    export class SchemaTreeGrid extends TreeGrid<Schema> {

        constructor(browseActions: SchemaBrowseActions) {
            super(new TreeGridBuilder<Schema>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<Schema>>().
                            setName("Name").
                            setId("displayName").
                            setField("displayName").
                            setFormatter(this.defaultNameFormatter).
                            build(),

                        new GridColumnBuilder<TreeNode<Schema>>().
                            setName("Module").
                            setId("module").
                            setField("module").
                            build() ,

                        new GridColumnBuilder<TreeNode<Schema>>().
                            setName("Type").
                            setId("type").
                            setField("type").
                            build() ,

                        new GridColumnBuilder<TreeNode<Schema>>().
                            setName("Modified").
                            setId("modifiedTime").
                            setField("modifiedTime").
                            setFormatter(DateTimeFormatter.format).
                            setMaxWidth(270).
                            build()
                    ]).
                    prependClasses("schema-grid").
                    setShowContextMenu(new TreeGridContextMenu(browseActions))
            );
            browseActions.setSchemaTreeGrid(this);
        }

        fetchChildren(parent?: Schema): Q.Promise<Schema[]> {
            var parentId = parent ? parent.getKey() : '';
            return new api.schema.SchemaTreeRequest(parentId).sendAndParse();
        }

        private defaultNameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<Schema>) {
            var viewer = new SchemaViewer();
            viewer.setObject(node.getData());
            return viewer.toString();
        }
    }
}
