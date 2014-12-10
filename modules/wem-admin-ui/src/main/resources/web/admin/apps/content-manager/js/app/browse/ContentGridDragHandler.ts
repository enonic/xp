module app.browse {

    import OrderChildMovement = api.content.OrderChildMovement;
    import OrderChildMovements = api.content.OrderChildMovements;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import Element = api.dom.Element;
    import TreeGrid = api.ui.treegrid.TreeGrid;

    export class ContentGridDragHandler {

        private movements: OrderChildMovements;

        private contentGrid: TreeGrid<ContentSummaryAndCompareStatus>;

        private positionChangedListeners: {(event: Event):void}[] = [];

        private draggableItem: Element;

        private draggableTop: number;

        private rowHeight: number;

        constructor(treeGrid: TreeGrid<ContentSummaryAndCompareStatus>) {
            this.contentGrid = treeGrid;
            this.contentGrid.getGrid().subscribeOnDrag(this.handleDrag.bind(this));
            this.contentGrid.getGrid().subscribeOnDragEnd(this.handleDragEnd.bind(this));
            this.contentGrid.getGrid().subscribeBeforeMoveRows(this.handleBeforeMoveRows.bind(this));
            this.contentGrid.getGrid().subscribeMoveRows(this.handleMoveRows.bind(this));
            this.movements = new OrderChildMovements();
        }

        getContentMovements(): OrderChildMovements {
            return this.movements;
        }

        clearContentMovements() {
            this.movements = new OrderChildMovements();
        }

        handleDragStart() {
            var draggableClass = this.contentGrid.getOptions().getSelectedCellCssClass();
            draggableClass = (" " + draggableClass).replace(/\s/g, ".");
            var row = Element.fromString(draggableClass).getParentElement();
            this.draggableTop = row.getEl().getTopPx();
            this.draggableItem = Element.fromString(row.toString());
            row.getEl().setDisplay("none");

            this.rowHeight = row.getEl().getHeight();
            var proxyEl = Element.fromString(".slick-reorder-proxy").getEl();
            this.draggableItem.getEl().setTop(proxyEl.getTop()).setPosition("absolute");
            var gridClasses = (" " + this.contentGrid.getGrid().getEl().getClass()).replace(/\s/g, ".");
            wemjq(".tree-grid " + gridClasses + " .slick-viewport").append(this.draggableItem.getHTMLElement());
        }


        handleDrag(event: Event, data: DragEventData) {
            if (!this.draggableItem) {
                this.handleDragStart();
            }
            var top = Element.fromString(".slick-reorder-proxy").getEl().getTopPx();
            this.draggableItem.getEl().setTopPx(top - this.rowHeight / 2).setZindex(2);
        }


        handleDragEnd(event: Event, data) {
            this.contentGrid.refresh();
            this.draggableItem.remove();
            this.draggableItem = null;
        }

        private prev = 0;

        handleBeforeMoveRows(event: Event, data: DragEventData) {
            if (!this.draggableItem) {
                this.handleDragStart();
            }
            var gridClasses = (" " + this.contentGrid.getGrid().getEl().getClass()).replace(/\s/g, "."),
                children = Element.elementsFromRequest(".tree-grid " + gridClasses + " .grid-canvas .slick-row", false);

            var draggableTop = !!this.draggableItem ? this.draggableItem.getEl().getTopPx() : 0;

            for (var key in children) {
                var currentRowTop = children[key].getEl().getTopPx();
                if (data.rows[0] < data.insertBefore) {
                    if (this.draggableTop < currentRowTop && currentRowTop - this.rowHeight / 2 <= draggableTop) {
                        children[key].getEl().setMarginTop("-" + this.rowHeight + "px");
                    } else {
                        children[key].getEl().setMarginTop(null);
                    }
                } else if (data.rows[0] > data.insertBefore) {
                    if (this.draggableTop > currentRowTop && currentRowTop + this.rowHeight / 2 >= draggableTop) {
                        children[key].getEl().setMarginTop(this.rowHeight + "px");
                    } else {
                        children[key].getEl().setMarginTop(null);
                    }
                }
            }
            this.contentGrid.scrollToRow(data.insertBefore);
            return true;
        }


        handleMoveRows(event: Event, args: DragEventData) {
            var dataView = this.contentGrid.getGrid().getDataView();
            var draggableRow = args.rows[0];

            var rowDataId = dataView.getItem(draggableRow).getData().getContentId();
            var moveBeforeRowDataId = (dataView.getLength() <= args.insertBefore)
                ? null
                : dataView.getItem(args.insertBefore).getData().getContentId();
            var extractedRows = [], left, right;
            var insertBefore = <number>args.insertBefore;
            left = dataView.slick().getItems().slice(0, insertBefore);
            right = dataView.slick().getItems().slice(insertBefore, dataView.slick().getItems().length);


            extractedRows.push(dataView.slick().getItems()[draggableRow]);

            if (draggableRow < insertBefore) {
                left.splice(draggableRow, 1);
            } else {
                right.splice(draggableRow - insertBefore, 1);
            }

            var data = left.concat(extractedRows.concat(right));

            var selectedRows = left.length;

            var root = this.contentGrid.getRoot().getCurrentRoot();
            var rootChildren = root.getChildren();

            var item = rootChildren.slice(draggableRow, draggableRow + 1)[0];
            this.contentGrid.initData(data);
            this.contentGrid.getGrid().setSelectedRows([selectedRows]);
            rootChildren.splice(rootChildren.indexOf(item), 1);
            var insertPosition = (draggableRow > args.insertBefore) ? args.insertBefore : args.insertBefore - 1;
            rootChildren.splice(insertPosition, 0, item);
            root.setChildren(rootChildren);

            this.movements.addChildMovement(new OrderChildMovement(rowDataId, moveBeforeRowDataId));
            this.notifyPositionChanged();
        }

        private notifyPositionChanged() {
            this.positionChangedListeners.forEach((listener: ()=>void)=> {
                listener.call(this);
            });
        }

        onPositionChanged(listener: ()=>void) {
            this.positionChangedListeners.push(listener);
        }

        unPositionChanged(listener: ()=>void) {
            this.positionChangedListeners = this.positionChangedListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            });
        }
    }

    export interface DragEventData {
        insertBefore: number;
        rows: number[];
    }

}