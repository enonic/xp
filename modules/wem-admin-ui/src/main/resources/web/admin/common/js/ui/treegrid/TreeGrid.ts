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

        private active: boolean;

        private actions: TreeGridToolbarActions;

        private loadedListeners: Function[] = [];

        private contextMenuListeners: Function[] = [];

        private selectionChangeListeners: Function[] = [];

        private dataChangeListeners: {(event: DataChangedEvent<DATA>):void}[] = [];

        private loadBufferSize: number;

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

                this.loadBufferSize = builder.getLoadBufferSize();

                setInterval(this.postLoad.bind(this), 100);
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

        private scrollToRow(row: number) {
            if (row > -1 && this.grid.getSelectedRows().length > 0) {
                if (this.grid.getEl().getScrollTop() > row * 45) {
                    this.grid.getEl().setScrollTop(row * 45);
                } else if (this.grid.getEl().getScrollTop() + this.grid.getEl().getHeight() < (row + 1) * 45) {
                    this.grid.getEl().setScrollTop((row + 1) * 45 - this.grid.getEl().getHeight());
                }
            }
        }

        private loadEmptyNode(node: TreeNode<DATA>) {
            if (!this.getDataId(node.getData())) {
                this.fetchChildren(node.getParent()).then((dataList: DATA[]) => {
                    var oldChildren = node.getParent().getChildren();
                    // Ensure to remove empty node from the end if present
                    if (oldChildren[oldChildren.length - 1].getDataId() === "") {
                        oldChildren.pop();
                    }
                    var fetchedChildren = this.dataToTreeNodes(dataList, node.getParent());
                    var newChildren = oldChildren.concat(fetchedChildren.slice(oldChildren.length));
                    node.getParent().setChildren(newChildren);
                    this.initData(this.getRoot().treeToList());
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                }).done(() => this.notifyLoaded());
            }
        }

        private postLoad() {
            // Get current grid's canvas
            var gridClasses = (" " + this.grid.getEl().getClass()).replace(/\s/g, ".");
            var canvas = Element.fromString(".tree-grid " + gridClasses + " .grid-canvas", false);

            if (canvas.getEl().isVisible() && this.isActive()) {

                var canvasOffsetTop = !!canvas ? canvas.getEl().getOffsetTop() : 0;
                var rowHeight = this.grid.getOptions().rowHeight;

                // top > point && point + rowHeight < bottom
                var children = Element.elementsFromRequest(".tree-grid " + gridClasses + " .grid-canvas .slick-row:has(.children-to-load)",
                        false),
                    top = this.grid.getEl().getScrollTop(),
                    bottom = top + this.grid.getEl().getHeight() + this.loadBufferSize * rowHeight;


                // Filter the selected rows, that are also in the field of view or `loadBufferSize` rows lower.
                children = children.filter((el) => {
                    var offsetTop = el.getEl().getOffsetTop() - canvasOffsetTop;
                    return (offsetTop > top) && (offsetTop + rowHeight < (bottom + this.loadBufferSize * rowHeight));
                });

                // Search for the first "children-to-load" element.
                if (this.isActive() && children.length > 0) {
                    var offsetTop = Math.round(children[0].getEl().getOffsetTop() - canvasOffsetTop);
                    var node = this.grid.getDataView().getItem(Math.round(offsetTop / rowHeight));
                    this.loadEmptyNode(node);
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

        refreshNode(node?: TreeNode<DATA>): void {
            var root = this.root;
            this.active = false;

            node = node || root;
            node.regenerateIds();
            root.setExpanded(true);
            this.initData(root.treeToList());

            this.resetAndRender();

            this.active = true;

            this.notifyLoaded();
        }

        // Soft reset, that saves node status
        refresh(): void {
            var root = this.root;

            this.active = false;

            this.grid.invalidateAllRows();

            root.setExpanded(true);
            this.initData(root.treeToList());
            this.resetAndRender();

            this.active = true;

            this.notifyLoaded();
        }

        updateNode(data: DATA): void {

            var dataId = this.getDataId(data),
                nodesToUpdate = [],
                nodeToUpdate = this.root.findNode(dataId),
                stashedNodeToUpdate;

            if (!nodeToUpdate) {
                throw new Error("TreeNode to update not found: " + dataId);
            }

            nodesToUpdate.push(nodeToUpdate);

            if (!!this.stash) {
                stashedNodeToUpdate = this.stash.findNode(dataId);
                // filter may have multiple occurrences
                this.root.getChildren().forEach((topNode) => {
                    var match = topNode.findNode(dataId);
                    if (!!match) {
                        nodesToUpdate.push(match);
                    }
                });
            }

            this.fetch(nodesToUpdate[0])
                .then((data: DATA) => {
                    nodesToUpdate.forEach((node) => {
                        node.setData(data);
                        node.clearViewers();
                        this.gridData.updateItem(node.getId(), node);
                    });
                    if (!!stashedNodeToUpdate) {
                        stashedNodeToUpdate.setData(data);
                        stashedNodeToUpdate.clearViewers();
                    }
                    this.notifyDataChanged(new DataChangedEvent<DATA>(nodesToUpdate, DataChangedEvent.UPDATED));
                    this.sortNodeChildren(nodeToUpdate.getParent());
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                });
        }

        deleteNode(data: DATA, stashedParentNode?: TreeNode<DATA>): void {
            if (!stashedParentNode && this.stash) {
                this.deleteNode(data, this.stash);
            }
            var root = stashedParentNode || this.root;
            var node;
            while (node = root.findNode(this.getDataId(data))) {
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

        }

        appendNode(data: DATA, stashedParentNode?: TreeNode<DATA>): void {
            if (!stashedParentNode && this.stash) {
                this.appendNode(data, this.stash);
            }
            var root = stashedParentNode || this.root;

            var parentNode: TreeNode<DATA>;
            if (this.getSelectedNodes() && this.getSelectedNodes().length == 1) {
                parentNode = root.findNode(this.getSelectedNodes()[0].getDataId());
            } else {
                parentNode = root;
            }
            var isRootParentNode: boolean = (parentNode == root);

            if (!parentNode.hasChildren() && !isRootParentNode) {
                this.fetchData(parentNode)
                    .then((dataList: DATA[]) => {
                        parentNode.setChildren(this.dataToTreeNodes(dataList, parentNode));
                        var rootList = root.treeToList();
                        var nodeList = parentNode.treeToList();
                        this.initData(rootList);
                        var node = root.findNode(this.getDataId(data));
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
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    });
            } else {
                parentNode.addChild(this.dataToTreeNode(data, root), true);
                if (isRootParentNode) {
                    this.sortNodeChildren(parentNode);
                } else {
                    var node = root.findNode(this.getDataId(data));
                    if (node) {
                        if (!stashedParentNode) {
                            this.gridData.setItems(root.treeToList());
                        }
                        if (!isRootParentNode) {
                            if (!stashedParentNode) {
                                this.updateSelectedNode(parentNode);
                            }
                        }
                    }
                }
            }
        }

        deleteNodes(dataList: DATA[]): void {
            var root = this.root;
            var updated: TreeNode<DATA>[] = [];
            var deleted: TreeNode<DATA>[] = [];
            dataList.forEach((data: DATA) => {
                var node = root.findNode(this.getDataId(data));
                if (node && node.getParent()) {
                    var parent = node.getParent();
                    this.deleteNode(node.getData());
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
            if (this.stash) {
                this.stash.treeToList().forEach((child: TreeNode<DATA>) => {
                    this.refreshNodeData(child);
                });
            }
            this.notifyDataChanged(new DataChangedEvent<DATA>(deleted, DataChangedEvent.DELETED));
        }


        initData(nodes: TreeNode<DATA>[]) {
            this.gridData.setItems(nodes, "id");
            this.notifyDataChanged(new DataChangedEvent<DATA>(nodes, DataChangedEvent.ADDED));
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

        private updateSelectedNode(node: TreeNode<DATA>) {
            this.getGrid().clearSelection();
            this.refreshNode(node);
            var row = this.gridData.getRowById(node.getId());
            this.grid.selectRow(row);
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
            // get children
            var gridClasses = (" " + this.grid.getEl().getClass()).replace(/\s/g, "."),
                children = Element.elementsFromRequest(".tree-grid " + gridClasses + " .grid-canvas .slick-row", false),
                rowHeight = this.grid.getOptions().rowHeight;

            // Sort needed: rows may not match actual dom structure.
            var children = children.sort((a, b) => {
                var left = a.getEl().getOffsetTop(),
                    right = b.getEl().getOffsetTop();
                return left > right ? 1 : (left < right) ? -1 : 0;
            });

            collapsedRows.forEach((row) => {
                var elem = children[row].getEl();
                elem.setZindex(-1);
                elem.setMarginTop(-rowHeight * collapsedRows.length + "px");
            });

            animatedRows.forEach((row) => {
                var elem = children[row].getEl();
                elem.setMarginTop(-rowHeight * collapsedRows.length + "px");
            });
        }

        private animateExpand(expandedRows: number[], animated: number[]) {
            // get children
            var gridClasses = (" " + this.grid.getEl().getClass()).replace(/\s/g, "."),
                children = Element.elementsFromRequest(".tree-grid " + gridClasses + " .grid-canvas .slick-row", false),
                rowHeight = this.grid.getOptions().rowHeight;

            var children = children.sort((a, b) => {
                var left = a.getEl().getOffsetTop(),
                    right = b.getEl().getOffsetTop();
                return left > right ? 1 : (left < right) ? -1 : 0;
            });

            this.getEl().addClass("quick");

            expandedRows.forEach((row) => {
                var elem = children[row].getEl();
                elem.setZindex(-1);
                elem.setMarginTop(-rowHeight * expandedRows.length + "px");
            });

            animated.forEach((row) => {
                var elem = children[row].getEl();
                elem.setMarginTop(-rowHeight * expandedRows.length + "px");
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
            var children = Element.elementsFromRequest(".tree-grid .grid-canvas .slick-row", false);
            children.forEach((child) => {
                child.getEl().setZindex(1);
            });
        }

        private resetAndRender() {
            this.resetZIndexes();
            this.grid.syncGridSelection(false);
            this.grid.invalidateAllRows();
            this.grid.renderGrid();
        }

        refreshNodeData(parentNode: TreeNode<DATA>): wemQ.Promise<TreeNode<DATA>> {
            return null;
        }

        sortNodeChildren(node: TreeNode<DATA>) {
        }
    }
}
