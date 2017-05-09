module api.ui.treegrid.actions {

    export class SelectionPanelToggler extends api.ui.button.TogglerButton {

        private tooltip: Tooltip;

        constructor(treeGrid: TreeGrid<any>) {
            super('selection-toggler');

            this.setEnabled(true);

            this.tooltip = new Tooltip(this, '', 1000);

            treeGrid.onSelectionChanged((currentSelection: TreeNode<any>[], fullSelection: TreeNode<any>[]) => {

                let oldLabel = this.getLabel();
                let newLabel = fullSelection.length ? fullSelection.length.toString() : '';

                if (oldLabel == newLabel) {
                    return;
                }
                this.tooltip.setText(this.isActive() ? 'Hide selection' : 'Show selection');

                this.removeClass('single-item multiple-items');
                this.removeClass(`size-${oldLabel.length}`);
                this.setLabel(newLabel);
                if (newLabel !== '') {
                    this.addClass(`size-${newLabel.length}`);
                    this.addClass('updated');
                    if (fullSelection.length == 1) {
                        this.addClass('single-item');
                    } else if (fullSelection.length > 1) {
                        this.addClass('multiple-items');
                    }
                    setTimeout(() => {
                        this.removeClass('updated');
                    }, 200);
                }

            });

            this.onActiveChanged((isActive: boolean) => {
                let isVisible = this.tooltip.isVisible();
                if (isVisible) {
                    this.tooltip.hide();
                }
                this.tooltip.setText(isActive ? 'Hide selection' : 'Show selection');
                if (isVisible) {
                    this.tooltip.show();
                }
            });
        }
    }
}
