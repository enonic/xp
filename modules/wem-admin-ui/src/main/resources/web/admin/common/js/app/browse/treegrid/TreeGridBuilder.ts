module api.app.browse.treegrid {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
    import GridOptions = api.ui.grid.GridOptions;
    import GridOptionsBuilder = api.ui.grid.GridOptionsBuilder;


    export class TreeGridBuilder<NODE extends api.node.Node> {

        showToolbar: boolean = true;

        options: GridOptions<NODE>;

        columns: GridColumn<TreeNode<NODE>>[] = [];

        classes: string = "";

        constructor(grid?: TreeGrid<NODE>) {
            if (grid) {
                this.showToolbar = grid.hasToolbar();
                this.classes = grid.getEl().getClass().split(" ").filter((elem) => {
                    return elem.length > 0 && elem !== "tree-grid" && elem !== "no-toolbar";
                }).join(" ");
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
        private nodeExtractor(node, column) {
            return node["data"][column.field];
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

        /**
        Should be overriden by child class.
         */
        build(): TreeGrid<NODE> {
            return new TreeGrid<NODE>(this);
        }

    }
}
