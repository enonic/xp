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

        protected handleDragInit(event: DragEvent, data: DragEventData) {
            event.stopImmediatePropagation();
        }

        protected handleDragStart() {
            let draggableClass = this.contentGrid.getOptions().getSelectedCellCssClass() || '';
            draggableClass = (' ' + draggableClass).replace(/\s/g, '.');
            let row = Element.fromString(draggableClass).getParentElement();

            let nodes = this.contentGrid.getRoot().getCurrentRoot().treeToList();
            let draggedNode = nodes[row.getSiblingIndex()];
            draggedNode.setExpanded(false);
            this.contentGrid.refreshNode(draggedNode);

            row = Element.fromString(draggableClass).getParentElement();

            this.draggableTop = row.getEl().getTopPx();
            this.draggableItem = Element.fromString(row.toString());

            this.draggableItem.addClass('draggable');
            row.getEl().setDisplay('none');

            this.rowHeight = row.getEl().getHeight();
            let proxyEl = Element.fromString('.slick-reorder-proxy').getEl();
            this.draggableItem.getEl().setTop(proxyEl.getTop()).setPosition('absolute');
            let gridClasses = (' ' + this.contentGrid.getGrid().getEl().getClass()).replace(/\s/g, '.');

            wemjq('.tree-grid ' + gridClasses + ' .slick-viewport').append(wemjq(this.draggableItem.getHTMLElement()));
        }

        protected handleDrag(event: Event, data: DragEventData) {
            if (!this.draggableItem) {
                this.handleDragStart();
            }
            let top = Element.fromString('.slick-reorder-proxy').getEl().getTopPx();
            this.draggableItem.getEl().setTopPx(top /*- this.rowHeight*//* / 2*/).setZindex(2);
        }

        protected handleDragEnd(event: Event, data: DragEventData) {
            this.draggableItem.remove();
            this.draggableItem = null;
            this.contentGrid.refresh();
        }

        protected handleBeforeMoveRows(event: Event, data: DragEventData): boolean {

            if (!this.draggableItem) {
                this.handleDragStart();
            }
            const gridClasses = (' ' + this.contentGrid.getGrid().getEl().getClass()).replace(/\s/g, '.');
            const children = Element.fromSelector('.tree-grid ' + gridClasses + ' .grid-canvas .slick-row', false);

            if (children && !children[0].getPreviousElement()) {
                children.shift();
            }

            const setMarginTop = (element: Element, margin: string) => element.getEl().setMarginTop(margin);

            children.forEach((child: Element, index: number) => {
                if (data.rows[0] <= data.insertBefore) { //move item down
                    if (index > data.rows[0] && index <= data.insertBefore) {
                        setMarginTop(child, `-${this.rowHeight}px`);
                    } else {
                        setMarginTop(child, null);
                    }
                } else if (data.rows[0] >= data.insertBefore) { //move item up
                    if (index < data.rows[0] && index >= data.insertBefore) {
                        setMarginTop(child, `${this.rowHeight}px`);
                    } else {
                        setMarginTop(child, null);
                    }
                }
            });

            this.contentGrid.scrollToRow(data.insertBefore);
            return true;
        }

        protected handleMoveRows(event: Event, args: DragEventData) {
            let dataView = this.contentGrid.getGrid().getDataView();
            let draggableRow = args.rows[0];

            let rowDataId = this.getModelId(dataView.getItem(draggableRow).getData());
            let insertTarget = args.insertBefore;

            // when dragging forwards/down insertBefore is the target element
            // when dragging backwards/up insertBefore is one position after the target element
            let insertBefore = draggableRow < insertTarget ? insertTarget + 1 : insertTarget;

            let moveBeforeRowDataId = ((dataView.getLength() - 1) <= insertTarget)
                ? null
                : this.getModelId(dataView.getItem(insertBefore).getData());

            // draggable count in new data
            let selectedRow = this.makeMovementInNodes(draggableRow, insertTarget);

            if (selectedRow <= this.contentGrid.getRoot().getCurrentRoot().treeToList().length - 1) {
                this.contentGrid.getGrid().setSelectedRows([selectedRow]);
            }
            this.handleMovements(rowDataId, moveBeforeRowDataId);

            this.notifyPositionChanged();
        }

        protected makeMovementInNodes(draggableRow: number, insertBefore: number): number {

            let root = this.contentGrid.getRoot().getCurrentRoot();
            let rootChildren = root.treeToList();

            let item = rootChildren.slice(draggableRow, draggableRow + 1)[0];
            rootChildren.splice(rootChildren.indexOf(item), 1);
            rootChildren.splice(insertBefore, 0, item);

            this.contentGrid.initData(rootChildren);
            root.setChildren(rootChildren);

            return rootChildren.indexOf(item);

        }

        getDraggableItem(): Element {
            return this.draggableItem;
        }

        protected handleMovements(rowDataId: any, moveBeforeRowDataId: any) {
            throw new Error('Must be implemented by inheritors');
        }

        protected getModelId(model: MODEL): any {
            throw new Error('Must be implemented by inheritors');
        }

        onPositionChanged(listener: ()=>void) {
            this.positionChangedListeners.push(listener);
        }

        unPositionChanged(listener: ()=>void) {
            this.positionChangedListeners = this.positionChangedListeners.filter((currentListener: ()=>void) => {
                return currentListener !== listener;
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
