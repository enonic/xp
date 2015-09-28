module api.ui.treegrid {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
    import GridOptions = api.ui.grid.GridOptions;
    import GridOptionsBuilder = api.ui.grid.GridOptionsBuilder;
    import ContextMenu = api.ui.menu.ContextMenu;

    export class TreeGridBuilder<NODE> {

        private expandAll: boolean = false;

        private showToolbar: boolean = true;

        private contextMenu: TreeGridContextMenu;

        private options: GridOptions<NODE>;

        private columns: GridColumn<TreeNode<NODE>>[] = [];

        private classes: string = "";

        private autoLoad: boolean = true;

        private hotkeysEnabled: boolean = true;

        private partialLoadEnabled: boolean = false;

        private loadBufferSize: number = 0;

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
                setAutoHeight(false).
                setEnableAsyncPostRender(true).
                setAutoRenderGridOnDataChanges(true).

                // It is necessary to turn off the library key handling. It may cause
                // the conflicts with Mousetrap, which leads to skipping the key events
                // Do not set to true, if you are not fully aware of the result
                setEnableCellNavigation(false).

                setEnableColumnReorder(false).
                setForceFitColumns(true).
                setHideColumnHeaders(true).
                setCheckableRows(true).
                setRowHeight(45).
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

        isExpandAll(): boolean {
            return this.expandAll;
        }

        setExpandAll(value: boolean): TreeGridBuilder<NODE> {
            this.expandAll = value;
            return this;
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

        setAutoLoad(autoLoad: boolean): TreeGridBuilder<NODE> {
            this.autoLoad = autoLoad;
            return this;
        }

        isAutoLoad(): boolean {
            return this.autoLoad;
        }

        setCheckableRows(checkable: boolean): TreeGridBuilder<NODE> {
            this.options.setCheckableRows(checkable);
            return this;
        }

        isCheckableRows(): boolean {
            return this.options.isCheckableRows()
        }

        setDragAndDrop(dragAndDrop: boolean): TreeGridBuilder<NODE> {
            this.options.setDragAndDrop(dragAndDrop);
            return this;
        }

        isDragAndDrop(): boolean {
            return this.options.isDragAndDrop();
        }

        setSelectedCellCssClass(selectedCellCss: string): TreeGridBuilder<NODE> {
            this.options.setSelectedCellCssClass(selectedCellCss);
            return this;
        }

        getSelectedCellCssClass(): string {
            return this.options.getSelectedCellCssClass();
        }

        disableMultipleSelection(disableMultipleSelection: boolean): TreeGridBuilder<NODE> {
            this.options.disableMultipleSelection(disableMultipleSelection);
            return this;
        }

        isMultipleSelectionDisabled(): boolean {
            return this.options.isMultipleSelectionDisabled();
        }

        setHotkeysEnabled(enabled: boolean): TreeGridBuilder<NODE> {
            this.hotkeysEnabled = enabled;
            return this;
        }

        isHotkeysEnabled(): boolean {
            return this.hotkeysEnabled;
        }

        setPartialLoadEnabled(enabled: boolean): TreeGridBuilder<NODE> {
            this.partialLoadEnabled = enabled;
            return this;
        }

        isPartialLoadEnabled(): boolean {
            return this.partialLoadEnabled;
        }

        setLoadBufferSize(loadBufferSize: number): TreeGridBuilder<NODE> {
            this.loadBufferSize = loadBufferSize;
            return this;
        }

        getLoadBufferSize(): number {
            return this.loadBufferSize;
        }

        /**
         Should be overriden by child class.
         */
        build(): TreeGrid<NODE> {
            return new TreeGrid<NODE>(this);
        }

    }
}
