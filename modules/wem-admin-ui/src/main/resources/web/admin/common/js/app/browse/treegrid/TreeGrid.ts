module api.app.browse.treegrid {

    import Item = api.item.Item;

    import Element = api.dom.Element;
    import ElementHelper = api.dom.ElementHelper;

    import Grid = api.ui.grid.Grid;
    import GridOptions = api.ui.grid.GridOptions;
    import GridColumn = api.ui.grid.GridColumn;
    import DataView = api.ui.grid.DataView;
    import KeyBinding = api.ui.KeyBinding;
    import KeyBindings = api.ui.KeyBindings;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    export interface TreeGridParams {
        showToolbar?:boolean;
    }

    export class TreeGrid<NODE extends api.node.Node> extends api.ui.Panel {

        private columns: GridColumn<NODE>[] = [];

        private gridOptions: GridOptions;

        private grid: Grid<TreeNode<NODE>>;

        private gridData: DataView<TreeNode<NODE>>;

        private root: TreeNode<NODE>;

        private toolbar: TreeGridToolbar;

        private canvasElement: api.dom.Element;

        private active: boolean;

        private loadedListeners: Function[] = [];

        private rowSelectionChangeListeners: Function[] = [];

        constructor(params:TreeGridParams, classes:string = "") {

            super("tree-grid " + classes.trim());

            // root node with undefined item
            this.root = new TreeNode<NODE>();
            this.root.setExpanded(true);

            this.gridData = new DataView<TreeNode<NODE>>();
            this.gridData.setFilter((node: TreeNode<NODE>) => {
                return node.isVisible();
            });

            /*
             Note: We are using a proxy class to handle items/nodes and
             track the selection, expansion, etc.
             To have access to the complex properties, that are not in the root
             of the object, like `node.data.id`, we need to specify a custom
             column value extractor.
             */
            function nodeExtractor(node, column) {
                return node["data"][column.field];
            }

            this.gridOptions = <GridOptions>{
                dataItemColumnValueExtractor: nodeExtractor,
                editable: false,
                enableAsyncPostRender: true,
                enableCellNavigation: false, // necessary to turn off library key handling
                enableColumnReorder: false,
                forceFitColumns: true,
                hideColumnHeaders: true,
                checkableRows: true,
                rowHeight: 45,
                autoHeight: true
            };

            this.grid = new Grid<TreeNode<NODE>>(this.gridData, this.columns, this.gridOptions);

            // Custom row selection required for valid behaviour
            this.grid.setSelectionModel(new Slick.RowSelectionModel({selectActiveRow: false}));

            this.onClicked(() => {
                this.grid.focus();
            });

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
                    } else {
                        this.active = true;
                        this.grid.selectRow(data.row);
                    }
                }
                event.stopPropagation();
            });

            var keyBindings = [
                new KeyBinding('up', () => {
                    if (this.active) {
                        this.grid.moveSelectedUp();
                    }
                }),
                new KeyBinding('down', () => {
                    if (this.active) {
                        this.grid.moveSelectedDown();
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
                        if (node && node.getData().hasChildren()
                            && !node.isExpanded() &&  this.active) {

                            this.active = false;
                            this.expandNode(node);
                        }
                    }
                })
            ];

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

            var actions = new TreeGridActions(this.grid);

            if (params.showToolbar) {
                this.toolbar = new TreeGridToolbar(actions);
                this.appendChild(this.toolbar);
            } else {
                this.addClass("no-toolbar");
            }


            this.appendChild(this.grid);

            this.expandNode();

            this.onShown(() => {
                this.grid.resizeCanvas();
                KeyBindings.get().bindKeys(keyBindings);
            });

            this.onRendered(() => {
                this.grid.resizeCanvas();
            });

            this.onRemoved(() => {
                KeyBindings.get().unbindKeys(keyBindings);
            });

            this.grid.subscribeOnSelectedRowsChanged((e, rows) => {
                this.notifyRowSelectionChanged(e, rows.rows);
            });
        }

        getGrid(): Grid<TreeNode<NODE>> {
            return this.grid;
        }

        isActive(): boolean {
            return this.active;
        }

        setActive(active: boolean = true) {
            this.active = active;
        }

        /*
         Must be overridden in most cases.
         Various items may have different requests
         */
        fetchChildren(parent?: NODE): Q.Promise<NODE[]> {
            var deferred = Q.defer<NODE[]>();
            // Empty logic
            deferred.resolve(null);
            return deferred.promise;
        }

        setColumns(columns: GridColumn<TreeNode<NODE>>[]) {
            if (columns.length > 0) {
                var formatter = columns[0].formatter;
                var toggleFormatter = (row: number, cell: number, value: any, columnDef: any, node: TreeNode<NODE>) => {
                    var toggleSpan = new api.dom.SpanEl("toggle icon icon-xsmall");
                    if (node.getData().hasChildren()) {
                        var toggleClass = node.isExpanded() ? "collapse" : "expand";
                        toggleSpan.addClass(toggleClass);
                    }
                    toggleSpan.getEl().setMarginLeft(16 * (node.calcLevel() - 1) + "px");

                    return toggleSpan.toString() + formatter(row, cell, value, columnDef, node.getData());
                };

                columns[0].formatter = toggleFormatter;
            }

            this.columns = columns;

            this.grid.setColumns(this.columns);
        }

        selectAll() {
            this.grid.selectAll();
        }

        getSelectedTreeNodes() : TreeNode<NODE>[] {
            return this.grid.getSelectedRowItems();
        }

        getSelectedDataNodes() : NODE[] {
            var dataNodes: NODE[] = [];
            var treeNodes = this.grid.getSelectedRowItems();
            treeNodes.forEach((treeNode: TreeNode<NODE>) => {
                dataNodes.push(treeNode.getData());
            });
            return dataNodes;
        }

        reload(parent?: NODE): void {
            this.fetchChildren(parent).then((items: NODE[]) => {
                var node = new TreeNode<NODE>();
                node.setChildrenFromItems(items);
                node.setExpanded(true);
                this.initData(node.treeToList());
            })
        }

        private initData(nodes: TreeNode<NODE>[]) {
            this.gridData.setItems(nodes, "id");
        }

        private expandNode(node?: TreeNode<NODE>) {
            node = node || this.root;

            var parent = node.getData(),
                rootList: TreeNode<NODE>[],
                nodeList: TreeNode<NODE>[];

            if (node) {
                node.setExpanded(true);

                if (node.hasChildren()) {
                    rootList = this.root.treeToList();
                    nodeList = node.treeToList();
                    this.initData(rootList);
                    this.updateExpanded(node, nodeList, rootList);
                } else {
                    this.fetchChildren(parent)
                        .then((items: NODE[]) => {
                            node.setChildrenFromItems(items);
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

        private updateExpanded(node: TreeNode<NODE>,
                               nodeList: TreeNode<NODE>[],
                               rootList: TreeNode<NODE>[]) {
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
                this.resetZIndexes();
                this.grid.syncGridSelection(false); // Sync selected rows
                this.active = true;
            }, 350);
        }

        private collapseNode(node: TreeNode<NODE>) {
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
                this.resetZIndexes();
                this.grid.syncGridSelection(false);
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
            }, 10);
        }

        private notifyLoaded(): void {
            for (var i in this.loadedListeners) {
                this.loadedListeners[i](this);
            }
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

        private notifyRowSelectionChanged(event:any, rows:number[]): void {
            var selectedRows:NODE[] = [];
            if (rows) {
                rows.forEach((rowIndex) => {
                    selectedRows.push(this.gridData.getItem(rowIndex).getData());
                });
            }
            for (var i in this.rowSelectionChangeListeners) {
                this.rowSelectionChangeListeners[i](selectedRows);
            }
        }


        onRowSelectionChanged(listener: (selectedRows:NODE[]) => void) {
            this.rowSelectionChangeListeners.push(listener);
            return this;
        }

        unRowSelectionChanged(listener: (selectedRows:NODE[]) => void) {
            this.rowSelectionChangeListeners = this.rowSelectionChangeListeners.filter((curr) => {
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
    }
}
