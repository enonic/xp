import "../../api.ts";

import GridDragHandler = api.ui.grid.GridDragHandler;
import TreeGrid = api.ui.treegrid.TreeGrid;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import OrderChildMovement = api.content.order.OrderChildMovement;
import OrderChildMovements = api.content.order.OrderChildMovements;
import ContentId = api.content.ContentId;

export class ContentGridDragHandler extends GridDragHandler<ContentSummaryAndCompareStatus> {

    private movements: OrderChildMovements;

    constructor(treeGrid: TreeGrid<ContentSummaryAndCompareStatus>) {
        super(treeGrid);
        this.movements = new OrderChildMovements();
    }

    getContentMovements(): OrderChildMovements {
        return this.movements;
    }

    clearContentMovements() {
        this.movements = new OrderChildMovements();
    }

    handleMovements(rowDataId: ContentId, moveBeforeRowDataId: ContentId) {
        this.movements.addChildMovement(new OrderChildMovement(rowDataId, moveBeforeRowDataId));
    }

    getModelId(model: ContentSummaryAndCompareStatus) {
        return model ? model.getContentId() : null;
    }
}
