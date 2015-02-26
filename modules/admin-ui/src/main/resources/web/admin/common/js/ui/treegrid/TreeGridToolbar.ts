module api.ui.treegrid {

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import TreeGridToolbarActions = api.ui.treegrid.actions.TreeGridToolbarActions;

    export class TreeGridToolbar extends api.ui.toolbar.Toolbar {

        private treeGrid: TreeGrid<any>;

        constructor(actions: TreeGridToolbarActions<any>, treeGrid: TreeGrid<any>) {
            super();

            this.addActions(actions.getAllActions());
            this.treeGrid = treeGrid;
        }

        refresh(selectedItemsCount: number) {
            this.removeActions();
            var actions = new api.ui.treegrid.actions.TreeGridToolbarActions(this.treeGrid, selectedItemsCount).getAllActions();
            this.addActions(actions);
        }

    }
}
