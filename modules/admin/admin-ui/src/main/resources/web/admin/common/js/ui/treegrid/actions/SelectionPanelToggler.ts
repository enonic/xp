module api.ui.treegrid.actions {

    export class SelectionPanelToggler extends api.ui.button.TogglerButton {

        private tooltip: Tooltip;

        constructor(treeGrid: TreeGrid<any>) {
            super('selection-toggler');

            this.tooltip = new Tooltip(this, '', 1000);

            treeGrid.onSelectionChanged((currentSelection: TreeNode<any>[], fullSelection: TreeNode<any>[]) => {
                this.setEnabled(fullSelection.length === 1);
                this.setActive(fullSelection.length > 1);

                let oldLabel = this.getLabel();
                let newLabel = fullSelection.length ? fullSelection.length.toString() : '';

                if (oldLabel == newLabel) {
                    return;
                }

                this.removeClass(`size-${oldLabel.length}`);
                this.setLabel(newLabel);
                if (newLabel !== '') {
                    this.addClass(`size-${newLabel.length}`);
                    this.addClass('updated');
                    setTimeout(() => {
                        this.removeClass('updated');
                    }, 200);
                }

            });

            treeGrid.onHighlightingChanged(() => {
                this.setActive(false);
            });

            this.onActiveChanged((isActive: boolean) => {
                this.tooltip.setText(isActive ? 'Hide selection' : 'Show 1 selected item');
            });
        }
    }
}