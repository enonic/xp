module api.ui.treegrid {

    import Item = api.item.Item;

    import Element = api.dom.Element;
    import ElementHelper = api.dom.ElementHelper;
    import ValidationRecordingViewer = api.form.ValidationRecordingViewer;

    import Grid = api.ui.grid.Grid;
    import GridOptions = api.ui.grid.GridOptions;
    import GridColumn = api.ui.grid.GridColumn;
    import GridOptionsBuilder = api.ui.grid.GridOptionsBuilder;
    import DataView = api.ui.grid.DataView;
    import KeyBinding = api.ui.KeyBinding;
    import KeyBindings = api.ui.KeyBindings;

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import TreeGridToolbarActions = api.ui.treegrid.actions.TreeGridToolbarActions;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    /*
     * There are several methods that should be overridden:
     * 1. hasChildren(data: DATA)  -- Should be implemented if a grid has a tree structure and supports expand/collapse.
     * 2. fetch(data?: DATA) -- Should fetch full data with a valid hasChildren() value;
     * 3. fetchChildren(parentData?: DATA) -- Should fetch children of a parent data;
     * 4. fetchRoot() -- Fetches root nodes. by default return fetchChildren() with an empty parameter.
     */
    export class TreeGrid<DATA> extends api.ui.panel.Panel {

        public static LEVEL_STEP_INDENT: number = 16;

        private columns: GridColumn<DATA>[] = [];

        private gridOptions: GridOptions<DATA>;

        private grid: Grid<TreeNode<DATA>>;

        private gridData: DataView<TreeNode<DATA>>;

        private root: TreeRoot<DATA>;

        private toolbar: TreeGridToolbar;

        private contextMenu: TreeGridContextMenu;

        private expandAll: boolean;

        private active: boolean;

        private loadedListeners: Function[] = [];

        private contextMenuListeners: Function[] = [];

        private selectionChangeListeners: Function[] = [];

        private dataChangeListeners: {(event: DataChangedEvent<DATA>):void}[] = [];

        private activeChangedListeners: {(active: boolean): void}[] = [];

        private loadBufferSize: number;

        private loading: boolean = false;

        private scrollable: api.dom.Element;

        private quietErrorHandling: boolean;

        private errorPanel: ValidationRecordingViewer;

        private highlightedRow: JQuery;

        private highlightedNode: TreeNode<DATA>;

        constructor(builder: TreeGridBuilder<DATA>) {

            super(builder.getClasses());

            this.expandAll = builder.isExpandAll();
            this.quietErrorHandling = builder.getQuietErrorHandling();

            // root node with undefined item
            this.root = new TreeRoot<DATA>();

            this.gridData = new DataView<TreeNode<DATA>>();
            this.gridData.setFilter((node: TreeNode<DATA>) => {
                return node.isVisible();
            });
            this.gridData.setItemMetadataHandler(this.handleItemMetadata.bind(this));


            this.columns = this.updateColumnsFormatter(builder.getColumns());

            this.gridOptions = builder.getOptions();

            this.grid = new Grid<TreeNode<DATA>>(this.gridData, this.columns, this.gridOptions);

            // Custom row selection required for valid behaviour
            this.grid.setSelectionModel(new Slick.RowSelectionModel({
                selectActiveRow: false
            }));

            /*
             * Default checkbox plugin should be unselected, because the
             * cell navigation is disabled. Enabling it will break the
             * key custom key navigation. Without it plugin is having
             * some spacebar handling error, due to active cell can't be set.
             */
            var selectorPlugin = this.grid.getCheckboxSelectorPlugin();
            if (selectorPlugin) {
                this.grid.unregisterPlugin(this.grid.getCheckboxSelectorPlugin())
            }

            this.grid.syncGridSelection(false);

            if (builder.getContextMenu()) {
                this.setContextMenu(builder.getContextMenu());
            }

            if (builder.isShowToolbar()) {
                this.toolbar = new TreeGridToolbar(new TreeGridToolbarActions(this), this);
                this.appendChild(this.toolbar);
                // make sure it won't left from the cloned grid
                this.removeClass("no-toolbar");
            } else {
                this.addClass("no-toolbar");
            }

            this.appendChild(this.grid);

            if (builder.isAutoLoad()) {
                this.onAdded(() => {
                    this.reload().then(() => this.grid.resizeCanvas());
                });
            }

            this.initEventListeners(builder);

        }

        private initEventListeners(builder: TreeGridBuilder<DATA>) {

            let keyBindings = [];
            let interval;

            this.onClicked(() => {
                this.grid.focus();
            });

            if (builder.isPartialLoadEnabled()) {

                this.loadBufferSize = builder.getLoadBufferSize();
                this.onRendered(() => {
                    if (interval) {
                        clearInterval(interval);
                    }
                    interval = setInterval(this.postLoad.bind(this), 200);
                });
            }

            this.grid.subscribeOnClick((event, data) => {
                if (!this.isActive()) {
                    return;
                }

                if (this.contextMenu) {
                    this.contextMenu.hide();
                }

                this.setActive(false);
                var elem = new ElementHelper(event.target);
                let clickedRow = wemjq(elem.getHTMLElement()).closest(".slick-row");
                let isRowHighlighted = clickedRow.hasClass("selected");

                if (elem.hasClass("expand")) {
                    elem.removeClass("expand").addClass("collapse");
                    var node = this.gridData.getItem(data.row);
                    this.expandNode(node).then(() => {
                        if (isRowHighlighted) {
                            this.highlightRowByNode(node);
                        }
                    });

                    return;
                }

                if (elem.hasClass("collapse")) {
                    elem.removeClass("collapse").addClass("expand");
                    var node = this.gridData.getItem(data.row);
                    this.collapseNode(node);
                    if (isRowHighlighted) {
                        this.highlightRowByNode(node);
                    }

                    return;
                }

                let isMultiSelect = clickedRow.children().is(".slick-cell-checkboxsel");

                this.setActive(true);

                if (elem.hasAnyParentClass("slick-cell-checkboxsel")) {
                    if (elem.getAttribute("type") !== "checkbox") {
                        return;
                    }

                    if (!isMultiSelect) {
                        this.grid.toggleRow(data.row);
                        return;
                    }

                    this.grid.toggleRow(data.row);

                    if (this.grid.getSelectedRows().length > 1) {
                        this.unhighlightRows();
                    }
                    else if (this.grid.isRowSelected(data.row)) {
                        this.highlightRow(clickedRow);
                    }

                    return;
                }
                
                // A cell in the row is clicked
                if (isMultiSelect) {

                    if (this.grid.getSelectedRows().length > 0 || isRowHighlighted) {
                        this.unselectAllRows();
                    }

                    if (!isRowHighlighted) {
                        this.highlightRow(clickedRow);
                    }
                }
                else {
                    this.root.clearStashedSelection();
                    let repeatedSelection = this.grid.selectRow(data.row) === -1;

                    if (!elem.hasClass("sort-dialog-trigger")) {
                        new TreeGridItemClickedEvent(repeatedSelection).fire();
                    }
                }
            });

            if (this.quietErrorHandling) {
                this.appendChild(this.errorPanel = new api.form.ValidationRecordingViewer());
                this.errorPanel.hide();
            }

            this.grid.onShown(() => {
                this.scrollable = this.queryScrollable();
            });

            this.onShown(() => {
                this.grid.resizeCanvas();
                if (builder.isHotkeysEnabled()) {

                    if (!this.gridOptions.isMultipleSelectionDisabled()) {
                        keyBindings = [
                            new KeyBinding('shift+up', (event: ExtendedKeyboardEvent) => {
                                if (this.isActive()) {
                                    this.scrollToRow(this.grid.addSelectedUp());
                                }
                                event.preventDefault();
                                event.stopImmediatePropagation();
                            }),
                            new KeyBinding('shift+down', (event: ExtendedKeyboardEvent) => {
                                if (this.isActive()) {
                                    this.scrollToRow(this.grid.addSelectedDown());
                                }
                                event.preventDefault();
                                event.stopImmediatePropagation();
                            })
                        ];
                    }

                    keyBindings = keyBindings.concat([
                        new KeyBinding('up', () => {
                            if (this.isActive()) {

                                this.navigateUp();

                                if (this.grid.navigateUp()) {
                                    this.unhighlightRow(this.highlightedRow);
                                    let cell = this.grid.getActiveCell();
                                }
                                //this.scrollToRow(this.grid.moveSelectedUp());
                            }
                        }),
                        new KeyBinding('down', () => {
                            if (this.isActive()) {
                                if (this.grid.navigateDown()) {
                                    this.unhighlightRow(this.highlightedRow);
                                    let cell = this.grid.getActiveCell();
                                }
                                //this.scrollToRow(this.grid.moveSelectedDown());
                            }
                        }),
                        new KeyBinding('left', () => {
                            var selected = this.grid.getSelectedRows();
                            if (selected.length === 1) {
                                var node = this.gridData.getItem(selected[0]);
                                if (node && this.isActive()) {
                                    if (node.isExpanded()) {
                                        this.setActive(false);
                                        this.collapseNode(node);
                                    } else if (node.getParent() !== this.root.getCurrentRoot()) {
                                        node = node.getParent();
                                        this.setActive(false);
                                        var row = this.gridData.getRowById(node.getId());
                                        this.grid.selectRow(row);
                                        this.collapseNode(node);
                                    }
                                }
                            }
                        }),
                        new KeyBinding('right', () => {
                            var selected = this.grid.getSelectedRows();
                            if (selected.length === 1) {
                                var node = this.gridData.getItem(selected[0]);
                                if (node && this.hasChildren(node.getData())
                                    && !node.isExpanded() && this.isActive()) {

                                    this.setActive(false);
                                    this.invalidate();
                                    this.expandNode(node);
                                }
                            }
                        }),
                        new KeyBinding('space', () => {
                            this.deselectAll();
                        })
                    ]);

                    KeyBindings.get().bindKeys(keyBindings);
                }
            });

            this.onRendered(() => {
                this.grid.resizeCanvas();
            });

            this.onRemoved(() => {
                if (builder.isHotkeysEnabled()) {
                    KeyBindings.get().unbindKeys(keyBindings);
                }

                if (builder.isPartialLoadEnabled() && interval) {
                    clearInterval(interval);
                }
            });

            this.grid.subscribeOnSelectedRowsChanged((event, rows) => {
                this.notifySelectionChanged(event, rows.rows);
            });

            this.onLoaded(() => this.unmask());
        }

        private navigateUp() {
            if (!this.highlightedRow) {

            }
        }

        private highlightRowByNode(node: TreeNode<DATA>) {
            let rowIndex = this.gridData.getRowById(node.getId());
            let cell = this.grid.getCellNode(rowIndex, 0);
            let clickedRow = wemjq(cell).closest(".slick-row");

            this.highlightRow(clickedRow);
        }

        private highlightRow(row: JQuery, skipEvent: boolean = false) {

            if (!!this.highlightedRow && this.highlightedRow === row) {
                return;
            }

            if (!!this.highlightedRow && this.highlightedRow !== row) {
                this.unhighlightCurrentRow();
            }

            row.addClass("selected");
            this.highlightedRow = row;
            //Fire event here to notify preview panel
        }

        private unhighlightCurrentRow() {
            this.unhighlightRow(this.highlightedRow);
        }

        private unhighlightRow(row) {
            if (!row) {
                return;
            }
            row.removeClass("selected");
            this.highlightedRow = null;
            //Fire event here to notify preview panel
        }

        private unhighlightRows() {
            wemjq(this.grid.getHTMLElement()).find(".slick-row.selected").removeClass("selected");
            this.highlightedRow = null;
        }

        private unselectAllRows() {
            this.unhighlightRows();
            this.grid.clearSelection();
        }

        public setContextMenu(contextMenu: TreeGridContextMenu) {
            this.contextMenu = contextMenu;
            this.grid.subscribeOnContextMenu((event) => {
                event.preventDefault();
                this.setActive(false);
                var cell = this.grid.getCellFromEvent(event);
                this.unhighlightRows();
                this.grid.selectRow(cell.row);
                this.contextMenu.showAt(event.pageX, event.pageY);
                this.notifyContextMenuShown(event.pageX, event.pageY);
                this.setActive(true);
            });

        }

        public isInRenderingView(): boolean {
            // TreeGrid in visible tab or TreeGrid is active
            return this.isVisible() && this.isActive();
        }

        private updateColumnsFormatter(columns: GridColumn<TreeNode<DATA>>[]) {
            if (columns.length > 0) {
                var formatter = columns[0].getFormatter();
                var toggleFormatter = (row: number, cell: number, value: any, columnDef: any, node: TreeNode<DATA>) => {
                    var toggleSpan = new api.dom.SpanEl("toggle icon");
                    if (this.hasChildren(node.getData())) {
                        var toggleClass = node.isExpanded() ? "collapse" : "expand";
                        toggleSpan.addClass(toggleClass);
                    }
                    toggleSpan.getEl().setMarginLeft(TreeGrid.LEVEL_STEP_INDENT * (node.calcLevel() - 1) + "px");

                    return toggleSpan.toString() + formatter(row, cell, value, columnDef, node);
                };

                columns[0].setFormatter(toggleFormatter);
            }

            return columns;
        }

        isEmptyNode(node: TreeNode<DATA>): boolean {
            return false;
        }

        getEmptyNodesCount(): number {

            var viewportRange = this.grid.getViewport(),
                lastIndex = this.gridData.getItems().length - 1,
            // first and last rows, that are visible in grid
                firstVisible = viewportRange.top,
            // interval borders to search for the empty node
                from = firstVisible;
            var emptyNodesCount = 0;

            for (var i = from; i <= lastIndex; i++) {
                if (!!this.gridData.getItem(i) && this.gridData.getItem(i).getDataId() === "") {
                    emptyNodesCount++;
                }
            }

            return emptyNodesCount;

        }

        mask() {
            this.grid.mask();
        }

        unmask() {
            this.grid.unmask();
        }

        getGrid(): Grid<TreeNode<DATA>> {
            return this.grid;
        }

        getOptions(): GridOptions<DATA> {
            return this.gridOptions;
        }

        getColumns(): GridColumn<TreeNode<DATA>>[] {
            return this.grid.getColumns();
        }

        getContextMenu(): TreeGridContextMenu {
            return this.contextMenu;
        }

        getRoot(): TreeRoot<DATA> {
            return this.root;
        }

        isNewlySelected(): boolean {
            return this.getRoot().isNewlySelected();
        }

        isActive(): boolean {
            return this.active;
        }

        setActive(active: boolean = true) {
            if (this.active != active) {
                this.active = active;
                this.notifyActiveChanged(active);
            }
        }

        onActiveChanged(listener: (active: boolean) => void) {
            this.activeChangedListeners.push(listener);
        }

        unActiveChanged(listener: (active: boolean) => void) {
            this.activeChangedListeners = this.activeChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyActiveChanged(active: boolean) {
            this.activeChangedListeners.forEach((listener) => {
                listener(active);
            })
        }

        getToolbar(): TreeGridToolbar {
            return this.toolbar;
        }

        hasToolbar(): boolean {
            return !!this.toolbar;
        }

        scrollToRow(row: number) {
            if (!this.scrollable) {
                // not present until shown
                return;
            }
            var scrollEl = this.scrollable.getEl();

            if (row > -1 && this.grid.getSelectedRows().length > 0) {
                if (scrollEl.getScrollTop() > row * 45) {
                    scrollEl.setScrollTop(row * 45);
                } else if (scrollEl.getScrollTop() + scrollEl.getHeight() < (row + 1) * 45) {
                    scrollEl.setScrollTop((row + 1) * 45 - scrollEl.getHeight());
                }
            }
        }

        queryScrollable(): api.dom.Element {
            var gridClasses = (" " + this.grid.getEl().getClass()).replace(/\s/g, ".");
            var viewport = Element.fromString(".tree-grid " + gridClasses + " .slick-viewport", false);
            return viewport;
        }

        private loadEmptyNode(node: TreeNode<DATA>) {
            if (!this.getDataId(node.getData())) {
                this.fetchChildren(node.getParent()).then((dataList: DATA[]) => {
                    var oldChildren = node.getParent().getChildren();
                    // Ensure to remove empty node from the end if present
                    if (oldChildren.length > 0 && oldChildren[oldChildren.length - 1].getDataId() === "") {
                        oldChildren.pop();
                    }
                    var fetchedChildren = this.dataToTreeNodes(dataList, node.getParent());
                    var needToCheckFetchedChildren = this.areAllOldChildrenSelected(oldChildren);
                    var newChildren = oldChildren.concat(fetchedChildren.slice(oldChildren.length));
                    node.getParent().setChildren(newChildren);
                    this.initData(this.root.getCurrentRoot().treeToList());
                    if (needToCheckFetchedChildren) {
                        this.select(fetchedChildren);
                    }
                    else {
                        this.triggerSelectionChangedListeners();
                    }
                }).catch((reason: any) => {
                    this.handleError(reason);
                }).then(() => {
                    this.notifyLoaded();
                    this.loading = false;
                });
            }
        }

        private select(fetchedChildren: TreeNode<DATA>[]) {
            var rowsToSelect: number[] = [];
            fetchedChildren.forEach((node: TreeNode<DATA>) => {
                var row = this.gridData.getRowById(node.getId());
                if (row) {
                    rowsToSelect.push(row);
                }
            });
            this.grid.addSelectedRows(rowsToSelect);
        }

        private areAllOldChildrenSelected(oldChildren: TreeNode<DATA>[]): boolean {
            if (oldChildren && oldChildren.length > 0) {
                return oldChildren.every(node =>
                        this.grid.isRowSelected(this.gridData.getRowById(node.getId()))
                );
            } else {
                return false
            }
        }

        private postLoad() {
            // Skip if not visible or active (is loading something)
            const disabled = !this.isInRenderingView() || this.loading;

            if (disabled) {
                return;
            }

            const viewportRange = this.grid.getViewport();
            const lastIndex = this.gridData.getItems().length - 1;
            // first and last rows, that are visible in grid
            const firstVisible = viewportRange.top;
            const lastVisible = Math.min(viewportRange.bottom, lastIndex);
            // interval borders to search for the empty node
            const from = firstVisible;
            const to = Math.min(lastVisible + this.loadBufferSize, lastIndex);

            for (let i = from; i <= to; i++) {
                if (this.gridData.getItem(i) && this.gridData.getItem(i).getDataId() === "") {
                    this.loading = true;
                    this.loadEmptyNode(this.gridData.getItem(i));
                    break;
                }
            }
        }

        /**
         * Used to determine if a data have child nodes.
         * Must be overridden for the grids with a tree structure.
         */
        hasChildren(data: DATA): boolean {
            return false;
        }

        /**
         * Used to get the data identifier or key.
         * Must be overridden.
         */
        getDataId(data: DATA): string {
            throw new Error("Must be implemented by inheritors");
        }

        /**
         * Fetches a single element.
         * Can be used to update/add a single node without
         * retrieving a a full data, or for the purpose of the
         * infinite scroll.
         */
        fetch(node: TreeNode<DATA>, dataId?: string): wemQ.Promise<DATA> {
            var deferred = wemQ.defer<DATA>();
            // Empty logic
            deferred.resolve(null);
            return deferred.promise;
        }

        /**
         * Used as a default children fetcher.
         * Must be overridden to use predefined root nodes.
         */
        fetchChildren(parentNode?: TreeNode<DATA>): wemQ.Promise<DATA[]> {
            var deferred = wemQ.defer<DATA[]>();
            // Empty logic
            deferred.resolve([]);
            return deferred.promise;
        }

        /**
         * Used as a default root fetcher.
         * Can be overridden to use predefined root nodes.
         * By default, return empty fetchChildren request.
         */
        fetchRoot(): wemQ.Promise<DATA[]> {
            return this.fetchChildren();
        }

        private fetchData(parentNode?: TreeNode<DATA>): wemQ.Promise<DATA[]> {
            return parentNode ? this.fetchChildren(parentNode) : this.fetchRoot();
        }

        dataToTreeNode(data: DATA, parent: TreeNode<DATA>): TreeNode<DATA> {
            return new TreeNodeBuilder<DATA>().
                setData(data, this.getDataId(data)).
                setExpanded(this.expandAll).
                setParent(parent).
                build();
        }

        dataToTreeNodes(dataArray: DATA[], parent: TreeNode<DATA>): TreeNode<DATA>[] {
            var nodes: TreeNode<DATA>[] = [];
            dataArray.forEach((data) => {
                nodes.push(this.dataToTreeNode(data, parent));
            });
            return nodes;
        }

        filter(dataList: DATA[]) {
            this.setActive(false);
            this.root.setFiltered(true);
            this.root.getCurrentRoot().setChildren(this.dataToTreeNodes(dataList, this.root.getCurrentRoot()));
            this.initData(this.root.getCurrentRoot().treeToList());
            this.invalidate();
            this.setActive(true);
        }

        resetFilter() {
            this.setActive(false);

            if (this.root.isFiltered()) {
                this.root.setFiltered(false);
                this.initData(this.root.getCurrentRoot().treeToList());
                this.invalidate();
                this.setActive(true);
                this.notifyLoaded();
            } else {
                // replace with refresh in future
                this.reload();
            }
        }

        selectNode(dataId: string) {
            var root = this.root.getCurrentRoot(),
                node = root.findNode(dataId);

            if (node) {
                var row = this.gridData.getRowById(node.getId());
                this.grid.selectRow(row);
            }
        }

        refreshNodeById(dataId: string) {
            var root = this.root.getCurrentRoot(),
                node = root.findNode(dataId);

            if (node) {
                this.refreshNode(node);
            }
        }

        selectAll() {
            var rows = [];
            for (var i = 0; i < this.gridData.getLength(); i++) {
                if (!api.util.StringHelper.isEmpty(this.gridData.getItem(i).getDataId())) {
                    rows.push(i);
                }
            }
            this.grid.setSelectedRows(rows);
        }

        deselectAll() {
            this.grid.clearSelection();
        }

        deselectNodes(dataIds: string[]) {
            var oldSelected = this.root.getFullSelection(),
                newSelected = [],
                newSelectedRows = [];

            for (var i = 0; i < oldSelected.length; i++) {
                if (dataIds.indexOf(oldSelected[i].getDataId()) < 0) {
                    newSelected.push(oldSelected[i]);
                    newSelectedRows.push(this.grid.getDataView().getRowById(oldSelected[i].getId()));
                }
            }

            this.root.removeSelections(dataIds);

            if (oldSelected.length !== newSelected.length) {
                this.grid.setSelectedRows(newSelectedRows);
            }
        }

        getSelectedNodes(): TreeNode<DATA>[] {
            return this.grid.getSelectedRowItems();
        }

        getSelectedDataList(): DATA[] {
            return this.root.getFullSelection().map((node: TreeNode<DATA>) => {
                return node.getData();
            });
        }

        // Hard reset

        reload(parentNodeData?: DATA): wemQ.Promise<void> {
            var expandedNodesDataId = this.grid.getDataView().getItems().filter((item) => {
                return item.isExpanded();
            }).map((item) => {
                return item.getDataId();
            });

            var selection = this.root.getCurrentSelection();

            this.root.resetCurrentRoot(parentNodeData);
            this.initData([]);

            this.mask();

            return this.reloadNode(null, expandedNodesDataId)
                .then(() => {
                    this.root.setCurrentSelection(selection);
                    this.initData(this.root.getCurrentRoot().treeToList());
                    this.updateExpanded();
                }).catch((reason: any) => {
                    this.handleError(reason);
                }).then(() => {
                    this.updateExpanded();
                }).then(() => this.notifyLoaded());
        }

        protected handleError(reason: any, message?: String) {
            this.grid.show();
            if (this.quietErrorHandling) {
                this.errorPanel.setError(message || reason);
                this.grid.hide();
                this.errorPanel.show();
            }
            else {
                api.DefaultErrorHandler.handle(reason);
            }
        }

        protected hideErrorPanel() {
            this.grid.show();
            if (this.quietErrorHandling) {
                this.errorPanel.hide();
            }
        }

        private reloadNode(parentNode?: TreeNode<DATA>, expandedNodesDataId?: String[]): wemQ.Promise<void> {

            var deferred = wemQ.defer<void>(),
                promises = [];

            this.fetchData(parentNode).then((dataList: DATA[]) => {
                var hasNotEmptyChildren = false;

                parentNode = parentNode || this.root.getCurrentRoot();
                parentNode.getChildren().length = 0;

                dataList.forEach((data: DATA) => {
                    var child = this.dataToTreeNode(data, parentNode);
                    var dataId = this.getDataId(data);
                    child.setExpanded(this.expandAll || expandedNodesDataId.indexOf(dataId) > -1);
                    parentNode.addChild(child);

                    if (child.isExpanded() && this.hasChildren(data)) {
                        hasNotEmptyChildren = true;
                        promises.push(this.reloadNode(child, expandedNodesDataId));
                    }
                });

                if (!hasNotEmptyChildren) {
                    deferred.resolve(null);
                } else {
                    wemQ.all(promises).spread(() => {
                        deferred.resolve(null);
                    }).catch((reason: any) => {
                        deferred.reject(reason);
                    }).done();
                }
            }).catch((reason: any) => {
                this.handleError(reason);
                deferred.reject(reason);
            }).done();

            return deferred.promise;
        }

        refreshNode(node?: TreeNode<DATA>): void {
            var root = this.root.getCurrentRoot();
            this.setActive(false);

            node = node || root;
            node.regenerateIds();
            root.setExpanded(true);
            this.initData(root.treeToList());

            this.invalidate();

            this.setActive(true);

            this.notifyLoaded();
        }

        // Soft reset, that saves node status
        refresh(): void {
            var root = this.root.getCurrentRoot();

            this.setActive(false);

            this.grid.invalidate();

            root.setExpanded(true);
            this.initData(root.treeToList());
            this.invalidate();

            this.setActive(true);

            this.notifyLoaded();
        }

        updateNode(data: DATA, oldDataId?: string): wemQ.Promise<void> {

            var dataId = oldDataId || this.getDataId(data),
                nodeToUpdate = this.root.getCurrentRoot().findNode(dataId);

            if (!nodeToUpdate) {
                throw new Error("TreeNode to update not found: " + dataId);
            }

            return this.fetchAndUpdateNodes([nodeToUpdate], oldDataId ? this.getDataId(data) : undefined);
        }

        updateNodes(data: DATA, oldDataId?: string): wemQ.Promise<void> {

            var dataId = oldDataId || this.getDataId(data),
                nodesToUpdate = this.root.getCurrentRoot().findNodes(dataId);

            if (!nodesToUpdate) {
                throw new Error("TreeNode to update not found: " + dataId);
            }

            return this.fetchAndUpdateNodes(nodesToUpdate, oldDataId ? this.getDataId(data) : undefined);
        }

        private fetchAndUpdateNodes(nodesToUpdate: TreeNode<DATA>[], dataId?: string): wemQ.Promise<void> {
            return this.fetch(nodesToUpdate[0], dataId)
                .then((data: DATA) => {
                    nodesToUpdate.forEach((node) => {
                        if (dataId) {
                            node.setDataId(dataId);
                        }
                        node.setData(data);
                        if (this.expandAll) {
                            node.setExpanded(this.expandAll);
                        }
                        node.setDataId(this.getDataId(data));
                        node.clearViewers();

                        if (node.isVisible()) {
                            var selected = this.grid.isRowSelected(this.gridData.getRowById(node.getId()));
                            this.gridData.updateItem(node.getId(), node);
                            if (selected) {
                                this.grid.addSelectedRow(this.gridData.getRowById(node.getId()));
                            }
                        }
                    });

                }).catch((reason: any) => {
                    this.handleError(reason);
                });
        }

        deleteNode(data: DATA): void {
            this.deleteRootNode(this.root.getDefaultRoot(), data);
            if (this.root.isFiltered()) {
                this.deleteRootNode(this.root.getFilteredRoot(), data);
            }
        }

        private deleteRootNode(root: TreeNode<DATA>, data: DATA): void {
            var dataId = this.getDataId(data),
                node: TreeNode<DATA>;

            while (node = root.findNode(dataId)) {
                if (node.hasChildren()) {
                    node.getChildren().forEach((child: TreeNode<DATA>) => {
                        this.deleteNode(child.getData());
                    });
                }
                if (this.gridData.getItemById(node.getId())) {
                    this.gridData.deleteItem(node.getId());
                }

                var parent = node.getParent();
                if (node && parent) {
                    parent.removeChild(node);
                    parent.setMaxChildren(parent.getMaxChildren() - 1);
                    this.notifyDataChanged(new DataChangedEvent<DATA>([node], DataChangedEvent.DELETED));
                }
            }

            this.root.removeSelections([dataId]);
        }

        /**
         * @param data
         * @param nextToSelection - by default node is appended as child to selection or root, set this to true to append to the same level
         * @param stashedParentNode
         */
        appendNode(data: DATA, nextToSelection: boolean = false, prepend: boolean = true,
                   stashedParentNode?: TreeNode<DATA>): wemQ.Promise<void> {
            var parentNode = this.getParentNode(nextToSelection, stashedParentNode);
            var index = prepend ? 0 :  Math.max(0, parentNode.getChildren().length - 1);
            return this.insertNode(data, nextToSelection, index, stashedParentNode);
        }

        getParentNode(nextToSelection: boolean = false, stashedParentNode?: TreeNode<DATA>) {
            var root = stashedParentNode || this.root.getCurrentRoot();
            var parentNode: TreeNode<DATA>;
            if (this.getSelectedNodes() && this.getSelectedNodes().length == 1) {
                parentNode = root.findNode(this.getSelectedNodes()[0].getDataId());
                if (nextToSelection) {
                    parentNode = parentNode.getParent() || this.root.getCurrentRoot();
                }
            } else {
                parentNode = root;
            }
            return parentNode;
        }

        insertNode(data: DATA, nextToSelection: boolean = false, index: number = 0,
                   stashedParentNode?: TreeNode<DATA>): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();
            var root = stashedParentNode || this.root.getCurrentRoot();
            var parentNode = this.getParentNode(nextToSelection, stashedParentNode);

            var isRootParentNode: boolean = (parentNode == root);

            if (!parentNode.hasChildren() && !isRootParentNode) {
                this.fetchData(parentNode)
                    .then((dataList: DATA[]) => {
                        if (parentNode.hasChildren()) {
                            this.doInsertNodeToParentWithChildren(parentNode, data, root, index, stashedParentNode, isRootParentNode);

                        } else {
                            parentNode.setChildren(this.dataToTreeNodes(dataList, parentNode));
                            this.initData(root.treeToList());
                            var node = root.findNode(this.getDataId(data));
                            if (!node) {
                                parentNode.insertChild(this.dataToTreeNode(data, root), index);
                                node = root.findNode(this.getDataId(data));
                            }

                            if (node) {
                                if (!stashedParentNode) {
                                    this.gridData.setItems(root.treeToList());
                                }
                                this.notifyDataChanged(new DataChangedEvent<DATA>([node], DataChangedEvent.ADDED));

                                if (parentNode != root) {
                                    this.refreshNodeData(parentNode).then((node: TreeNode<DATA>) => {
                                        if (!stashedParentNode) {
                                            this.updateSelectedNode(node);
                                        }
                                    });
                                }
                            }
                        }
                        deferred.resolve(null);
                    }).catch((reason: any) => {
                        this.handleError(reason);
                        deferred.reject(reason);
                    });
            } else {
                this.doInsertNodeToParentWithChildren(parentNode, data, root, index, stashedParentNode, isRootParentNode);
                deferred.resolve(null);
            }

            return deferred.promise;
        }

        private doInsertNodeToParentWithChildren(parentNode, data, root, index: number, stashedParentNode, isRootParentNode) {
            parentNode.insertChild(this.dataToTreeNode(data, root), index);

            var node = root.findNode(this.getDataId(data));
            if (node) {
                if (!stashedParentNode) {
                    this.gridData.setItems(root.treeToList());
                }
                if (isRootParentNode) {
                    this.sortNodeChildren(parentNode);
                } else {
                    if (!stashedParentNode) {
                        this.updateSelectedNode(parentNode);
                    }
                }
            }
        }

        deleteNodes(dataList: DATA[]): void {
            this.deleteRootNodes(this.root.getDefaultRoot(), dataList);
            if (this.root.isFiltered()) {
                this.deleteRootNodes(this.root.getFilteredRoot(), dataList);
            }
        }

        private deleteRootNodes(root: TreeNode<DATA>, dataList: DATA[]): void {
            var updated: TreeNode<DATA>[] = [],
                deleted: TreeNode<DATA>[] = [];

            dataList.forEach((data: DATA) => {
                var node = root.findNode(this.getDataId(data));
                if (node && node.getParent()) {
                    var parent = node.getParent();
                    this.deleteRootNode(root, node.getData());
                    updated.push(parent);
                    deleted.push(node);
                    updated.filter((el) => {
                        return el.getDataId() !== node.getDataId();
                    });
                }
            });
            root.treeToList().forEach((child: TreeNode<DATA>) => {
                this.refreshNodeData(child);
            });
            this.notifyDataChanged(new DataChangedEvent<DATA>(deleted, DataChangedEvent.DELETED));
        }


        initData(nodes: TreeNode<DATA>[]) {
            this.gridData.setItems(nodes, "id");
            this.notifyDataChanged(new DataChangedEvent<DATA>(nodes, DataChangedEvent.ADDED));
            this.resetCurrentSelection(nodes);
        }

        private resetCurrentSelection(nodes: TreeNode<DATA>[]) {
            var selection: any = [],
                selectionIds = this.root.getFullSelection().map((el) => {
                    return el.getDataId();
                });

            selectionIds.forEach((selectionId) => {
                nodes.forEach((node, index) => {
                    if (node.getDataId() === selectionId) {
                        selection.push(index);
                    }
                });
            });

            this.grid.setSelectedRows(selection);
        }

        expandNode(node?: TreeNode<DATA>, expandAll: boolean = false): wemQ.Promise<boolean> {
            var deferred = wemQ.defer<boolean>();
            
            node = node || this.root.getCurrentRoot();

            if (node) {
                node.setExpanded(true);

                if (node.hasChildren()) {
                    this.initData(this.root.getCurrentRoot().treeToList());
                    this.updateExpanded();
                    if (expandAll) {
                        node.getChildren().forEach((child: TreeNode<DATA>) => {
                            this.expandNode(child);
                        });
                    }
                    deferred.resolve(true);
                } else {
                    this.mask();
                    this.fetchData(node)
                        .then((dataList: DATA[]) => {
                            node.setChildren(this.dataToTreeNodes(dataList, node));
                            this.initData(this.root.getCurrentRoot().treeToList());
                            this.updateExpanded();
                            if (expandAll) {
                                node.getChildren().forEach((child: TreeNode<DATA>) => {
                                    this.expandNode(child);
                                });
                            }
                            deferred.resolve(true);
                        }).catch((reason: any) => {
                            this.handleError(reason);
                        deferred.resolve(false);
                        }).finally(() => {
                        }).done(() => this.notifyLoaded());
                }
            }

            return deferred.promise;
        }

        protected updateExpanded() {
            this.invalidate();
            this.setActive(true);
        }

        private updateSelectedNode(node: TreeNode<DATA>) {
            this.getGrid().clearSelection();
            this.refreshNode(node);
            var row = this.gridData.getRowById(node.getId());
            this.grid.selectRow(row);
        }

        private collapseNode(node: TreeNode<DATA>) {
            node.setExpanded(false);

            // Save the selected collapsed rows in cache
            this.root.stashSelection();

            this.gridData.refresh();
            this.invalidate();
            this.triggerSelectionChangedListeners();
            this.setActive(true);
        }

        notifyLoaded(): void {
            this.loadedListeners.forEach((listener) => {
                listener(this);
            });
        }

        onLoaded(listener: () => void) {
            this.loadedListeners.push(listener);
            return this;
        }

        unLoaded(listener: () => void) {
            this.loadedListeners = this.loadedListeners.filter((curr) => {
                return curr != listener;
            });
            return this;
        }

        getItem(rowIndex: number): TreeNode<DATA> {
            return this.gridData.getItem(rowIndex);
        }

        private notifySelectionChanged(event: any, rows: number[]): void {
            var currentSelection: TreeNode<DATA>[] = [];
            if (rows) {
                rows.forEach((rowIndex) => {
                    currentSelection.push(this.gridData.getItem(rowIndex));
                });
            }

            this.root.setCurrentSelection(currentSelection);

            this.triggerSelectionChangedListeners();
        }

        triggerSelectionChangedListeners() {
            for (var i in this.selectionChangeListeners) {
                this.selectionChangeListeners[i](this.root.getCurrentSelection(), this.root.getFullSelection());
            }
        }

        onSelectionChanged(listener: (currentSelection: TreeNode<DATA>[], fullSelection: TreeNode<DATA>[]) => void) {
            this.selectionChangeListeners.push(listener);
            return this;
        }

        unSelectionChanged(listener: (currentSelection: TreeNode<DATA>[], fullSelection: TreeNode<DATA>[]) => void) {
            this.selectionChangeListeners = this.selectionChangeListeners.filter((curr) => {
                return curr != listener;
            });
            return this;
        }

        private notifyContextMenuShown(x: number, y: number) {
            var showContextMenuEvent = new ContextMenuShownEvent(x, y);
            this.contextMenuListeners.forEach((listener) => {
                listener(showContextMenuEvent);
            });
        }

        protected getErrorPanel(): ValidationRecordingViewer {
            return this.errorPanel;
        }

        onContextMenuShown(listener: () => void) {
            this.contextMenuListeners.push(listener);
            return this;
        }

        unContextMenuShown(listener: () => void) {
            this.contextMenuListeners = this.contextMenuListeners.filter((curr) => {
                return curr != listener;
            });
            return this;
        }

        notifyDataChanged(event: DataChangedEvent<DATA>) {
            this.dataChangeListeners.forEach((listener) => {
                listener(event);
            });
        }

        onDataChanged(listener: (event: DataChangedEvent<DATA>) => void) {
            this.dataChangeListeners.push(listener);
            return this;
        }

        unDataChanged(listener: (event: DataChangedEvent<DATA>) => void) {
            this.dataChangeListeners = this.dataChangeListeners.filter((curr) => {
                return curr != listener;
            });
            return this;
        }

        isFiltered() {
            return this.root.isFiltered();
        }

        invalidate() {
            this.grid.invalidate();
        }

        initAndRender() {
            this.initData(this.getRoot().getCurrentRoot().treeToList());
            this.invalidate();
        }

        refreshNodeData(parentNode: TreeNode<DATA>): wemQ.Promise<TreeNode<DATA>> {
            return null;
        }

        sortNodeChildren(node: TreeNode<DATA>) {
        }

        protected handleItemMetadata(row: number) {
            const node = this.gridData.getItem(row);
            if (this.isEmptyNode(node)) {
                return {cssClasses: 'empty-node'};
            }

            return null;
        }
    }
}