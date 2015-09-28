module api.ui.treegrid {

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import TreeGridToolbarActions = api.ui.treegrid.actions.TreeGridToolbarActions;

    export class TreeGridToolbar extends api.ui.toolbar.Toolbar {

        private treeGrid: TreeGrid<any>;
        private refreshButton: api.dom.Element;

        constructor(actions: TreeGridToolbarActions<any>, treeGrid: TreeGrid<any>) {
            super();

            this.addActions(actions.getAllActions());

            this.addGreedySpacer();
            this.refreshButton = new api.ui.button.Button().addClass('icon-loop2');
            this.refreshButton.onClicked((event: MouseEvent) => treeGrid.reload());
            this.addElement(this.refreshButton);

            this.treeGrid = treeGrid;
        }

        refresh(selectedItemsCount: number) {
            this.removeChild(this.refreshButton);
            this.removeGreedySpacer();
            this.removeActions();

            var actions = new api.ui.treegrid.actions.TreeGridToolbarActions(this.treeGrid, selectedItemsCount).getAllActions();

            this.addActions(actions);
            this.addGreedySpacer();
            this.addElement(this.refreshButton);
        }

    }
}
