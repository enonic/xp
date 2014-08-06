module api.ui.treegrid {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
    import GridOptions = api.ui.grid.GridOptions;
    import GridOptionsBuilder = api.ui.grid.GridOptionsBuilder;
    import ContextMenu = api.ui.menu.ContextMenu;

    export class TreeGridBuilder<NODE> {

        private showToolbar: boolean = true;

        private contextMenu: TreeGridContextMenu;

        private options: GridOptions<NODE>;

        private columns: GridColumn<TreeNode<NODE>>[] = [];

        private classes: string = "";

        constructor(grid?: TreeGrid<NODE>) {
            if (grid) {
                this.showToolbar = grid.hasToolbar();
                this.contextMenu = grid.getContextMenu();
                this.classes = grid.getEl().getClass();
                this.copyOptions(grid.getOptions())
                    .copyColumns(grid.getColumns());
            } else {
                this.options = this.buildDefaultOptions();
                this.columns = this.buildDefaultColumns();
            }

            this.classes = "tree-grid " + this.classes;
        }

        /*
         Note: We are using a proxy class to handle items/nodes and
         track the selection, expansion, etc.
         To have access to the complex properties, that are not in the root
         of the object, like `node.data.id`, we need to specify a custom
         column value extractor.
         */
        nodeExtractor(node, column) {
            var names = column.field.split('.');
            var val = node["data"][names[0]];

            for (var i = 1; i < names.length; i++) {
                if (val && typeof val == 'object' && names[i] in val) {
                    val = val[names[i]];
                } else {
                    val = '';
                }
            }
            return val;
        }

        buildDefaultOptions(): GridOptions<NODE> {
            return new GridOptionsBuilder<NODE>().
                setDataItemColumnValueExtractor(this.nodeExtractor).
                setEditable(false).
                setEnableAsyncPostRender(true).
                setAutoRenderGridOnDataChanges(true).
                setEnableCellNavigation(false).
                setEnableColumnReorder(false).
                setForceFitColumns(true).
                setHideColumnHeaders(true).
                setCheckableRows(true).
                setRowHeight(45).
                setAutoHeight(true).
                build();
        }

        buildDefaultColumns(): GridColumn<TreeNode<NODE>>[] {
            return [];
        }

        copyOptions(options: GridOptions<NODE>): TreeGridBuilder<NODE> {
            this.options = new GridOptionsBuilder<NODE>().build();
            return this;
        }

        copyColumns(columns: GridColumn<TreeNode<NODE>>[]): TreeGridBuilder<NODE> {
            this.columns = [];
            columns.forEach((column) => {
                this.columns.push(new GridColumnBuilder<NODE>(column).build());
            });
            return this;
        }

        isShowToolbar(): boolean {
            return this.showToolbar;
        }

        getContextMenu(): TreeGridContextMenu {
            return this.contextMenu;
        }

        getOptions(): GridOptions<NODE> {
            return this.options;
        }

        getColumns(): GridColumn<TreeNode<NODE>>[] {
            return this.columns;
        }

        getClasses(): string {
            return this.classes;
        }

        setShowToolbar(showToolbar: boolean): TreeGridBuilder<NODE> {
            this.showToolbar = showToolbar;
            return this;
        }

        setShowContextMenu(contextMenu: TreeGridContextMenu): TreeGridBuilder<NODE> {
            this.contextMenu = contextMenu;
            return this;
        }

        setOptions(options: GridOptions<NODE>): TreeGridBuilder<NODE> {
            this.options = options;
            return this;
        }

        setColumns(columns: GridColumn<TreeNode<NODE>>[]): TreeGridBuilder<NODE> {
            this.columns = columns;
            return this;
        }

        setClasses(classes: string): TreeGridBuilder<NODE> {
            this.classes = classes;
            return this;
        }

        prependClasses(classes: string): TreeGridBuilder<NODE> {
            this.classes = classes + " " + this.classes;
            return this;
        }

        /**
         Should be overriden by child class.
         */
        build(): TreeGrid<NODE> {
            return new TreeGrid<NODE>(this);
        }

    }
}
