module api.ui.selector {

    import Viewer = api.ui.Viewer;

    export interface DropdownGridConfig<OPTION_DISPLAY_VALUE> {

        maxHeight?: number;

        width: number;

        optionDisplayValueViewer?: Viewer<OPTION_DISPLAY_VALUE>;

        filter: (item: Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        dataIdProperty?:string;

        multipleSelections?: boolean;

        isDropdownGrid?: boolean;

        optionDataHelper?: OptionDataHelper<OPTION_DISPLAY_VALUE>;

        optionDataLoader?: OptionDataLoader<OPTION_DISPLAY_VALUE>;
    }

    export class DropdownGrid<OPTION_DISPLAY_VALUE> {

        protected maxHeight: number;

        protected customHeight: number;

        protected width: number;

        protected dataIdProperty: string;

        protected optionDisplayValueViewer: Viewer<OPTION_DISPLAY_VALUE>;

        protected filter: (item: Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        protected rowSelectionListeners: {(event: DropdownGridRowSelectedEvent): void}[];

        protected multipleSelectionListeners: {(event: DropdownGridMultipleSelectionEvent): void}[];

        protected multipleSelections: boolean;

        protected config: DropdownGridConfig<OPTION_DISPLAY_VALUE>;

        constructor(config: DropdownGridConfig<OPTION_DISPLAY_VALUE>) {
            this.config = config;
            this.rowSelectionListeners = [];
            this.multipleSelectionListeners = [];
            this.optionDisplayValueViewer = config.optionDisplayValueViewer ?
                new (<any>config.optionDisplayValueViewer['constructor'])() : new DefaultOptionDisplayValueViewer();
            this.filter = config.filter;
            this.dataIdProperty = config.dataIdProperty || 'value';
            this.maxHeight = config.maxHeight;
            this.customHeight = config.maxHeight;
            this.width = config.width;
            this.multipleSelections = config.multipleSelections || false;

            this.initGridAndData();
            this.initCommonGridProps();
            this.initGridEventListeners();
        }

        setReadonlyChecker(checker: (optionToCheck: OPTION_DISPLAY_VALUE) => boolean) {
            return;
        }

        protected initGridAndData() {
            throw new Error('Must be implemented by inheritors');
        }

        reload(): wemQ.Promise<void> {
            return api.util.PromiseHelper.newResolvedVoidPromise();
        }

        getElement(): api.dom.Element {
            throw new Error('Must be implemented by inheritors');
        }

        getGrid(): api.ui.grid.Grid<any> {
            throw new Error('Must be implemented by inheritors');
        }

        protected getGridData(): api.ui.grid.DataView<any> {
            throw new Error('Must be implemented by inheritors');
        }

        private initCommonGridProps() {
            this.getGrid().addClass('options-container');
            this.getGrid().getEl().setPosition('absolute');
            this.getGrid().hide();
            this.getGrid().setSelectionModel(new Slick.RowSelectionModel({selectActiveRow: false}));
        }

        protected initGridEventListeners() {
            // Listen to click in grid and issue selection
            this.getGrid().subscribeOnClick((e, args) => {
                this.notifyRowSelection(args.row);

                e.preventDefault();
                return false;
            });

            this.getGrid().subscribeOnSelectedRowsChanged((e, args) => {
                this.notifyMultipleSelection(args.rows);
            });
        }

        renderGrid() {
            this.getGrid().renderGrid();
        }

        isVisible(): boolean {
            return this.getGrid().isVisible();
        }

        show() {
            this.getGrid().show();
        }

        hide() {
            this.getGrid().hide();
        }

        getSelectedOptionCount(): number {
            return this.getGrid().getSelectedRows().length;
        }

        protected createOptions(): api.ui.grid.GridOptions<any> {

            return new api.ui.grid.GridOptionsBuilder()
                .setWidth(this.width + 'px')
                .setHeight(this.maxHeight + 'px')
                .setHideColumnHeaders(true)
                .setEnableColumnReorder(false)
                .setFullWidthRows(true)
                .setForceFitColumns(true)
                .setRowHeight(this.optionDisplayValueViewer.getPreferredHeight())
                .setCheckableRows(this.multipleSelections)
                .setMultiSelect(this.multipleSelections)
                .setDataIdProperty(this.dataIdProperty)
                .build();
        }

        protected createColumns(): api.ui.grid.GridColumn<any>[] {
            let columnFormatter =
                (row: number, cell: number, value: OPTION_DISPLAY_VALUE, columnDef: any, dataContext: Option<OPTION_DISPLAY_VALUE>) => {
                    this.optionDisplayValueViewer.setObject(value);
                    return this.optionDisplayValueViewer.toString();
                };

            return [
                new api.ui.grid.GridColumnBuilder().setId('option').setName('Options').setField('displayValue').setFormatter(
                    columnFormatter).build()
            ];
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[]) {
            this.getGridData().setItems(options, this.dataIdProperty);
        }

        removeAllOptions() {
            this.getGridData().setItems([]);
        }

        addOption(option: Option<OPTION_DISPLAY_VALUE>) {
            this.getGridData().addItem(option);
        }

        hasOptions(): boolean {
            return this.getGridData().getLength() > 0;
        }

        getOptionCount(): number {
            return this.getGridData().getLength();
        }

        getOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.getGridData().getItems();
        }

        getSelectedOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.getGrid().getSelectedRows().map((row) => {
                return this.getGridData().getItem(row);
            });
        }

        getOptionByValue(value: string): Option<OPTION_DISPLAY_VALUE> {
            return this.getGridData().getItemById(value);
        }

        getOptionByRow(rowIndex: number): Option<OPTION_DISPLAY_VALUE> {
            return this.getGridData().getItem(rowIndex);
        }

        getRowByValue(value: string): number {
            return this.getGridData().getRowById(value);
        }

        setFilterArgs(args: any) {
            this.getGridData().setFilterArgs(args);
            this.getGridData().refresh();
        }

        setTopPx(value: number) {
            this.getGrid().getEl().setTopPx(value);
        }

        setWidthPx(value: number) {
            this.getGrid().getEl().setWidthPx(value);
        }

        adjustGridHeight() {

            let gridEl = this.getGrid().getEl();
            let rowsHeight = this.getOptionCount() * this.optionDisplayValueViewer.getPreferredHeight();

            if (rowsHeight < this.customHeight) {
                let borderWidth = gridEl.getBorderTopWidth() + gridEl.getBorderBottomWidth();
                gridEl.setHeightPx(rowsHeight + borderWidth);
                this.getGrid().getOptions().setAutoHeight(true);
            } else if (gridEl.getHeight() < this.customHeight) {
                gridEl.setHeightPx(this.customHeight);
                this.getGrid().getOptions().setAutoHeight(false);
            } else if (this.customHeight !== this.maxHeight) {
                gridEl.setHeightPx(this.customHeight);
                this.getGrid().getOptions().setAutoHeight(false);
            }

            this.getGrid().resizeCanvas();
        }

        markSelections(selectedOptions: Option<OPTION_DISPLAY_VALUE>[], ignoreEmpty: boolean = false) {

            let stylesHash: Slick.CellCssStylesHash = {};
            let rows: number[] = [];
            selectedOptions.forEach((selectedOption: Option<OPTION_DISPLAY_VALUE>) => {
                let row = this.getGridData().getRowById(selectedOption.value);
                rows.push(row);
                stylesHash[row] = {option: 'selected'};
            });
            this.getGrid().setCellCssStyles('selected', stylesHash);
            if (!(rows.length === 0 && ignoreEmpty)) {
                this.getGrid().setSelectedRows(rows);
            }
        }

        markReadOnly(selectedOptions: Option<OPTION_DISPLAY_VALUE>[]) {

            let stylesHash: Slick.CellCssStylesHash = {};
            selectedOptions.forEach((selectedOption: Option<OPTION_DISPLAY_VALUE>) => {
                if(selectedOption.readOnly) {
                    let row = this.getGridData().getRowById(selectedOption.value);
                    stylesHash[row] = {_checkbox_selector: 'readonly', option: 'readonly'};
                }
            });
            this.getGrid().setCellCssStyles('readonly', stylesHash);
        }

        hasActiveRow(): boolean {
            return !!this.getGrid().getActiveCell();
        }

        getActiveRow(): number {
            const activeCell = this.getGrid().getActiveCell();

            return !activeCell ? -1 : activeCell.row;
        }

        expandActiveRow() {
            this.getGrid().navigateDown();
        }

        collapseActiveRow() {
            this.getGrid().navigateUp();
        }

        navigateToRow(row: number) {
            this.getGrid().setActiveCell(row, 0);
        }

        navigateToNextRow() {
            this.getGrid().navigateDown();
        }

        navigateToPreviousRow() {
            this.getGrid().navigateUp();
        }

        resetActiveSelection() {
            if (this.getGrid().getActiveCell()) {
                this.getGrid().resetActiveCell();
            }
        }

        setCustomHeight(height: number) {
            this.customHeight = Math.min(height, this.maxHeight);
        }

        resetCustomHeight() {
            this.customHeight = this.maxHeight;
        }

        toggleRowSelection(row: number, isMaximumReached: boolean = false) {
            let rows = this.getGrid().getSelectedRows();
            let oldRows = rows.join();
            let index = rows.indexOf(row);

            if (index >= 0) {
                rows.splice(index, 1);
            } else if (!isMaximumReached) {
                rows.push(row);
            }

            // update on changes only
            if (oldRows !== rows.join()) {
                this.getGrid().setSelectedRows(rows);
            }
        }

        navigateToFirstRow() {
            this.navigateToRow(0);
        }

        onRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.rowSelectionListeners.push(listener);
        }

        unRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.rowSelectionListeners.filter((currentListener: (event: DropdownGridRowSelectedEvent) => void) => {
                return listener !== currentListener;
            });
        }

        onClick(callback: (e: MouseEvent, args: any) => void) {
            this.getGrid().subscribeOnClick(callback);
        }

        unClick(callback: (e: MouseEvent, args: any) => void) {
            this.getGrid().unsubscribeOnClick(callback);
        }

        onMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.multipleSelectionListeners.push(listener);
        }

        unMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.multipleSelectionListeners.filter((currentListener: (event: DropdownGridMultipleSelectionEvent) => void) => {
                return listener !== currentListener;
            });
        }

        protected notifyRowSelection(rowSelected: number) {
            const event = new DropdownGridRowSelectedEvent(rowSelected);
            this.rowSelectionListeners.forEach((listener: (event: DropdownGridRowSelectedEvent) => void) => {
                listener(event);
            });
        }

        protected notifyMultipleSelection(rowsSelected: number[]) {
            const event = new DropdownGridMultipleSelectionEvent(rowsSelected);
            this.multipleSelectionListeners.forEach((listener: (event: DropdownGridMultipleSelectionEvent) => void) => {
                listener(event);
            });
        }
    }
}
