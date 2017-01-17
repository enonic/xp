module api.ui.treegrid.actions {

    import Action = api.ui.Action;

    export class SelectionController<DATA> extends Checkbox {

        private treeGrid: TreeGrid<DATA>;

        private tooltip: Tooltip;

        constructor(builder: SelectionControllerBuilder<DATA>) {
            super(builder);

            this.addClass("selection-controller");

            this.treeGrid = builder.treeGrid;

            this.treeGrid.onSelectionChanged(() => {
                if (this.treeGrid.isAllSelected()) {
                    this.setChecked(true, true);
                } else {
                    if (this.isChecked()) {
                        this.setChecked(false, true);
                    }
                }

                this.tooltip.setText(this.isChecked() ? "Clear selection" : "Select all rows");
            });

            this.onClicked((event) => {

                if (this.isChecked()) {
                    this.treeGrid.getRoot().clearStashedSelection();
                    this.treeGrid.getGrid().clearSelection();
                } else {
                    this.treeGrid.selectAll();
                }

                event.preventDefault();
            });

            this.onRendered(() => {
                this.setChecked(false, true);
            });

            this.tooltip = new Tooltip(this, "", 1000);
        }

        static create(): SelectionControllerBuilder<any> {
            return new SelectionControllerBuilder();
        }
    }

    export class SelectionControllerBuilder<DATA> extends CheckboxBuilder {

        treeGrid: TreeGrid<DATA>;

        public setTreeGrid(value: TreeGrid<DATA>): SelectionControllerBuilder<DATA> {
            this.treeGrid = value;
            return this;
        }

        build(): SelectionController<DATA> {
            return new SelectionController(this);
        }
    }
}