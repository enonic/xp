module api.ui.treegrid.actions {

    import Action = api.ui.Action;

    export class SelectionController extends Checkbox {

        private tooltip: Tooltip;

        constructor(treeGrid: TreeGrid<any>) {
            super(new CheckboxBuilder());

            this.addClass('selection-controller');

            treeGrid.onSelectionChanged(() => {

                if (this.isDisabled() != treeGrid.isEmpty()) {
                    this.setDisabled(treeGrid.isEmpty());
                }

                if (treeGrid.isAllSelected() && !treeGrid.isEmpty()) {
                    this.setChecked(true, true);
                } else {
                    if (this.isChecked()) {
                        this.setChecked(false, true);
                    }
                }
                this.setPartial(this.treeGrid.isAnySelected() && !this.treeGrid.isAllSelected());

                this.tooltip.setText(this.isChecked() ? 'Clear selection' : 'Select all rows');
            });

            this.onClicked((event) => {

                event.preventDefault();

                if (this.isDisabled()) {
                    return;
                }

                if (this.isChecked() || this.isPartial()) {
                    this.treeGrid.getRoot().clearStashedSelection();
                    this.treeGrid.getGrid().clearSelection();
                } else {
                    treeGrid.selectAll();
                }
            });

            this.onRendered(() => {
                this.setChecked(false, true);
            });

            this.tooltip = new Tooltip(this, '', 1000);
        }
    }
}
