module api.ui.treegrid {

    import Item = api.item.Item;

    import Element = api.dom.Element;
    import ElementHelper = api.dom.ElementHelper;

    import Grid = api.ui.grid.Grid;
    import GridOptions = api.ui.grid.GridOptions;
    import GridColumn = api.ui.grid.GridColumn;
    import GridOptionsBuilder = api.ui.grid.GridOptionsBuilder;
    import DataView = api.ui.grid.DataView;
    import KeyBinding = api.ui.KeyBinding;
    import KeyBindings = api.ui.KeyBindings;

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import TreeGridToolbarActions = api.ui.treegrid.actions.TreeGridToolbarActions;

    /*
     * There are several methods that should be overridden:
     * 1. hasChildren(data: DATA)  -- Should be implemented if a grid has a tree structure and supports expand/collapse.
     * 2. fetch(data?: DATA) -- Should fetch full data with a valid hasChildren() value;
     * 3. fetchChildren(parentData?: DATA) -- Should fetch children of a parent data;
     * 4. fetchRoot() -- Fetches root nodes. by default return fetchChildren() with an empty parameter.
     */
    export class TreeGrid<DATA> extends api.ui.panel.Panel {

        private columns: GridColumn<DATA>[] = [];

        private gridOptions: GridOptions<DATA>;

        private grid: Grid<TreeNode<DATA>>;

        private gridData: DataView<TreeNode<DATA>>;

        private root: TreeNode<DATA>;

        private stash: TreeNode<DATA>;

        private toolbar: TreeGridToolbar;

        private contextMenu: TreeGridContextMenu;

        private canvasElement: api.dom.Element;

        private active: boolean;

        private actions: TreeGridToolbarActions;

        private loadedListeners: Function[] = [];

        private contextMenuListeners: Function[] = [];

        private selectionChangeListeners: Function[] = [];

        private dataChangeListeners: {(event: DataChangedEvent<DATA>):void}[] = [];

        constructor(builder: TreeGridBuilder<DATA>) {

            super(builder.getClasses());

            // root node with undefined item
            this.root = new TreeNodeBuilder<DATA>().setExpanded(true).build();

            this.gridData = new DataView<TreeNode<DATA>>();
            this.gridData.setFilter((node: TreeNode<DATA>) => {
                return node.isVisible();
            });

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

            this.actions = new TreeGridToolbarActions(this.grid);

            this.onClicked(() => {
                this.grid.focus();
            });

            if (builder.getContextMenu()) {
                this.contextMenu = builder.getContextMenu();
                this.grid.subscribeOnContextMenu((event) => {
                    event.preventDefault();
                    this.active = false;
                    var cell = this.grid.getCellFromEvent(event);
                    this.grid.selectRow(cell.row);
                    this.contextMenu.showAt(event.pageX, event.pageY);
                    this.notifyContextMenuShown(event.pageX, event.pageY);
                    this.active = true;
                });
            }

            this.grid.subscribeOnClick((event, data) => {
                if (this.active) {
                    this.active = false;
                    var elem = new ElementHelper(event.target);
                    if (elem.hasClass("expand")) {
                        elem.removeClass("expand").addClass("collapse");
                        var node = this.gridData.getItem(data.row);
                        this.expandNode(node);
                    } else if (elem.hasClass("collapse")) {
                        this.active = false;
                        elem.removeClass("collapse").addClass("expand");
                        var node = this.gridData.getItem(data.row);
                        this.collapseNode(node);
                    } else if (data.cell === 0) {
                        this.active = true;
                        if (elem.getAttribute("type") === "checkbox") {
                            this.grid.toggleRow(data.row);
                        }
                    } else {
                        this.active = true;
                        this.grid.selectRow(data.row);
                    }
                }
                if (this.contextMenu) {
                    this.contextMenu.hide();
                }
                event.stopPropagation();
            });

            this.canvasElement = Element.fromHtmlElement(this.grid.getHTMLElement(), true);
            for (var i = 0, gridChildren = this.canvasElement.getChildren(); i < gridChildren.length; i++) {
                if (gridChildren[i].hasClass('slick-viewport')) {
                    for (var j = 0, children = gridChildren[i].getChildren(); j < children.length; j++) {
                        if (children[j].hasClass('grid-canvas')) {
                            this.canvasElement = children[j];
                        }
                    }
                }
            }

            if (builder.isShowToolbar()) {
                this.toolbar = new TreeGridToolbar(this.actions);
                this.appendChild(this.toolbar);
                // make sure it won't left from the cloned grid
                this.removeClass("no-toolbar");
            } else {
                this.addClass("no-toolbar");
            }

            this.appendChild(this.grid);

            if (builder.isAutoLoad()) {
                this.reload();
            }

            if (builder.isPartialLoadEnabled()) {
                var postLoadCycle = setInterval(this.postLoad.bind(this), 100);

                this.getGrid().onScroll(() => {
                    clearInterval(postLoadCycle);
                    postLoadCycle = setInterval(this.postLoad.bind(this), 100);
                });
            }

            var keyBindings;

            this.onShown(() => {
                this.grid.resizeCanvas();
                if (builder.isHotkeysEnabled()) {
                    keyBindings = [
                        new KeyBinding('up', () => {
                            if (this.active) {
                                this.scrollToRow(this.grid.moveSelectedUp());
                            }
                        }),
                        new KeyBinding('down', () => {
                            if (this.active) {
                                this.scrollToRow(this.grid.moveSelectedDown());
                            }
                        }),
                        new KeyBinding('shift+up', (event: ExtendedKeyboardEvent) => {
                            if (this.active) {
                                this.scrollToRow(this.grid.addSelectedUp());
                            }
                            event.preventDefault();
                            event.stopImmediatePropagation();
                        }),
                        new KeyBinding('shift+down', (event: ExtendedKeyboardEvent) => {
                            if (this.active) {
                                this.scrollToRow(this.grid.addSelectedDown());
                            }
                            event.preventDefault();
                            event.stopImmediatePropagation();
                        }),
                        new KeyBinding('left', () => {
                            var selected = this.grid.getSelectedRows();
                            if (selected.length === 1) {
                                var node = this.gridData.getItem(selected[0]);
                                if (node && this.active) {
                                    if (node.isExpanded()) {
                                        this.active = false;
                                        this.collapseNode(node);
                                    } else if (node.getParent() !== this.root) {
                                        node = node.getParent();
                                        this.active = false;
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
                                        && !node.isExpanded() && this.active) {

                                    this.active = false;
                                    this.resetAndRender();
                                    this.expandNode(node);
                                }
                            }
                        }),
                        new KeyBinding('space', () => {
                            this.deselectAll();
                        })
                    ];
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
            });

            this.grid.subscribeOnSelectedRowsChanged((event, rows) => {
                this.notifySelectionChanged(event, rows.rows);
            });
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

        getRoot(): TreeNode<DATA> {
            return this.root;
        }

        isActive(): boolean {
            return this.active;
        }

        setActive(active: boolean = true) {
            this.active = active;
        }

        hasToolbar(): boolean {
            return !!this.toolbar;
        }

        getCanvas(): api.dom.Element {
            return this.canvasElement;
        }

        setCanvas(canvasElement: api.dom.Element) {
            this.canvasElement = canvasElement;
        }

        private scrollToRow(row: number) {
            if (row > -1 && this.grid.getSelectedRows().length > 0) {
                if (this.grid.getEl().getScrollTop() > row * 45) {
                    this.grid.getEl().setScrollTop(row * 45);
                } else if (this.grid.getEl().getScrollTop() + this.grid.getEl().getHeight() < (row + 1) * 45) {
                    this.grid.getEl().setScrollTop((row + 1) * 45 - this.grid.getEl().getHeight());
                }
            }
        }

        private loadEmptyNode(node: TreeNode<DATA>, loadMask?: api.ui.mask.LoadMask) {
            if (!this.getDataId(node.getData())) {
                this.setActive(false);

                if (loadMask) {
                    loadMask.show();
                }

                this.fetchChildren(node.getParent()).then((dataList: DATA[]) => {
                    node.getParent().setChildren(this.dataToTreeNodes(dataList, node.getParent()));
                    this.initData(this.getRoot().treeToList());
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.setActive(true);
                    if (loadMask) {
                        loadMask.hide();
                        loadMask.remove();
                    }
                }).done(() => this.notifyLoaded());
            }
        }

        private postLoad() {
            if (this.canvasElement.getEl().isVisible()) {
                this.canvasElement = Element.fromHtmlElement(this.getCanvas().getHTMLElement(), true);
                // top > point && point + 45 < bottom
                var children = this.canvasElement.getChildren(),
                    top = this.grid.getEl().getScrollTop(),
                    bottom = top + this.grid.getEl().getHeight();
                children = children.filter((el) => {
                    return (el.getEl().getOffsetTopRelativeToParent() + 5 > top) &&
                        (el.getEl().getOffsetTopRelativeToParent() + 40 < bottom);
                });

                for (var i = 0; i < children.length; i++) {
                    if (children[i].getHTMLElement().getElementsByClassName("children-to-load").length > 0 && this.isActive()) {
                        var node = this.grid.getDataView().getItem(children[i].getEl().getOffsetTopRelativeToParent() / 45),
                            loadMask = new api.ui.mask.LoadMask(children[i]);
                        loadMask.addClass("small");
                        this.loadEmptyNode(node, loadMask);
                        break;
                    }
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
        fetch(node: TreeNode<DATA>): wemQ.Promise<DATA> {
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
            if (!this.stash) {
                this.stash = this.root;
            }

            this.root = new TreeNodeBuilder<DATA>().build();

            this.initData([]);

            this.root.setExpanded(true);

            this.active = false;
            this.root.setChildren(this.dataToTreeNodes(dataList, this.root));
            this.initData(this.root.treeToList());
            this.resetAndRender();
            this.active = true;
        }

        resetFilter() {
            this.active = false;

            if (!this.stash) {
                // replace with refresh in future
                this.reload();
            } else {
                this.root = this.stash;
                this.initData(this.root.treeToList());
                this.resetAndRender();
                this.active = true;
                this.notifyLoaded();
            }

            this.stash = null;
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
                    toggleSpan.getEl().setMarginLeft(16 * (node.calcLevel() - 1) + "px");

                    return toggleSpan.toString() + formatter(row, cell, value, columnDef, node);
                };

                columns[0].setFormatter(toggleFormatter);
            }

            return columns;
        }

        selectAll() {
            this.grid.selectAll();
        }

        deselectAll() {
            this.grid.clearSelection();
        }

        deselectNode(dataId: string) {
            var oldSelected = this.grid.getSelectedRows(),
                newSelected = [];
            for (var i = 0; i < oldSelected.length; i++) {
                if (dataId !== this.gridData.getItem(oldSelected[i]).getDataId()) {
                    newSelected.push(oldSelected[i]);
                }
            }

            if (oldSelected.length !== newSelected.length) {
                this.grid.setSelectedRows(newSelected);
            }
        }

        getSelectedNodes(): TreeNode<DATA>[] {
            return this.grid.getSelectedRowItems();
        }

        getSelectedDataList(): DATA[] {
            var dataList: DATA[] = [];
            var treeNodes = this.grid.getSelectedRowItems();
            treeNodes.forEach((treeNode: TreeNode<DATA>) => {
                dataList.push(treeNode.getData());
            });
            return dataList;
        }

        // Hard reset
        reload(parentNode?: TreeNode<DATA>): void {
            this.root = new TreeNodeBuilder<DATA>().build();

            this.initData([]);

            this.root.setExpanded(true);

            this.fetchData(parentNode)
                .then((dataList: DATA[]) => {
                    this.root.setChildren(this.dataToTreeNodes(dataList, this.root));
                    this.initData(this.root.treeToList());
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.resetAndRender();
                    this.active = true;
                }).done(() => this.notifyLoaded());
        }

        // Soft reset, that saves node status
        refresh(): void {
            var root = this.stash || this.root;

            this.active = false;

            root.regenerateIds();

            root.setExpanded(true);
            this.initData(root.treeToList());
            this.resetAndRender();

            this.active = true;

            this.notifyLoaded();
        }

        updateNode(data: DATA): void {
            var root = this.stash || this.root;
            var dataId = this.getDataId(data);
            var node = root.findNode(dataId);
            if (!node) {
                throw new Error("Node not found for data: " + this.getDataId(data));
            }

            this.fetch(node)
                .then((data: DATA) => {
                    node.setData(data);
                    this.gridData.updateItem(node.getId(), node);
                    this.notifyDataChanged(new DataChangedEvent<DATA>([node], DataChangedEvent.ACTION_UPDATED));
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                });
        }

        deleteNode(data: DATA): void {
            var root = this.stash || this.root;
            var node = root.findNode(this.getDataId(data));
            this.gridData.deleteItem(node.getId());
            var parent = node.getParent();
            if (node && parent) {
                parent.removeChild(node);
                parent.setMaxChildren(parent.getMaxChildren() - 1);
                this.notifyDataChanged(new DataChangedEvent<DATA>([node], DataChangedEvent.ACTION_DELETED));
            }
        }

        appendNode(data: DATA): void {
            var root = this.stash || this.root;
            root.addChild(this.dataToTreeNode(data, root));
            var node = root.findNode(this.getDataId(data));
            if (node) {
                this.gridData.insertItem(this.gridData.getLength(), node);
                this.notifyDataChanged(new DataChangedEvent<DATA>([node], DataChangedEvent.ACTION_ADDED));
            }
        }

        deleteNodes(dataList: DATA[]): void {
            var root = this.stash || this.root;
            var updated: TreeNode<DATA>[] = [];
            var deleted: TreeNode<DATA>[] = [];
            dataList.forEach((data: DATA) => {
                var node = root.findNode(this.getDataId(data));
                if (node && node.getParent()) {
                    var parent = node.getParent();
                    updated.push(parent);
                    parent.removeChild(node);
                    deleted.push(node);
                    parent.setMaxChildren(parent.getMaxChildren() - 1);
                    updated.filter((el) => {
                        return el.getDataId() !== node.getId();
                    });
                }
            });
            this.notifyDataChanged(new DataChangedEvent<DATA>(deleted, DataChangedEvent.ACTION_DELETED));

            // TODO: For future use. Implement single node update by node id.
            /*
             var promises = updated.map((el) => {
             return this.fetch(el);
             });
             wemQ.all(promises).then((results: DATA[]) => {
             results.forEach((result: DATA, index: number) => {
             updated[index].setData(result);
             });
             }).catch((reason: any) => {
             api.DefaultErrorHandler.handle(reason);
             }).finally(() => {
             this.active = true;
             }).done(() => this.notifyLoaded());
             */
        }

        initData(nodes: TreeNode<DATA>[]) {
            this.gridData.setItems(nodes, "id");
            this.notifyDataChanged(new DataChangedEvent<DATA>(nodes, DataChangedEvent.ACTION_ADDED));
        }

        private expandNode(node?: TreeNode<DATA>) {
            node = node || this.root;

            var rootList: TreeNode<DATA>[],
                nodeList: TreeNode<DATA>[];

            if (node) {
                node.setExpanded(true);

                if (node.hasChildren()) {
                    rootList = this.root.treeToList();
                    nodeList = node.treeToList();
                    this.initData(rootList);
                    this.updateExpanded(node, nodeList, rootList);
                } else {
                    this.fetchData(node)
                        .then((dataList: DATA[]) => {
                            node.setChildren(this.dataToTreeNodes(dataList, node));
                            rootList = this.root.treeToList();
                            nodeList = node.treeToList();
                            this.initData(rootList);
                            this.updateExpanded(node, nodeList, rootList);
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).finally(() => {
                        }).done(() => this.notifyLoaded());
                }
            }
        }

        private updateExpanded(node: TreeNode<DATA>, nodeList: TreeNode<DATA>[], rootList: TreeNode<DATA>[]) {
            var nodeRow = node.getData() ? this.gridData.getRowById(node.getId()) : -1,
                expandedRows = [],
                animatedRows = [];

            nodeList.forEach((elem, index) => {
                if (index > 0) {
                    expandedRows.push(this.gridData.getRowById(elem.getId()));
                }
            });

            rootList.forEach((elem) => {
                var row = this.gridData.getRowById(elem.getId());
                if (row > nodeRow && expandedRows.indexOf(row) < 0) {
                    animatedRows.push(row);
                }
            });

            this.animateExpand(expandedRows, animatedRows);

            setTimeout(() => {
                this.resetAndRender();
                this.active = true;
            }, 350);
        }

        private collapseNode(node: TreeNode<DATA>) {
            var nodeRow = this.gridData.getRowById(node.getId()),
                animatedRows = [],
                collapsedRows = [];

            node.treeToList().forEach((elem, index) => {
                if (index > 0) {
                    collapsedRows.push(this.gridData.getRowById(elem.getId()));
                }
            });

            this.root.treeToList().forEach((elem) => {
                var row = this.gridData.getRowById(elem.getId());
                if (row > nodeRow && collapsedRows.indexOf(row) < 0) {
                    animatedRows.push(row);
                }
            });

            node.setExpanded(false);

            // Rows can have different order in HTML and Items array
            this.animateCollapse(collapsedRows, animatedRows);

            // Update data after animation
            setTimeout(() => {
                this.gridData.refresh();
                this.resetAndRender();
                this.active = true;
            }, 350);
        }

        private animateCollapse(collapsedRows: number[], animatedRows: number[]) {
            // update canvas content
            this.canvasElement = Element.fromHtmlElement(this.canvasElement.getHTMLElement(), true);

            // Sort needed: rows may not match actual dom structure.
            var children = this.canvasElement.getChildren().sort((a, b) => {
                var left = a.getEl().getOffsetTop(),
                    right = b.getEl().getOffsetTop();
                return left > right ? 1 : (left < right) ? -1 : 0;
            });

            collapsedRows.forEach((row) => {
                var elem = children[row].getEl();
                elem.setZindex(-1);
                elem.setMarginTop(-45 * collapsedRows.length + "px");
            });

            animatedRows.forEach((row) => {
                var elem = children[row].getEl();
                elem.setMarginTop(-45 * collapsedRows.length + "px");
            });
        }

        private animateExpand(expandedRows: number[], animated: number[]) {
            // update canvas content
            this.canvasElement = Element.fromHtmlElement(this.canvasElement.getHTMLElement(), true);

            var children = this.canvasElement.getChildren().sort((a, b) => {
                var left = a.getEl().getOffsetTop(),
                    right = b.getEl().getOffsetTop();
                return left > right ? 1 : (left < right) ? -1 : 0;
            });

            this.getEl().addClass("quick");

            expandedRows.forEach((row) => {
                var elem = children[row].getEl();
                elem.setZindex(-1);
                elem.setMarginTop(-45 * expandedRows.length + "px");
            });

            animated.forEach((row) => {
                var elem = children[row].getEl();
                elem.setMarginTop(-45 * expandedRows.length + "px");
            });

            setTimeout(() => {
                this.getEl().removeClass("quick");

                expandedRows.forEach((row) => {
                    var elem = children[row].getEl();
                    elem.setZindex(-1);
                    elem.setMarginTop(0 + "px");
                });

                animated.forEach((row) => {
                    var elem = children[row].getEl();
                    elem.setMarginTop(0 + "px");
                });
            }, 30);

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

        private notifySelectionChanged(event: any, rows: number[]): void {
            var selectedRows: TreeNode<DATA>[] = [];
            if (rows) {
                rows.forEach((rowIndex) => {
                    selectedRows.push(this.gridData.getItem(rowIndex));
                });
            }
            for (var i in this.selectionChangeListeners) {
                this.selectionChangeListeners[i](selectedRows);
            }
        }

        onSelectionChanged(listener: (selectedRows: TreeNode<DATA>[]) => void) {
            this.selectionChangeListeners.push(listener);
            return this;
        }

        unSelectionChanged(listener: (selectedRows: TreeNode<DATA>[]) => void) {
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

        private notifyDataChanged(event: DataChangedEvent<DATA>) {
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

        private resetZIndexes() {
            this.canvasElement = Element.fromHtmlElement(this.canvasElement.getHTMLElement(), true);
            this.canvasElement.getChildren().forEach((child) => {
                child.getEl().setZindex(1);
            });
        }

        private resetAndRender() {
            this.resetZIndexes();
            this.grid.syncGridSelection(false);
            this.grid.invalidateAllRows();
            this.grid.renderGrid();
        }
    }
}
