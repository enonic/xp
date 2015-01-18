module api.ui.treegrid {

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class TreeGridToolbar extends api.ui.toolbar.Toolbar {

        private treeGrid: TreeGrid<any>;

        constructor(actions: TreeGridActions, treeGrid: TreeGrid<any>) {
            super();

            this.addActions(actions.getAllActions());
            this.treeGrid = treeGrid;
        }

        refresh() {
            this.removeActions();
            var actions = new api.ui.treegrid.actions.TreeGridToolbarActions(this.treeGrid.getGrid()).getAllActions();
            this.addActions(actions);
        }

    }
}
