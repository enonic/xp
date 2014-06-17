module api.app.browse.treegrid {

    import Item = api.item.Item;

    import Element = api.dom.Element;
    import ElementHelper = api.dom.ElementHelper;

    import Grid = api.ui.grid.Grid;
    import GridOptions = api.ui.grid.GridOptions;
    import GridColumn = api.ui.grid.GridColumn;
    import DataView = api.ui.grid.DataView;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    export class TreeGrid<T extends Item> extends api.ui.Panel {

        private columns:GridColumn<T>[] = [];

        private gridOptions:GridOptions;

        private grid:Grid<T>;

        private gridData:DataView<T>;

        private root:TreeNode<T>;

        private toolbar:TreeGridToolbar;

        private canvasElement:api.dom.Element;

        private active:boolean;

        constructor(classes:string = "") {

            super("tree-grid " + classes.trim());

            // root node with undefined item
            this.root = new TreeNode<T>();
            this.root.setExpanded(true);

            this.gridData = new DataView<T>();
            this.gridData.setFilter((item:T, root:TreeNode<T>) => {
                var node = root.findNode(item);

                return node ? node.isVisible() : false;
            });

            /*
            Note: SlickGrid recompile filter method,
            so all non-global variables will become undefined.
            To pass a parameter, we need to use setFilterArgs() method.
             */
            this.gridData.setFilterArgs(this.root);

            this.gridOptions = <GridOptions>{
                editable: false,
                enableAsyncPostRender: true,
                enableCellNavigation: true,
                enableColumnReorder: false,
                forceFitColumns: true,
                hideColumnHeaders: true,
                checkableRows: true,
                rowHeight: 45,
                autoHeight: true
            };

            this.grid = new Grid<T>(this.gridData, this.columns, this.gridOptions);

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
                        var item = this.gridData.getItem(data.row);
                        this.expandData(item);
                    } else if (elem.hasClass("collapse")) {
                        this.active = false;
                        elem.removeClass("collapse").addClass("expand");
                        var item = this.gridData.getItem(data.row);
                        this.collapseData(item);
                    } else {
                        this.active = true;
                        this.grid.selectRow(data.row);
                    }
                }
                event.stopPropagation();
            });

            this.grid.setOnKeyDown((event: KeyboardEvent) => {
                if (this.active) {
                    var selected = this.grid.getSelectedRows();
                    switch (event.keyCode) {
                    case 38: // up
                        this.grid.moveSelectedUp();
                        break;
                    case 40: // down
                        this.grid.moveSelectedDown();
                        break;
                    case 37: // left - collapse
                        if (selected.length === 1) {
                            var item = this.gridData.getItem(selected[0]);
                            var node = item ? this.root.findNode(item) : undefined;
                            if (node && this.active) {
                                if (node.isExpanded()) {
                                    this.active = false;
                                    this.collapseData(item);
                                } else if (node.getParent() !== this.root) {
                                    this.active = false;
                                    item = node.getParent().getItem();
                                    var row = this.gridData.getRowById(item.getId());
                                    this.grid.selectRow(row);
                                    this.collapseData(item);
                                }
                            }
                        }
                        break;
                    case 39: // right - expand
                        if (selected.length === 1) {
                            var item = this.gridData.getItem(selected[0]);
                            var node = item ? this.root.findNode(item) : undefined;
                            if (node && this.hasChildren(item)
                                && !node.isExpanded() &&  this.active) {

                                this.active = false;
                                this.expandData(item);
                            }
                        }
                        break;
                    }
                }
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

            var actions = TreeGridActions.init(this.grid);

            this.toolbar = new TreeGridToolbar(actions);

            this.appendChild(this.toolbar);

            this.appendChild(this.grid);

            this.expandData();

            this.onShown(() => {
                this.grid.resizeCanvas();
            });

            this.onRendered(() => {
                this.grid.resizeCanvas();
            });
        }

        getGrid():Grid<T> {
            return this.grid;
        }

        isActive():boolean {
            return this.active;
        }

        setActive(active:boolean = true) {
            this.active = active;
        }

        /*
        Must be overridden in most cases.
        Various items may have different determination of 'child'
         */
        hasChildren(item:T):boolean {
            return false;
        }

        /*
         Must be overridden in most cases.
         Various items may have different requests
         */
        listRequest(item?:T):Q.Promise<T[]> {
            var deferred = Q.defer<T[]>();
            // Empty logic
            deferred.resolve(null);
            return deferred.promise;
        }

        setColumns(columns:GridColumn<T>[]) {
            if (columns.length > 0) {
                var formatter = columns[0].formatter;
                var toggleFormatter = (row:number, cell:number, value:any, columnDef:any, item:T) => {
                    var node = this.root.findNode(item);

                    var toggleSpan = new api.dom.SpanEl("toggle icon icon-xsmall");
                    if (this.hasChildren(item)) {
                        var toggleClass = node.isExpanded() ? "collapse" : "expand";
                        toggleSpan.addClass(toggleClass);
                    }

                    toggleSpan.getEl().setMarginLeft(16 * (node.calcLevel() - 1) + "px");

                    return toggleSpan.toString() + formatter(row, cell, value, columnDef, item);
                };

                columns[0].formatter = toggleFormatter;
            }

            this.columns = columns;

            this.grid.setColumns(this.columns);
        }

        private initData(contents:T[]) {
            this.gridData.setItems(contents, "id");
        }

        private expandData(item?: T) {
            var node:TreeNode<T> = item? this.root.findNode(item) : this.root,
                rootItemList:T[],
                nodeItemList:T[];

            if (node) {
                node.setExpanded(true);

                if (node.hasChildren()) {
                    nodeItemList = node.treeToItemList();
                    rootItemList = this.root.treeToItemList();
                    this.initData(rootItemList);
                    this.updateExpanded(node, nodeItemList, rootItemList);
                } else {
                    this.listRequest(item)
                        .then((items:T[]) => {
                            node.setChildrenFromItems(items);
                            nodeItemList = node.treeToItemList();
                            rootItemList = this.root.treeToItemList();
                            this.initData(rootItemList);
                            this.updateExpanded(node, nodeItemList, rootItemList);
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).finally(() => {
                        }).done();
                }
            }
        }

        private updateExpanded(node:TreeNode<T>, nodeItemList:T[], rootItemList:T[]) {
            var nodeRow = node.getItem() ? this.gridData.getRowById(node.getItem().getId()) : -1,
                expanded = [],
                animated = [];

            nodeItemList.forEach((elem, index) => {
                if (index > 0) {
                    expanded.push(this.gridData.getRowById(elem.getId()));
                }
            });

            rootItemList.forEach((elem) => {
                var row = this.gridData.getRowById(elem.getId());
                if (row > nodeRow && expanded.indexOf(row) < 0) {
                    animated.push(row);
                }
            });

            this.animateExpand(expanded, animated);

            setTimeout(() => {
                this.resetZIndexes();
                this.grid.syncGridSelection(false); // Sync selected rows
                this.active = true;
            }, 350);
        }

        private collapseData(item: T) {
            var node = this.root.findNode(item),
                animated = [],
                collapsed = [];

            var nodeRow = this.gridData.getRowById(node.getItem().getId());

            node.treeToItemList().forEach((elem, index) => {
                if (index > 0) {
                    collapsed.push(this.gridData.getRowById(elem.getId()));
                }
            });

            this.root.treeToItemList().forEach((elem) => {
                var row = this.gridData.getRowById(elem.getId());
                if (row > nodeRow && collapsed.indexOf(row) < 0) {
                    animated.push(row);
                }
            });

            node.setExpanded(false);

            // Rows can have different order in HTML and Items array
            this.animateCollapse(collapsed, animated);

            // Update data after animation
            setTimeout(() => {
                this.gridData.refresh();
                this.resetZIndexes();
                this.grid.syncGridSelection(false);
                this.active = true;
            }, 350);
        }

        private animateCollapse(collapsed:number[], animated:number[]) {
            // update canvas content
            this.canvasElement = Element.fromHtmlElement(this.canvasElement.getHTMLElement(), true);

            // Sort needed: rows may not match actual dom structure.
            var children = this.canvasElement.getChildren().sort((a, b) => {
                var left = a.getEl().getOffsetTop(),
                    right = b.getEl().getOffsetTop();
                return left > right ? 1 : (left < right) ? -1 : 0;
            });

            collapsed.forEach((row) => {
                var elem = children[row].getEl();
                elem.setZindex(-1);
                elem.setMarginTop(-45 * collapsed.length + "px");
            });

            animated.forEach((row) => {
                var elem = children[row].getEl();
                elem.setMarginTop(-45 * collapsed.length + "px");
            });
        }

        private animateExpand(expanded:number[], animated:number[]) {
            // update canvas content
            this.canvasElement = Element.fromHtmlElement(this.canvasElement.getHTMLElement(), true);

            var children = this.canvasElement.getChildren().sort((a, b) => {
                var left = a.getEl().getOffsetTop(),
                    right = b.getEl().getOffsetTop();
                return left > right ? 1 : (left < right) ? -1 : 0;
            });

            this.getEl().addClass("quick");

            expanded.forEach((row) => {
                var elem = children[row].getEl();
                elem.setZindex(-1);
                elem.setMarginTop(-45 * expanded.length + "px");
            });

            animated.forEach((row) => {
                var elem = children[row].getEl();
                elem.setMarginTop(-45 * expanded.length + "px");
            });

            setTimeout(() => {
                this.getEl().removeClass("quick");

                expanded.forEach((row) => {
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

        private resetZIndexes() {
            this.canvasElement = Element.fromHtmlElement(this.canvasElement.getHTMLElement(), true);
            this.canvasElement.getChildren().forEach((child) => {
                child.getEl().setZindex(1);
            });
        }
    }
}
