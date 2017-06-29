module api.ui.selector {

    import Item = api.item.Item;

    import ElementHelper = api.dom.ElementHelper;
    import ValidationRecordingViewer = api.form.ValidationRecordingViewer;

    import Grid = api.ui.grid.Grid;
    import GridOptions = api.ui.grid.GridOptions;
    import GridColumn = api.ui.grid.GridColumn;
    import GridOptionsBuilder = api.ui.grid.GridOptionsBuilder;
    import DataView = api.ui.grid.DataView;
    import KeyBinding = api.ui.KeyBinding;
    import KeyBindings = api.ui.KeyBindings;

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
    import AppHelper = api.util.AppHelper;
    import DataChangedEvent = api.ui.treegrid.DataChangedEvent;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeNodeBuilder = api.ui.treegrid.TreeNodeBuilder;
    import TreeRoot = api.ui.treegrid.TreeRoot;
    import Element = api.dom.Element;

    export class DropdownTreeGrid<OPTION_DISPLAY_VALUE> extends DropdownGrid<OPTION_DISPLAY_VALUE> {

        private optionsTreeGrid: OptionsTreeGrid<OPTION_DISPLAY_VALUE>;

        constructor(config: DropdownGridConfig<OPTION_DISPLAY_VALUE>) {
            super(config);

            this.optionsTreeGrid.getGrid().getDataView().onRowCountChanged(() => this.notifyRowCountChanged());
        }

        expandActiveRow() {
            if (!this.hasActiveRow()) {
                return;
            }
            this.optionsTreeGrid.expandRow(this.getActiveRow());
        }

        collapseActiveRow() {
            if (!this.hasActiveRow()) {
                return;
            }
            this.optionsTreeGrid.collapseRow(this.getActiveRow());
        }

        reload(): wemQ.Promise<void> {
            return this.optionsTreeGrid.reload();
        }

        setReadonlyChecker(checker: (optionToCheck: OPTION_DISPLAY_VALUE) => boolean) {
            this.optionsTreeGrid.setReadonlyChecker(checker);
        }

        presetDefaultOption(data: OPTION_DISPLAY_VALUE) {
            this.optionsTreeGrid.presetDefaultOption(data);
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[]) {
            this.optionsTreeGrid.setOptions(options);
        }

        getSelectedOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.optionsTreeGrid.getSelectedNodes().map(selectedNode => {
                return this.getOptionByValue(selectedNode.getDataId());
            });
        }

        protected initGridAndData() {
            if (this.filter) {
                // TODO
                // this.getGridData().setFilter(this.filter);
            }

            this.optionsTreeGrid = new OptionsTreeGrid(this.createColumns(),
                this.createOptions(),
                this.config.optionDataLoader,
                this.config.optionDataHelper);
        }

        protected initGridEventListeners() {
            this.getGrid().subscribeOnClick((e, args) => {
                const elem = new ElementHelper(e.target);

                let isCheckboxClicked = elem.hasClass('slick-cell-checkboxsel') || elem.hasAnyParentClass('slick-cell-checkboxsel');

                if (!elem.hasClass('expand collapse') && !isCheckboxClicked) {
                    //also should not be called for checkbox
                    this.notifyRowSelection(args.row);
                    e.preventDefault();
                    return false;
                }
            });

            this.getGrid().subscribeOnSelectedRowsChanged((e, args) => {
                this.notifyMultipleSelection(args.rows);
            });
        }

        markSelections(selectedOptions: Option<OPTION_DISPLAY_VALUE>[], ignoreEmpty: boolean = false) {
            this.optionsTreeGrid.getRoot().clearStashedSelection();
            super.markSelections(selectedOptions, ignoreEmpty);
        }

        protected createColumns(): api.ui.grid.GridColumn<any>[] {
            let columnFormatter =
                (row: number, cell: number, value: OPTION_DISPLAY_VALUE, columnDef: any, node: TreeNode<Option<OPTION_DISPLAY_VALUE>>) => {
                    if (value && node.getData().displayValue) {
                        this.optionDisplayValueViewer.setObject(value);
                        return this.optionDisplayValueViewer.toString();
                    }
                    return '';
                };

            return [
                new api.ui.grid.GridColumnBuilder().setId('option').setName('Options').setField('displayValue').setFormatter(
                    columnFormatter).build()
            ];
        }

        getElement(): Element {
            return this.optionsTreeGrid;
        }

        getGrid(): api.ui.grid.Grid<TreeNode<Option<OPTION_DISPLAY_VALUE>>> {
            return this.optionsTreeGrid.getGrid();
        }

        protected getGridData(): api.ui.grid.DataView<TreeNode<Option<OPTION_DISPLAY_VALUE>>> {
            return this.optionsTreeGrid.getGrid().getDataView();
        }

        getOptionByRow(rowIndex: number): Option<OPTION_DISPLAY_VALUE> {
            const item = this.getGridData().getItem(rowIndex);
            return item ? item.getData() : null;
        }

        getOptionByValue(value: string): Option<OPTION_DISPLAY_VALUE> {
            const item = this.getGridData().getItemById(value);
            return item ? item.getData() : null;
        }
    }
}
