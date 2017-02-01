module api.ui.treegrid {

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import TreeGridToolbarActions = api.ui.treegrid.actions.TreeGridToolbarActions;
    import Button = api.ui.button.Button;
    import TogglerButton = api.ui.button.TogglerButton;

    export class TreeGridToolbar extends api.ui.toolbar.Toolbar {

        private treeGrid: TreeGrid<any>;
        private refreshButton: Button;
        private cartButton: TogglerButton;
        private cartButtonListeners: {(isActive: boolean): void}[] = [];

        constructor(actions: TreeGridToolbarActions<any>, treeGrid: TreeGrid<any>) {
            super('tree-grid-toolbar');

            this.addElement(actions.getSelectionController());

            this.addGreedySpacer();

            this.cartButton = new TogglerButton('icon-cart');
            this.cartButton.onActiveChanged(isActive => this.notifyCartButtonClicked(isActive));
            this.addElement(this.cartButton);

            treeGrid.onSelectionChanged((currentSelection: TreeNode<any>[], fullSelection: TreeNode<any>[]) => {
                this.cartButton.setEnabled(fullSelection.length == 1);
                this.cartButton.setActive(fullSelection.length > 1);
            });

            this.refreshButton = new Button();
            this.refreshButton
                .addClass(api.StyleHelper.getIconCls('loop'))
                .onClicked((event: MouseEvent) => treeGrid.reload());
            this.addElement(this.refreshButton);

            this.treeGrid = treeGrid;
        }

        onCartButtonClicked(listener: (isActive: boolean) => void) {
            this.cartButtonListeners.push(listener);
        }

        unCartButtonClicked(listener: (isActive: boolean) => void) {
            this.cartButtonListeners = this.cartButtonListeners.filter(curr => {
                return curr !== listener;
            })
        }

        private notifyCartButtonClicked(isActive: boolean) {
            this.cartButtonListeners.forEach(listener => listener(isActive));
        }
    }
}
