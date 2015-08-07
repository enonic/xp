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

        private root: TreeRoot<DATA>;

        private toolbar: TreeGridToolbar;

        private contextMenu: TreeGridContextMenu;

        private active: boolean;

        private actions: TreeGridToolbarActions<any>;

        private loadedListeners: Function[] = [];

        private contextMenuListeners: Function[] = [];

        private selectionChangeListeners: Function[] = [];

        private dataChangeListeners: {(event: DataChangedEvent<DATA>):void}[] = [];

        private loadBufferSize: number;

        constructor(builder: TreeGridBuilder<DATA>) {

            super(builder.getClasses());

            // root node with undefined item
            this.root = new TreeRoot<DATA>();

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

            this.grid.syncGridSelection(false);

            this.actions = new TreeGridToolbarActions(this);

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
                        event.stopPropagation();
                    } else if (elem.hasClass("collapse")) {
                        this.active = false;
                        elem.removeClass("collapse").addClass("expand");
                        var node = this.gridData.getItem(data.row);
                        this.collapseNode(node);
                        event.stopPropagation();
                    } else if (data.cell === 0) {
                        this.active = true;
                        if (elem.getAttribute("type") === "checkbox") {
                            this.grid.toggleRow(data.row);
                            event.stopPropagation();
                        }
                    } else {
                        this.active = true;
                        this.root.clearStashedSelection();
                        this.grid.selectRow(data.row);
                    }
                }
                if (this.contextMenu) {
                    this.contextMenu.hide();
                }
            });

            if (builder.isShowToolbar()) {
                this.toolbar = new TreeGridToolbar(this.actions, this);
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
                var interval;
                this.onShown(() => {
                    if (interval) {
                        clearInterval(interval);
                    }
                    interval = setInterval(this.postLoad.bind(this), 200);
                });
            }

            var keyBindings = [];

            this.onShown(() => {
                this.grid.resizeCanvas();
                if (builder.isHotkeysEnabled()) {

                    if (!this.gridOptions.isMultipleSelectionDisabled()) {
                        keyBindings = [
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
                            })
                        ];
                    }

                    keyBindings = keyBindings.concat([
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
                        new KeyBinding('left', () => {
                            var selected = this.grid.getSelectedRows();
                            if (selected.length === 1) {
                                var node = this.gridData.getItem(selected[0]);
                                if (node && this.active) {
                                    if (node.isExpanded()) {
                                        this.active = false;
                                        this.collapseNode(node);
                                    } else if (node.getParent() !== this.root.getCurrentRoot()) {
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

            /* if (this.toolbar) {
             this.gridData.onRowCountChanged(() => {
             this.toolbar.refresh();
             });

             this.onSelectionChanged(() => {
             this.toolbar.refresh();
             });
             }*/
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

        getRoot(): TreeRoot<DATA> {
            return this.root;
        }

        isActive(): boolean {
            return this.active;
        }

        setActive(active: boolean = true) {
            this.active = active;
        }

        getToolbar(): TreeGridToolbar {
            return this.toolbar;
        }

        hasToolbar(): boolean {
            return !!this.toolbar;
        }

        scrollToRow(row: number) {
            var gridClasses = (" " + this.grid.getEl().getClass()).replace(/\s/g, ".");
            var canvas = Element.fromString(".tree-grid " + gridClasses + " .grid-canvas", false);
            var viewport = Element.fromString(".tree-grid " + gridClasses + " .slick-viewport", false);
            var scrollEl = viewport.getEl();
            if (row > -1 && this.grid.getSelectedRows().length > 0) {
                if (scrollEl.getScrollTop() > row * 45) {
                    scrollEl.setScrollTop(row * 45);
                } else if (scrollEl.getScrollTop() + scrollEl.getHeight() < (row + 1) * 45) {
                    scrollEl.setScrollTop((row + 1) * 45 - scrollEl.getHeight());
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
                    this.initData(this.root.getCurrentRoot().treeToList());
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                }).done(() => this.notifyLoaded());
            }
        }

        private postLoad() {
            // Skip if not visible or active (is loading something)
            var iFrame = api.app.Application.getApplication().getAppFrame(),
                disabled = !iFrame.isVisible() || // application's iframe is visible
                           !this.isVisible() ||   // TreeGrid is visible in tab
                           !this.isActive();      // TreeGrid is active

            if (disabled) {
                return;
            }

            var viewportRange = this.grid.getViewport(),
                lastIndex = this.gridData.getItems().length - 1,
            // first and last rows, that are visible in grid
                firstVisible = viewportRange.top,
                lastVisible = Math.min(viewportRange.bottom, lastIndex),
            // interval borders to search for the empty node
                from = firstVisible,
                to = Math.min(lastVisible + this.loadBufferSize, lastIndex);

            for (var i = from; i <= to; i++) {
                if (!!this.gridData.getItem(i) && this.gridData.getItem(i).getDataId() === "") {
                    //emptyNode = this.gridData.getItem(i);
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
            this.active = false;
            this.root.setFiltered(true);
            this.root.getCurrentRoot().setChildren(this.dataToTreeNodes(dataList, this.root.getCurrentRoot()));
            this.initData(this.root.getCurrentRoot().treeToList());
            this.invalidate();
            this.active = true;
        }

        resetFilter() {
            this.active = false;

            if (this.root.isFiltered()) {
                this.root.setFiltered(false);
                this.initData(this.root.getCurrentRoot().treeToList());
                this.invalidate();
                this.active = true;
                this.notifyLoaded();
            } else {
                // replace with refresh in future
                this.reload();
            }
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

            this.root.removeSelection(dataId);

            if (oldSelected.length !== newSelected.length) {
                this.grid.setSelectedRows(newSelected);
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

        reload(parentNodeData?: DATA): void {
            this.root.resetCurrentRoot(parentNodeData);
            this.initData([]);

            this.fetchData()
                .then((dataList: DATA[]) => {
                    this.root.getCurrentRoot().setChildren(this.dataToTreeNodes(dataList, this.root.getCurrentRoot()));
                    this.initData(this.root.getCurrentRoot().treeToList());
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.invalidate();
                    this.active = true;
                }).done(() => this.notifyLoaded());
        }

        refreshNode(node?: TreeNode<DATA>): void {
            var root = this.root.getCurrentRoot();
            this.active = false;

            node = node || root;
            node.regenerateIds();
            root.setExpanded(true);
            this.initData(root.treeToList());

            this.invalidate();

            this.active = true;

            this.notifyLoaded();
        }

        // Soft reset, that saves node status
        refresh(): void {
            var root = this.root.getCurrentRoot();

            this.active = false;

            this.grid.invalidate();

            root.setExpanded(true);
            this.initData(root.treeToList());
            this.invalidate();

            this.active = true;

            this.notifyLoaded();
        }

        updateNode(data: DATA, oldDataId?: string): wemQ.Promise<void> {

            var dataId = oldDataId || this.getDataId(data),
                nodeToUpdate = this.root.getCurrentRoot().findNode(dataId);

            if (!nodeToUpdate) {
                throw new Error("TreeNode to update not found: " + dataId);
            }

            return this.fetchAndUpdateNodes([nodeToUpdate]);
        }

        updateNodes(data: DATA, oldDataId?: string): wemQ.Promise<void> {

            var dataId = oldDataId || this.getDataId(data),
                nodesToUpdate = this.root.getCurrentRoot().findNodes(dataId);

            if (!nodesToUpdate) {
                throw new Error("TreeNode to update not found: " + dataId);
            }

            return this.fetchAndUpdateNodes(nodesToUpdate);
        }

        private fetchAndUpdateNodes(nodesToUpdate: TreeNode<DATA>[]): wemQ.Promise<void> {
            return this.fetch(nodesToUpdate[0])
                .then((data: DATA) => {
                    nodesToUpdate.forEach((node) => {
                        node.setData(data);
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

                    this.notifyDataChanged(new DataChangedEvent<DATA>(nodesToUpdate, DataChangedEvent.UPDATED));
                    this.initData(this.root.getCurrentRoot().treeToList());
                    this.root.updateSelection(nodesToUpdate[0].getDataId(), data);
                    this.triggerSelectionChangedListeners();
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
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

            this.root.removeSelection(dataId);
        }

        /**
         * @param data
         * @param nextToSelection - by default node is appended as child to selection or root, set this to true to append to the same level
         * @param stashedParentNode
         */
        appendNode(data: DATA, nextToSelection?: boolean, prepend: boolean = true, stashedParentNode?: TreeNode<DATA>): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

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
            var isRootParentNode: boolean = (parentNode == root);

            if (!parentNode.hasChildren() && !isRootParentNode) {
                this.fetchData(parentNode)
                    .then((dataList: DATA[]) => {
                        if (parentNode.hasChildren()) {
                            this.doAppendNodeToParentWithChildren(parentNode, data, root, prepend, stashedParentNode, isRootParentNode);

                        } else {
                            parentNode.setChildren(this.dataToTreeNodes(dataList, parentNode));
                            this.initData(root.treeToList());
                            var node = root.findNode(this.getDataId(data));
                            if (!node) {
                                parentNode.addChild(this.dataToTreeNode(data, root), prepend);
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
                        api.DefaultErrorHandler.handle(reason);
                        deferred.reject(reason);
                    });
            } else {
                this.doAppendNodeToParentWithChildren(parentNode, data, root, prepend, stashedParentNode, isRootParentNode);
                deferred.resolve(null);
            }

            return deferred.promise;
        }

        private doAppendNodeToParentWithChildren(parentNode, data, root, prepend, stashedParentNode, isRootParentNode) {
            parentNode.addChild(this.dataToTreeNode(data, root), prepend);

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
            var selection: any = [],
                selectionIds = this.root.getFullSelection().map((el) => {
                    return el.getDataId();
                });

            this.gridData.setItems(nodes, "id");
            this.notifyDataChanged(new DataChangedEvent<DATA>(nodes, DataChangedEvent.ADDED));

            selectionIds.forEach((selectionId) => {
                nodes.forEach((node, index) => {
                    if (node.getDataId() === selectionId) {
                        selection.push(index);
                    }
                });
            });

            this.grid.setSelectedRows(selection);
        }

        expandNode(node?: TreeNode<DATA>) {
            node = node || this.root.getCurrentRoot();

            if (node) {
                node.setExpanded(true);

                if (node.hasChildren()) {
                    this.initData(this.root.getCurrentRoot().treeToList());
                    this.updateExpanded();
                } else {
                    this.fetchData(node)
                        .then((dataList: DATA[]) => {
                            node.setChildren(this.dataToTreeNodes(dataList, node));
                            this.initData(this.root.getCurrentRoot().treeToList());
                            this.updateExpanded();
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).finally(() => {
                        }).done(() => this.notifyLoaded());
                }
            }
        }

        private updateExpanded() {
            this.invalidate();
            this.active = true;
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
            this.active = true;
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
    }
}
