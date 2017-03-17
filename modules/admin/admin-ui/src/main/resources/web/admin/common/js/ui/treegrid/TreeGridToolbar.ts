module api.ui.treegrid {

    import Button = api.ui.button.Button;
    import SelectionController = api.ui.treegrid.actions.SelectionController;
    import SelectionPanelToggler = api.ui.treegrid.actions.SelectionPanelToggler;

    export class TreeGridToolbar extends api.dom.DivEl {

        private selectionPanelToggler: SelectionPanelToggler;

        constructor(treeGrid: TreeGrid<any>) {
            super('tree-grid-toolbar toolbar');

            const selectionController: SelectionController = new SelectionController(treeGrid);

            this.selectionPanelToggler = new SelectionPanelToggler(treeGrid);

            const refreshButton: Button = new Button();
            refreshButton
                .addClass(api.StyleHelper.getIconCls('loop'))
                .onClicked((event: MouseEvent) => treeGrid.reload());

            this.appendChild(selectionController);
            this.appendChild(this.selectionPanelToggler);
            this.appendChild(refreshButton);
        }

        getSelectionPanelToggler(): SelectionPanelToggler {
            return this.selectionPanelToggler;
        }
    }
}
