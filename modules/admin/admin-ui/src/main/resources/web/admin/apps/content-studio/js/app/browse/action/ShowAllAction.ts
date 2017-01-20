import Action = api.ui.Action;
import {ContentBrowseItemsSelectionPanel} from '../ContentBrowseItemsSelectionPanel';
import {ContentTreeGrid} from '../ContentTreeGrid';

export class ShowAllAction<DATA> extends Action {

    constructor(panel: ContentBrowseItemsSelectionPanel, treeGrid: ContentTreeGrid) {
        super();

        this.createLabel(treeGrid);

        this.setEnabled(true);

        this.onExecuted(() => panel.showAll());

        treeGrid.onSelectionChanged(() => {
            const selectedCount = treeGrid.getRoot().getFullSelection().length;
            this.createLabel(treeGrid);
            this.setEnabled(selectedCount > 0);
        });
    }

    private createLabel(treeGrid: ContentTreeGrid) {
        let selectedCount = treeGrid.getRoot().getFullSelection().length;
        let label = selectedCount > 0 ? `Show All (${selectedCount})` : 'Show All';

        this.setLabel(label);
    }
}
