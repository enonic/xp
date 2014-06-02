module app.browse.grid {

    export class ContentGridPanel2 extends api.app.browse.grid2.GridPanel2 {

        private columns:api.ui.grid.GridColumn<api.content.ContentSummary>[] = [];

        private gridOptions:api.ui.grid.GridOptions;

        private grid:api.ui.grid.Grid<api.content.ContentSummary>;

        private gridData:api.ui.grid.DataView<api.content.ContentSummary>;

        private nameFormatter: (row: number, cell: number, value: any, columnDef: any, dataContext: Slick.SlickData) => string;

        constructor() {
            super();

            this.nameFormatter = (row: number, cell: number, value: any, columnDef: any, item: api.content.ContentSummary) => {
                var format = "";
                var level = item.getPath().getLevel() - 1;
                var nextItem = this.gridData.getItem(row + 1);

                var toggleSpan = new api.dom.SpanEl("toggle");
                if (item.hasChildren()) {
                    var toggleClass = (nextItem && (nextItem.getPath().getLevel() - 1) > level) ? "collapse" : "expand";
                    toggleSpan.addClass(toggleClass);
                }
                if (level > 0) {
                    toggleSpan.getEl().setMarginLeft(16 * level + "px");
                }

                format += toggleSpan.toString();


                var contentSummaryViewer = new api.content.ContentSummaryViewer();
                contentSummaryViewer.setObject(item);
                if (level > 0) {
                    contentSummaryViewer.getEl().setPaddingLeft(16 * level + "px");
                }
                format += contentSummaryViewer.toString();

                return format;
            };

            var column1 = <api.ui.grid.GridColumn<any>> {
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: this.nameFormatter
            };
            var column2 = <api.ui.grid.GridColumn<any>> {
                name: "ModifiedTime",
                id: "modifiedTime",
                field: "modifiedTime",
                cssClass: "modified",
                minWidth: 150,
                maxWidth: 170,
                formatter:api.app.browse.grid2.DateTimeFormatter.format
            };
            this.columns = [column1, column2];

            this.gridData = new api.ui.grid.DataView<api.content.ContentSummary>();

            this.gridOptions = <api.ui.grid.GridOptions>{
                editable: false,
                enableCellNavigation: true,
                enableColumnReorder: false,
                forceFitColumns: true,
                hideColumnHeaders: true,
                checkableRows: true,
                rowHeight: 45,
                autoHeight: true
            };

            this.grid = new api.ui.grid.Grid<api.content.ContentSummary>(this.gridData, this.columns, this.gridOptions);

            // Custom row selection required for valid behaviour
            this.grid.setSelectionModel(new Slick.RowSelectionModel({selectActiveRow: false}));
            this.grid.subscribeOnClick((event, data) => {
                var elem = new api.dom.ElementHelper(event.target);

                if (elem.hasClass("expand")) {
                    elem.removeClass("expand").addClass("collapse");
                    var item = this.gridData.getItem(data.row);
                    this.expandData(item.getId());
                    event.stopImmediatePropagation();
                } else if (elem.hasClass("collapse")) {
                    elem.removeClass("collapse").addClass("expand");
                    var item = this.gridData.getItem(data.row);
                    this.collapseData(item);
                } else {
                    this.grid.setSelectedRows([data.row]);
                }
            });

            this.appendChild(this.grid);

            this.expandData();

            this.onShown((event) => {
                this.grid.resizeCanvas();
            });

            this.onRendered((event) => {
                this.grid.resizeCanvas();
            });

        }

        private initData(contents:api.content.ContentSummary[]) {

            this.gridData.setItems(contents, "id");
        }

        private expandData(id: string = "") {
            var request = new api.content.ListContentByIdRequest(id);
            request.sendAndParse().then((contents:api.content.ContentSummary[]) => {
                        if (id) {
                            var items = this.gridData.getItems();
                            for (var i = 0; i < items.length; i++) {
                                if (items[i].getId() === id) {
                                    items = items.slice(0, i+1).concat(contents).concat(items.slice(i+1));
                                    this.initData(items);
                                    break;
                                }
                            }
                        } else { // root path
                            this.initData(contents);
                        }
                    }
                ).catch((reason: any) => {
                    api.notify.showError(reason.toString());
                }).finally(() => {
                }).done();
        }

        private collapseData(item: api.content.ContentSummary) {
            var oldItems = this.gridData.getItems(),
                newItems = [];
            oldItems.forEach((elem, index) => {
                if (!elem.getPath().isDescendantOf(item.getPath())) {
                    newItems.push(elem);
                }
            });
            this.initData(newItems);
        }
    }
}
