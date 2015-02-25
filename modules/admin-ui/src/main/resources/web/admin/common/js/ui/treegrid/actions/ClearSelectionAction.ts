module api.ui.treegrid.actions {

    import Action = api.ui.Action;

    export class ClearSelectionAction<DATA> extends Action {

        constructor(treeGrid: TreeGrid<DATA>, selectedItemsCount?: number) {
            var label = "Clear Selection";
            label += !!selectedItemsCount ? " (" + selectedItemsCount + ")" : "";
            super(label);
            this.setEnabled(true);
            this.onExecuted(() => {
                treeGrid.getRoot().clearStashedSelection();
                treeGrid.getGrid().clearSelection();
            });
        }
    }
}
