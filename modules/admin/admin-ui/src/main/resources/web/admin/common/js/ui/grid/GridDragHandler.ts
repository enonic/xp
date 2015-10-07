module api.ui.grid {

    import Element = api.dom.Element;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;

    import DragHelper = api.ui.DragHelper;
    import ElementHelper = api.dom.ElementHelper;

    export class GridDragHandler<MODEL> {

        protected contentGrid: TreeGrid<MODEL>;

        private positionChangedListeners: {(event: Event):void}[] = [];

        private draggableItem: Element;

        private draggableTop: number;

        private rowHeight: number;

        constructor(treeGrid: TreeGrid<MODEL>) {
            this.contentGrid = treeGrid;
            this.contentGrid.getGrid().subscribeOnDrag(this.handleDrag.bind(this));
            this.contentGrid.getGrid().subscribeOnDragInit(this.handleDragInit.bind(this));
            this.contentGrid.getGrid().subscribeOnDragEnd(this.handleDragEnd.bind(this));
            this.contentGrid.getGrid().subscribeBeforeMoveRows(this.handleBeforeMoveRows.bind(this));
            this.contentGrid.getGrid().subscribeMoveRows(this.handleMoveRows.bind(this));
        }


        protected handleDragInit(e, dd) {
            e.stopImmediatePropagation();
        }


        protected handleDragStart() {
            var draggableClass = this.contentGrid.getOptions().getSelectedCellCssClass() || "";
            draggableClass = (" " + draggableClass).replace(/\s/g, ".");
            var row = Element.fromString(draggableClass).getParentElement();

            var nodes = this.contentGrid.getRoot().getCurrentRoot().treeToList(),
                draggedNode = nodes[row.getSiblingIndex()];
            draggedNode.setExpanded(false);
            this.contentGrid.refreshNode(draggedNode);

            row = Element.fromString(draggableClass).getParentElement();

            this.draggableTop = row.getEl().getTopPx();
            this.draggableItem = Element.fromString(row.toString());

            this.draggableItem.addClass("draggable");
            row.getEl().setDisplay("none");

            this.rowHeight = row.getEl().getHeight();
            var proxyEl = Element.fromString(".slick-reorder-proxy").getEl();
            this.draggableItem.getEl().setTop(proxyEl.getTop()).setPosition("absolute");
            var gridClasses = (" " + this.contentGrid.getGrid().getEl().getClass()).replace(/\s/g, ".");


            wemjq(".tree-grid " + gridClasses + " .slick-viewport").append(wemjq(this.draggableItem.getHTMLElement()));
        }


        protected handleDrag(event: Event, data: DragEventData) {
            if (!this.draggableItem) {
                this.handleDragStart();
            }
            var top = Element.fromString(".slick-reorder-proxy").getEl().getTopPx();
            this.draggableItem.getEl().setTopPx(top /*- this.rowHeight*//* / 2*/).setZindex(2);
        }


        protected handleDragEnd(event: Event, data) {
            this.draggableItem.remove();
            this.draggableItem = null;
            this.contentGrid.refresh();
        }

        protected handleBeforeMoveRows(event: Event, data: DragEventData): boolean {

            if (!this.draggableItem) {
                this.handleDragStart();
            }
            var gridClasses = (" " + this.contentGrid.getGrid().getEl().getClass()).replace(/\s/g, "."),
                children = Element.fromSelector(".tree-grid " + gridClasses + " .grid-canvas .slick-row", false);

            for (var key in children) {
                if (data.rows[0] <= data.insertBefore) {//move item down
                    if (key > data.rows[0] && key <= data.insertBefore) {
                        children[key].getEl().setMarginTop("-" + this.rowHeight + "px");
                    } else {
                        children[key].getEl().setMarginTop(null);
                    }
                    continue;
                }
                if (data.rows[0] >= data.insertBefore) {//move item up
                    if (key < data.rows[0] && key >= data.insertBefore) {
                        children[key].getEl().setMarginTop(this.rowHeight + "px");
                    } else {
                        children[key].getEl().setMarginTop(null);
                    }
                }
            }
            this.contentGrid.scrollToRow(data.insertBefore);
            return true;
        }


        protected handleMoveRows(event: Event, args: DragEventData) {
            var dataView = this.contentGrid.getGrid().getDataView();
            var draggableRow = args.rows[0];

            var rowDataId = this.getModelId(dataView.getItem(draggableRow).getData());
            var insertBefore = args.insertBefore;
            var moveBeforeRowDataId = ((dataView.getLength() - 1) <= insertBefore)
                ? null
                : this.getModelId(dataView.getItem(insertBefore).getData());

            // draggable count in new data
            var selectedRow = this.makeMovementInNodes(draggableRow, insertBefore);

            if (selectedRow <= this.contentGrid.getRoot().getCurrentRoot().treeToList().length - 1) {
                this.contentGrid.getGrid().setSelectedRows([selectedRow]);
            }
            this.handleMovements(rowDataId, moveBeforeRowDataId);

            this.notifyPositionChanged();
        }


        protected makeMovementInNodes(draggableRow: number, insertBefore: number): number {

            var root = this.contentGrid.getRoot().getCurrentRoot();
            var rootChildren = root.treeToList();

            var item = rootChildren.slice(draggableRow, draggableRow + 1)[0];
            rootChildren.splice(rootChildren.indexOf(item), 1);
            rootChildren.splice(insertBefore, 0, item);

            this.contentGrid.initData(rootChildren);
            root.setChildren(rootChildren);

            return rootChildren.indexOf(item);

        }

        getDraggableItem(): Element {
            return this.draggableItem;
        }

        protected handleMovements(rowDataId, moveBeforeRowDataId) {
            throw new Error("Must be implemented by inheritors");
        }

        protected getModelId(model: MODEL): any {
            throw new Error("Must be implemented by inheritors");
        }

        onPositionChanged(listener: ()=>void) {
            this.positionChangedListeners.push(listener);
        }

        unPositionChanged(listener: ()=>void) {
            this.positionChangedListeners = this.positionChangedListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            });
        }

        private notifyPositionChanged() {
            this.positionChangedListeners.forEach((listener: ()=>void)=> {
                listener.call(this);
            });
        }
    }

    export interface DragEventData {
        insertBefore: number;
        rows: number[];
    }
}