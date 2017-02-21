module api.ui.treegrid {

    import TreeGridToolbarActions = api.ui.treegrid.actions.TreeGridToolbarActions;
    import Button = api.ui.button.Button;
    import TogglerButton = api.ui.button.TogglerButton;

    export class TreeGridToolbar extends api.dom.DivEl {

        private treeGrid: TreeGrid<any>;
        private refreshButton: Button;
        private cartButton: TogglerButton;
        private cartButtonListeners: {(isActive: boolean): void}[] = [];

        constructor(actions: TreeGridToolbarActions<any>, treeGrid: TreeGrid<any>) {
            super('tree-grid-toolbar toolbar');

            this.appendChild(actions.getSelectionController());

            this.cartButton = new TogglerButton('icon-cart');
            this.cartButton.onActiveChanged(isActive => this.notifyCartButtonClicked(isActive));
            this.appendChild(this.cartButton);

            treeGrid.onSelectionChanged((currentSelection: TreeNode<any>[], fullSelection: TreeNode<any>[]) => {
                this.cartButton.setEnabled(fullSelection.length === 1);
                this.cartButton.setActive(fullSelection.length > 1);

                let oldLabel = this.cartButton.getLabel();
                let newLabel = fullSelection.length ? fullSelection.length.toString() : '';

                if (oldLabel == newLabel) {
                    return;
                }

                this.cartButton.removeClass(`size-${oldLabel.length}`);
                this.cartButton.setLabel(newLabel);
                if (newLabel !== '') {
                    this.cartButton.addClass(`size-${newLabel.length}`);
                    this.cartButton.addClass('updated');
                    setTimeout(() => {
                        this.cartButton.removeClass('updated');
                    }, 200);
                }
            });

            treeGrid.onHighlightingChanged(() => {
                this.cartButton.setActive(false);
            });

            this.refreshButton = new Button();
            this.refreshButton
                .addClass(api.StyleHelper.getIconCls('loop'))
                .onClicked((event: MouseEvent) => treeGrid.reload());

            this.appendChild(this.refreshButton);

            this.treeGrid = treeGrid;
        }

        onCartButtonClicked(listener: (isActive: boolean) => void) {
            this.cartButtonListeners.push(listener);
        }

        unCartButtonClicked(listener: (isActive: boolean) => void) {
            this.cartButtonListeners = this.cartButtonListeners.filter(curr => {
                return curr !== listener;
            });
        }

        private notifyCartButtonClicked(isActive: boolean) {
            this.cartButtonListeners.forEach(listener => listener(isActive));
        }
    }
}
