import Action = api.ui.Action;
import {ContentBrowseItemsSelectionPanel} from "../ContentBrowseItemsSelectionPanel";
import {ContentTreeGrid} from "../ContentTreeGrid";

export class ShowAllAction<DATA> extends Action {

    constructor(panel: ContentBrowseItemsSelectionPanel, treeGrid: ContentTreeGrid) {
        super(this.createLabel(treeGrid.getRoot().getFullSelection().length));

        this.setEnabled(true);

        this.onExecuted(() => panel.showAll());

        treeGrid.onSelectionChanged(() => {
            const selectedCount = treeGrid.getRoot().getFullSelection().length;
            this.setLabel(this.createLabel(selectedCount));
            this.setEnabled(selectedCount > 0);
        });
    }

    private createLabel(count: number): string {
        return count > 0 ? `Show All (${count})` : 'Show All';
    }
}
