import Action = api.ui.Action;
import {ContentBrowseItemsSelectionPanel} from "../ContentBrowseItemsSelectionPanel";

export class ShowAllAction<DATA> extends Action {

    constructor(panel: ContentBrowseItemsSelectionPanel) {
        super("Show All");

        this.setEnabled(true);

        this.onExecuted(() => panel.showAll());
    }
}
