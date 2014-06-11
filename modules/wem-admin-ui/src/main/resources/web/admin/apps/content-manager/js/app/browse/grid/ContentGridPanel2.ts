module app.browse.grid {
    
    import Element = api.dom.Element;
    import ElementHelper = api.dom.ElementHelper;

    import Grid = api.ui.grid.Grid;
    import GridOptions = api.ui.grid.GridOptions;
    import GridColumn = api.ui.grid.GridColumn;
    import DataView = api.ui.grid.DataView;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;


    export class ContentGridPanel2 extends api.app.browse.grid2.GridPanel2 {

        private columns:GridColumn<ContentSummary>[] = [];

        private gridOptions:GridOptions;

        private grid:Grid<ContentSummary>;

        private gridData:DataView<ContentSummary>;

        private nameFormatter: (row: number, cell: number, value: any, columnDef: any, dataContext: Slick.SlickData) => string;

        private toolbar:GridToolbar;
        
        private canvasElement:api.dom.DivEl;

        private cache:ContentGridCache2;

        constructor() {
            super();

            this.addClass("active");

            this.nameFormatter = (row: number, cell: number, value: any, columnDef: any, item: ContentSummary) => {
                var format = "";
                var level = item.getPath().getLevel() - 1;

                var toggleSpan = new api.dom.SpanEl("toggle icon icon-xsmall");
                if (item.hasChildren()) {
                    var toggleClass = this.cache.isExpanded(item) ? "collapse" : "expand";
                    toggleSpan.addClass(toggleClass);
                }
                if (level > 0) {
                    toggleSpan.getEl().setMarginLeft(16 * level + "px");
                }

                format += toggleSpan.toString();


                var contentSummaryViewer = new ContentSummaryViewer();
                contentSummaryViewer.setObject(item);
                if (level > 0) {
                    contentSummaryViewer.getEl().setPaddingLeft(16 * level + "px");
                }
                format += contentSummaryViewer.toString();

                return format;
            };

            var column1 = <GridColumn<any>> {
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: this.nameFormatter
            };
            var column2 = <GridColumn<any>> {
                name: "ModifiedTime",
                id: "modifiedTime",
                field: "modifiedTime",
                cssClass: "modified",
                minWidth: 150,
                maxWidth: 170,
                formatter:api.app.browse.grid2.DateTimeFormatter.format
            };
            this.columns = [column1, column2];

            this.gridData = new DataView<ContentSummary>();
            this.gridData.setFilter((item) => {
                return item.isDisplayed();
            });

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

            this.grid = new Grid<ContentSummary>(this.gridData, this.columns, this.gridOptions);

            // Custom row selection required for valid behaviour
            this.grid.setSelectionModel(new Slick.RowSelectionModel({selectActiveRow: false}));
            this.grid.subscribeOnClick((event, data) => {
                if (this.hasClass("active")) {
                    var elem = new ElementHelper(event.target);
                    if (elem.hasClass("expand")) {
                        this.removeClass("active");
                        elem.removeClass("expand").addClass("collapse");
                        var item = this.gridData.getItem(data.row);
                        this.expandData(item);
                    } else if (elem.hasClass("collapse")) {
                        this.removeClass("active");
                        elem.removeClass("collapse").addClass("expand");
                        var item = this.gridData.getItem(data.row);
                        this.collapseData(item);
                    } else {
                        this.grid.setSelectedRows([data.row]);
                    }
                }
                event.stopImmediatePropagation();
            });

            var actions = GridActions.init(this.grid);
            // set actions for content menu here

            this.toolbar = new GridToolbar(actions);

            this.cache = new ContentGridCache2(this.grid);

            this.appendChild(this.toolbar);

            this.appendChild(this.grid);

            // Canvas element retrieving without jQuery
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

            this.expandData();

            this.onShown((event) => {
                this.grid.resizeCanvas();
            });

            this.onRendered((event) => {
                this.grid.resizeCanvas();
            });

        }

        private initData(contents:ContentSummary[]) {

            this.gridData.setItems(contents, "id");
        }

        private expandData(item?: ContentSummary) {
            var deferred = Q.defer<ContentSummary[]>(),
                promises = [], // list of parallel requests
                tree:ContentGridCacheItem2[] = this.cache.expand(item);

            this.cache.saveSelected();

            tree[0].setExpanded(true); // at least one item exists

            // Create parallel call for each branch
            tree.forEach((elem) => {
                if (elem.isUpdated()) {
                    if (elem.isExpanded()) {
                        this.gridData.getItems().forEach((item) => {
                            if (item.getPath().isChildOf(elem.getPath())) {
                                item.setDisplayed(true);
                            }
                        });
                    }
                    promises.push([]);
                } else {
                    var id = elem.getId();
                    elem.setUpdated(true);
                    promises.push(new api.content.ListContentByIdRequest(id).sendAndParse());
                }
            });

            // init data, when everything is retrieved
            Q.all(promises).then((results) => {
                var contentSummary:ContentSummary[] = this.gridData.getItems(),
                    expanded = [],
                    animated = [],
                    itemIndex = Infinity;

                results.forEach((result:ContentSummary[], index:number) => {
                    if (contentSummary.length) {
                        for (var i = 0; i < contentSummary.length; i++) {
                            if (contentSummary[i].getId() === tree[index].getId()) {
                                contentSummary = contentSummary.slice(0, i + 1).concat(result).concat(contentSummary.slice(i + 1));
                            }
                        }
                    } else {
                        contentSummary = result;
                    }
                });

                this.initData(contentSummary);

                if (item) {
                    this.gridData.getItems().forEach((elem, index) => {
                        var row = this.gridData.getRowById(elem.getId());
                        if (row && elem.getPath().isDescendantOf(item.getPath())) {
                            expanded.push(row);
                        } else if (row && index > itemIndex) {
                            animated.push(row);
                        } else if (elem.getId() === item.getId()) {
                            itemIndex = index;
                        }
                    });


                }

                this.cache.loadSelected();

                this.animateExpand(expanded, animated);

                setTimeout(() => {
                    this.resetIndexes(expanded);
//                    this.cache.loadSelected();
                    // Grid is again clickable
                    this.addClass("active");
                }, 310);

            }).catch((reason) => {
                api.notify.showError(reason.toString());
            }).finally(() => {
            }).done();

            deferred.resolve(null);
        }

        private collapseData(item: ContentSummary) {
            var oldItems = this.gridData.getItems(),
                animated = [],
                collapsed = [],
                itemIndex = Infinity;

            this.cache.saveSelected();

            oldItems.forEach((elem, index) => {
                var row = this.gridData.getRowById(elem.getId());
                if (row && elem.getPath().isDescendantOf(item.getPath())) {
                    elem.setDisplayed(false);
                    collapsed.push(row);
                } else if (row && index > itemIndex) {
                    animated.push(row);
                } else if (elem.getId() === item.getId()) {
                    itemIndex = index;
                }
            });

            // Rows can have diffrent order in HTML and Items array
            this.animateCollapse(collapsed, animated);

            // Update data after animation
            setTimeout(() => {
                this.cache.collapse(item);
                this.gridData.refresh();
                this.cache.loadSelected();
                // Grid is again clickable
                this.addClass("active");
            }, 310);
        }

        animateCollapse(collapsed:number[], animated:number[]) {
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

        animateExpand(expanded:number[], animated:number[]) {
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

        resetIndexes(elements:number[]) {
            this.canvasElement = Element.fromHtmlElement(this.canvasElement.getHTMLElement(), true);
            elements.forEach((row) => {
                var elem = this.canvasElement.getChildren()[row].getEl();
                elem.setZindex(1);
            });
        }
    }
}
