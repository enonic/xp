module api.ui.treegrid {

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import TreeGridToolbarActions = api.ui.treegrid.actions.TreeGridToolbarActions;

    export class TreeGridToolbar extends api.ui.toolbar.Toolbar {

        private treeGrid: TreeGrid<any>;
        private refreshButton: api.dom.Element;

        constructor(actions: TreeGridToolbarActions<any>, treeGrid: TreeGrid<any>) {
            super("tree-grid-toolbar");

            this.addGreedySpacer();
            this.refreshButton = new api.ui.button.Button().addClass(api.StyleHelper.getIconCls("loop"));
            this.refreshButton.onClicked((event: MouseEvent) => treeGrid.reload());
            this.addElement(this.refreshButton);

            this.addElement(actions.getSelectionController());

            this.treeGrid = treeGrid;
        }
    }
}
