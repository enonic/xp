module api.ui.treegrid.actions {

    import Action = api.ui.Action;
    import TreeGrid = api.ui.treegrid.TreeGrid;

    export class TreeGridToolbarActions<M extends api.Equitable> {

        public selectionController: SelectionController<M>;

        constructor(grid: TreeGrid<any>) {
            this.selectionController = SelectionController.create().setTreeGrid(grid).build();
        }

        getSelectionController(): SelectionController<M> {
            return this.selectionController;
        }
    }
}
