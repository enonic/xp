module app.wizard {

    import GridDragHandler = api.ui.grid.GridDragHandler;
    import TreeNode = api.ui.treegrid.TreeNode;
    import RegionView = api.liveedit.RegionView;
    import ItemView = api.liveedit.ItemView;
    import PageView = api.liveedit.PageView;
    import ComponentView = api.liveedit.ComponentView;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;
    import Component = api.content.page.region.Component;

    import DragHelper = api.ui.DragHelper;
    import ElementHelper = api.dom.ElementHelper;

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

            this.getDraggableItem().getChildren().forEach((childEl: api.dom.Element) => {
                childEl.removeClass("selected");
            });

            DragHelper.get().setDropAllowed(true);
            this.getDraggableItem().appendChild(DragHelper.get());
        }

        protected handleBeforeMoveRows(event: Event, data): boolean {
            super.handleBeforeMoveRows(event, data);

            var dataList = this.contentGrid.getRoot().getCurrentRoot().treeToList();

            var draggableRow = data.rows[0],
                insertBefore = data.insertBefore;

            this.updateDragHelperStatus(draggableRow, insertBefore, dataList);
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
            var insertPosition = (draggableRow > insertBefore) ? insertBefore : insertBefore - 1;

            this.moveIntoNewParent(item, insertBefore, dataList);

            dataList.splice(dataList.indexOf(item), 1);
            dataList.splice(insertPosition, 0, item);

            return dataList.indexOf(item);
        }


        protected getModelId(model: ItemView) {
            return model ? model.getItemId() : null;
        }

        protected handleMovements(rowDataId, moveBeforeRowDataId) {
            return;
        }

        private getRowByTarget(el: ElementHelper): ElementHelper {

            return (el && el.hasClass("slick-row")) ? el : this.getRowByTarget(el.getParent());
        }


        private updateDragHelperStatus(draggableRow: number, insertBeforePos: number, data: TreeNode<ItemView>[]) {

            var parentPosition = insertBeforePos <= 0 ? 0 : insertBeforePos - 1;

            var parentComponentNode = data[parentPosition],
                parentComponentView = parentComponentNode.getData(),
                draggableComponentView = data[draggableRow].getData();

            if (parentComponentView) {
                while (!(api.ObjectHelper.iFrameSafeInstanceOf(parentComponentView, RegionView) ||

                         ( api.ObjectHelper.iFrameSafeInstanceOf(parentComponentView, LayoutComponentView) &&  // lets drag items inside the 'main' region between layouts
                           (parentComponentNode.isExpanded() && parentComponentNode.getChildren().length > 0) ) ||

                         api.ObjectHelper.iFrameSafeInstanceOf(parentComponentView, PageView))) {

                    parentPosition = parentPosition - 1;
                    parentComponentView = data[parentPosition].getData();
                }

                if (api.ObjectHelper.iFrameSafeInstanceOf(draggableComponentView, LayoutComponentView)) {
                    if (parentComponentView.getName() != "main") {
                        DragHelper.get().setDropAllowed(false);
                        return;
                    }
                }

                if (api.ObjectHelper.iFrameSafeInstanceOf(parentComponentView, RegionView)) {
                    DragHelper.get().setDropAllowed(true);
                    return;
                }
            }
            DragHelper.get().setDropAllowed(false);
        }


        private moveIntoNewParent(item: TreeNode<ItemView>, insertBefore: number, data: TreeNode<ItemView>[]) {
            var regionPosition = insertBefore - 1;
            var insertIndex = 0;
            while (!(api.ObjectHelper.iFrameSafeInstanceOf(data[regionPosition].getData(), RegionView) ||
                     api.ObjectHelper.iFrameSafeInstanceOf(data[regionPosition].getData(), PageView))) {
                regionPosition = regionPosition - 1;
                insertIndex++;
            }

            var newParent = data[regionPosition];

            this.contentGrid.deselectAll();
            (<ComponentView<Component>>item.getData()).moveToRegion(<RegionView>newParent.getData(), insertIndex);
            this.contentGrid.refresh();


            return data[regionPosition];
        }

    }
}
