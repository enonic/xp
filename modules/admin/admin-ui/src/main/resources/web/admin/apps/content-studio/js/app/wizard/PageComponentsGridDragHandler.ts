module app.wizard {

    import GridDragHandler = api.ui.grid.GridDragHandler;
    import TreeNode = api.ui.treegrid.TreeNode;
    import DragEventData = api.ui.grid.DragEventData;
    import RegionView = api.liveedit.RegionView;
    import ItemView = api.liveedit.ItemView;
    import PageView = api.liveedit.PageView;
    import ComponentView = api.liveedit.ComponentView;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
    import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;
    import Component = api.content.page.region.Component;

    import DragHelper = api.ui.DragHelper;
    import ElementHelper = api.dom.ElementHelper;
    import Element = api.dom.Element;

    export class PageComponentsGridDragHandler extends GridDragHandler<ItemView> {

        protected handleDragInit(e, dd) {
            var row = this.getRowByTarget(new ElementHelper(<HTMLElement>e.target)),
                nodes = this.contentGrid.getRoot().getCurrentRoot().treeToList(),
                draggedNode = nodes[row.getSiblingIndex()];

            if (draggedNode.getData().isDraggableView()) { // prevent the grid from cancelling drag'n'drop by default
                e.stopImmediatePropagation();
            }
        }

        protected handleDragStart() {
            super.handleDragStart();

            api.liveedit.Highlighter.get().hide();
            this.getDraggableItem().getChildren().forEach((childEl: api.dom.Element) => {
                childEl.removeClass("selected");
            });

            DragHelper.get().setDropAllowed(true);

            api.dom.Body.get().appendChild(DragHelper.get());
            api.dom.Body.get().onMouseMove(this.handleHelperMove);

            this.contentGrid.onMouseLeave(this.handleMouseLeave);
            this.contentGrid.onMouseEnter(this.handleMouseEnter);
        }


        protected handleDragEnd(event: Event, data) {
            api.dom.Body.get().unMouseMove(this.handleHelperMove);
            api.dom.Body.get().removeChild(DragHelper.get());

            this.contentGrid.unMouseLeave(this.handleMouseLeave);
            this.contentGrid.unMouseEnter(this.handleMouseEnter);

            super.handleDragEnd(event, data);
        }

        protected handleBeforeMoveRows(event: Event, data): boolean {

            var dataList = this.contentGrid.getRoot().getCurrentRoot().treeToList();

            var draggableRow = data.rows[0],
                insertBefore = data.insertBefore;

            var insertPosition = (draggableRow > insertBefore) ? insertBefore : insertBefore + 1;

            this.updateDragHelperStatus(draggableRow, insertPosition, dataList);

            if (DragHelper.get().isDropAllowed()) {
                super.handleBeforeMoveRows(event, data);
            }
            return true;
        }

        protected handleMoveRows(event: Event, args: api.ui.grid.DragEventData) {
            if (DragHelper.get().isDropAllowed()) {
                super.handleMoveRows(event, args);
            }
        }

        protected makeMovementInNodes(draggableRow: number, insertBefore: number): number {

            var root = this.contentGrid.getRoot().getCurrentRoot();
            var dataList = root.treeToList();

            var item = dataList.slice(draggableRow, draggableRow + 1)[0];
            var insertPosition = (draggableRow > insertBefore) ? insertBefore : insertBefore + 1;

            this.moveIntoNewParent(item, insertPosition, dataList);

            dataList.splice(dataList.indexOf(item), 1);
            dataList.splice(insertBefore, 0, item);

            return dataList.indexOf(item);
        }


        protected getModelId(model: ItemView) {
            return model ? model.getItemId() : null;
        }

        protected handleMovements(rowDataId, moveBeforeRowDataId) {
            return;
        }

        protected moveIntoNewParent(item: TreeNode<ItemView>, insertBefore: number, data: TreeNode<ItemView>[]) {
            var insertData = this.getParentPosition(insertBefore, data),
                regionPosition = insertData.parentPosition,
                insertIndex = insertData.insertIndex;

            var newParent = data[regionPosition];

            if (newParent == item.getParent() && data.indexOf(item) < insertBefore) {
                insertIndex--;
            }

            this.contentGrid.deselectAll();
            item.getData().deselect();

            (<ComponentView<Component>>item.getData()).moveToRegion(<RegionView>newParent.getData(), insertIndex);

            item.getData().select(null, api.liveedit.ItemViewContextMenuPosition.NONE);
            this.contentGrid.refresh();


            return data[regionPosition];
        }

        private updateDragHelperStatus(draggableRow: number, insertBeforePos: number, data: TreeNode<ItemView>[]) {

            var parentPosition = this.getParentPosition(insertBeforePos, data).parentPosition;

            var parentComponentNode = data[parentPosition],
                parentComponentView = parentComponentNode.getData(),
                draggableComponentView = data[draggableRow].getData();


            if (parentComponentView) {

                if (api.ObjectHelper.iFrameSafeInstanceOf(draggableComponentView, LayoutComponentView)) {
                    if (parentComponentView.getName() != "main") {
                        DragHelper.get().setDropAllowed(false);
                        return;
                    }
                }

                if (api.ObjectHelper.iFrameSafeInstanceOf(parentComponentView, RegionView)) {

                    if (api.ObjectHelper.iFrameSafeInstanceOf(draggableComponentView, FragmentComponentView)) {
                        if (api.ObjectHelper.iFrameSafeInstanceOf(parentComponentView.getParentItemView(), LayoutComponentView)) {
                            if ((<FragmentComponentView> draggableComponentView).containsLayout()) {
                                // Fragment with layout over Layout region
                                DragHelper.get().setDropAllowed(false);
                                return;
                            }
                        }
                    }

                    DragHelper.get().setDropAllowed(true);

                    var draggableItem = this.getDraggableItem();
                    if (draggableItem) {
                        this.updateDraggableItemPosition(draggableItem, parentComponentNode.calcLevel());
                    }
                    return;
                }
            }
            DragHelper.get().setDropAllowed(false);
        }

        private updateDraggableItemPosition(draggableItem: Element, parentLevel: number) {
            var margin = parentLevel * api.ui.treegrid.TreeGrid.LEVEL_STEP_INDENT;
            var nodes = draggableItem.getEl().getElementsByClassName("toggle icon");

            if (nodes.length == 1) {
                nodes[0].setMarginLeft(margin + "px");
            }
        }


        private getParentPosition(insertBeforePos: number, data: TreeNode<ItemView>[]): InsertData {
            var parentPosition = insertBeforePos,
                insertIndex = 0;

            if (!data[insertBeforePos - 1]) {
                return {parentPosition: 0, insertIndex: 0};
            }

            var calcLevel = data[parentPosition - 1].calcLevel();

            var isFirstChildPosition = ( data[insertBeforePos]
                    ? data[insertBeforePos - 1].calcLevel() < data[insertBeforePos].calcLevel()
                    : false) ||
                                       (api.ObjectHelper.iFrameSafeInstanceOf(data[insertBeforePos - 1].getData(), RegionView));

            do {

                parentPosition = parentPosition <= 0 ? 0 : parentPosition - 1;


                var parentComponentNode = data[parentPosition],
                    parentComponentView = parentComponentNode.getData();

                if (parentComponentNode.calcLevel() == calcLevel && !isFirstChildPosition) {
                    insertIndex++;
                }


            } while (!(api.ObjectHelper.iFrameSafeInstanceOf(parentComponentView, RegionView) ||

                     ( api.ObjectHelper.iFrameSafeInstanceOf(parentComponentView, LayoutComponentView) &&  // lets drag items inside the 'main' region between layouts
                       (parentComponentNode.isExpanded() && parentComponentNode.getChildren().length > 0) ) ||

                     api.ObjectHelper.iFrameSafeInstanceOf(parentComponentView, PageView))

                     || (parentComponentNode.calcLevel() >= calcLevel && !isFirstChildPosition));

            return {parentPosition: parentPosition, insertIndex: insertIndex};
        }

        private getRowByTarget(el: ElementHelper): ElementHelper {

            return (el && el.hasClass("slick-row")) ? el : this.getRowByTarget(el.getParent());
        }

        private handleMouseLeave() {
            DragHelper.get().setVisible(false);
        }

        private handleMouseEnter() {
            DragHelper.get().setVisible(true);
        }


        private handleHelperMove(event: MouseEvent) {
            DragHelper.get().getEl().setLeftPx(event.pageX);
            DragHelper.get().getEl().setTopPx(event.pageY);
        }

    }


    export interface InsertData {
        parentPosition: number;
        insertIndex: number;
    }
}
