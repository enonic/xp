module api.ui.grid {

    export class GridOptionsBuilder<T extends Slick.SlickData> {

        asyncEditorLoading: boolean;

        asyncEditorLoadDelay: number;

        asyncPostRenderDelay: number;

        autoEdit: boolean;

        autoHeight: boolean;

        cellFlashingCssClass: string;

        cellHighlightCssClass: string;

        dataItemColumnValueExtractor: any;

        defaultColumnWidth: number;

        defaultFormatter: Slick.Formatter<T>;

        editable: boolean;

        editCommandHandler: any;

        editorFactory: Slick.EditorFactory;

        editorLock: Slick.EditorLock<T>;

        enableAddRow: boolean;

        enableAsyncPostRender: boolean;

        enableCellRangeSelection: any;

        enableCellNavigation: boolean;

        enableColumnReorder: boolean;

        enableRowReordering: any;

        enableTextSelectionOnCells: boolean;

        explicitInitialization: boolean;

        forceFitColumns: boolean;

        forceSyncScrolling: boolean;

        formatterFactory: Slick.FormatterFactory<T>;

        fullWidthRows: boolean;

        headerRowHeight: number;

        leaveSpaceForNewRows: boolean;

        multiColumnSort: boolean;

        multiSelect: boolean;

        rowHeight: number;

        selectedCellCssClass: string;

        showHeaderRow: boolean;

        syncColumnCellResize: boolean;

        topPanelHeight: number;

        // Additional properties
        hideColumnHeaders: boolean;

        width: number;

        height: number;

        dataIdProperty: string;

        autoRenderGridOnDataChanges: boolean;

        checkableRows: boolean;

        disabledMultipleSelection: boolean;

        dragAndDrop: boolean;

        constructor(source?: GridOptions<T>) {

            if (source) {
                this.asyncEditorLoading = source.isAsyncEditorLoading();
                this.asyncEditorLoadDelay = source.getAsyncEditorLoadDelay();
                this.asyncPostRenderDelay = source.getAsyncPostRenderDelay();
                this.autoEdit = source.isAutoEdit();
                this.autoHeight = source.isAutoHeight();
                this.cellFlashingCssClass = source.getCellFlashingCssClass();
                this.cellHighlightCssClass = source.getCellHighlightCssClass();
                this.dataItemColumnValueExtractor = source.getDataItemColumnValueExtractor();
                this.defaultColumnWidth = source.getDefaultColumnWidth();
                this.defaultFormatter = source.getDefaultFormatter();
                this.editable = source.isEditable();
                this.editCommandHandler = source.getEditCommandHandler();
                this.editorFactory = source.getEditorFactory();
                this.editorLock = source.getEditorLock();
                this.enableAddRow = source.isEnableAddRow();
                this.enableAsyncPostRender = source.isEnableAsyncPostRender();
                this.enableCellRangeSelection = source.getEnableCellRangeSelection();
                this.enableCellNavigation = source.isEnableCellNavigation();
                this.enableColumnReorder = source.isEnableColumnReorder();
                this.enableRowReordering = source.getEnableRowReordering();
                this.enableTextSelectionOnCells = source.isEnableTextSelectionOnCells();
                this.explicitInitialization = source.isExplicitInitialization();
                this.forceFitColumns = source.isForceFitColumns();
                this.forceSyncScrolling = source.isForceSyncScrolling();
                this.formatterFactory = source.getFormatterFactory();
                this.fullWidthRows = source.isFullWidthRows();
                this.headerRowHeight = source.getHeaderRowHeight();
                this.leaveSpaceForNewRows = source.isLeaveSpaceForNewRows();
                this.multiColumnSort = source.isMultiColumnSort();
                this.multiSelect = source.isMultiSelect();
                this.rowHeight = source.getRowHeight();
                this.selectedCellCssClass = source.getSelectedCellCssClass();
                this.showHeaderRow = source.getShowHeaderRow();
                this.syncColumnCellResize = source.isSyncColumnCellResize();
                this.topPanelHeight = source.getTopPanelHeight();
                // Additional properties
                this.hideColumnHeaders = source.isHideColumnHeaders();
                this.width = source.getWidth();
                this.height = source.getHeight();
                this.dataIdProperty = source.getDataIdProperty();
                this.autoRenderGridOnDataChanges = source.isAutoRenderGridOnDataChanges();
                this.checkableRows = source.isCheckableRows();
                this.disabledMultipleSelection = source.isMultipleSelectionDisabled();
                this.dragAndDrop = source.isDragAndDrop();
            }
        }

        setAsyncEditorLoading(asyncEditorLoading: boolean): GridOptionsBuilder<T> {
            this.asyncEditorLoading = asyncEditorLoading;
            return this;
        }

        setAsyncEditorLoadDelay(asyncEditorLoadDelay: number): GridOptionsBuilder<T> {
            this.asyncEditorLoadDelay = asyncEditorLoadDelay;
            return this;
        }

        setAsyncPostRenderDelay(asyncPostRenderDelay: number): GridOptionsBuilder<T> {
            this.asyncPostRenderDelay = asyncPostRenderDelay;
            return this;
        }

        setAutoEdit(autoEdit: boolean): GridOptionsBuilder<T> {
            this.autoEdit = autoEdit;
            return this;
        }

        setAutoHeight(autoHeight: boolean): GridOptionsBuilder<T> {
            this.autoHeight = autoHeight;
            return this;
        }

        setCellFlashingCssClass(cellFlashingCssClass: string): GridOptionsBuilder<T> {
            this.cellFlashingCssClass = cellFlashingCssClass;
            return this;
        }

        setCellHighlightCssClass(cellHighlightCssClass: string): GridOptionsBuilder<T> {
            this.cellHighlightCssClass = cellHighlightCssClass;
            return this;
        }

        setDataItemColumnValueExtractor(dataItemColumnValueExtractor: any): GridOptionsBuilder<T> {
            this.dataItemColumnValueExtractor = dataItemColumnValueExtractor;
            return this;
        }

        setDefaultColumnWidth(defaultColumnWidth: number): GridOptionsBuilder<T> {
            this.defaultColumnWidth = defaultColumnWidth;
            return this;
        }

        setDefaultFormatter(defaultFormatter: Slick.Formatter<T>): GridOptionsBuilder<T> {
            this.defaultFormatter = defaultFormatter;
            return this;
        }

        setEditable(editable: boolean): GridOptionsBuilder<T> {
            this.editable = editable;
            return this;
        }

        setEditCommandHandler(editCommandHandler: any): GridOptionsBuilder<T> {
            this.editCommandHandler = editCommandHandler;
            return this;
        }

        setEditorFactory(editorFactory: Slick.EditorFactory): GridOptionsBuilder<T> {
            this.editorFactory = editorFactory;
            return this;
        }

        setEditorLock(editorLock: Slick.EditorLock<T>): GridOptionsBuilder<T> {
            this.editorLock = editorLock;
            return this;
        }

        setEnableAddRow(enableAddRow: boolean): GridOptionsBuilder<T> {
            this.enableAddRow = enableAddRow;
            return this;
        }

        setEnableAsyncPostRender(enableAsyncPostRender: boolean): GridOptionsBuilder<T> {
            this.enableAsyncPostRender = enableAsyncPostRender;
            return this;
        }

        setEnableCellRangeSelection(enableCellRangeSelection: any): GridOptionsBuilder<T> {
            this.enableCellRangeSelection = enableCellRangeSelection;
            return this;
        }

        setEnableCellNavigation(enableCellNavigation: boolean): GridOptionsBuilder<T> {
            this.enableCellNavigation = enableCellNavigation;
            return this;
        }

        setEnableColumnReorder(enableColumnReorder: boolean): GridOptionsBuilder<T> {
            this.enableColumnReorder = enableColumnReorder;
            return this;
        }

        setEnableRowReordering(enableRowReordering: any): GridOptionsBuilder<T> {
            this.enableRowReordering = enableRowReordering;
            return this;
        }

        setEnableTextSelectionOnCells(enableTextSelectionOnCells: boolean): GridOptionsBuilder<T> {
            this.enableTextSelectionOnCells = enableTextSelectionOnCells;
            return this;
        }

        setExplicitInitialization(explicitInitialization: boolean): GridOptionsBuilder<T> {
            this.explicitInitialization = explicitInitialization;
            return this;
        }

        setForceFitColumns(forceFitColumns: boolean): GridOptionsBuilder<T> {
            this.forceFitColumns = forceFitColumns;
            return this;
        }

        setForceSyncScrolling(forceSyncScrolling: boolean): GridOptionsBuilder<T> {
            this.forceSyncScrolling = forceSyncScrolling;
            return this;
        }

        setFormatterFactory(formatterFactory: Slick.FormatterFactory<T>): GridOptionsBuilder<T> {
            this.formatterFactory = formatterFactory;
            return this;
        }

        setFullWidthRows(fullWidthRows: boolean): GridOptionsBuilder<T> {
            this.fullWidthRows = fullWidthRows;
            return this;
        }

        setHeaderRowHeight(headerRowHeight: number): GridOptionsBuilder<T> {
            this.headerRowHeight = headerRowHeight;
            return this;
        }

        setLeaveSpaceForNewRows(leaveSpaceForNewRows: boolean): GridOptionsBuilder<T> {
            this.leaveSpaceForNewRows = leaveSpaceForNewRows;
            return this;
        }

        setMultiColumnSort(multiColumnSort: boolean): GridOptionsBuilder<T> {
            this.multiColumnSort = multiColumnSort;
            return this;
        }

        setMultiSelect(multiSelect: boolean): GridOptionsBuilder<T> {
            this.multiSelect = multiSelect;
            return this;
        }

        setRowHeight(rowHeight: number): GridOptionsBuilder<T> {
            this.rowHeight = rowHeight;
            return this;
        }

        setSelectedCellCssClass(selectedCellCssClass: string): GridOptionsBuilder<T> {
            this.selectedCellCssClass = selectedCellCssClass;
            return this;
        }

        setShowHeaderRow(showHeaderRow: boolean): GridOptionsBuilder<T> {
            this.showHeaderRow = showHeaderRow;
            return this;
        }

        setSyncColumnCellResize(syncColumnCellResize: boolean): GridOptionsBuilder<T> {
            this.syncColumnCellResize = syncColumnCellResize;
            return this;
        }

        setTopPanelHeight(topPanelHeight: number): GridOptionsBuilder<T> {
            this.topPanelHeight = topPanelHeight;
            return this;
        }

        setHideColumnHeaders(hideColumnHeaders: boolean): GridOptionsBuilder<T> {
            this.hideColumnHeaders = hideColumnHeaders;
            return this;
        }

        setWidth(width: number): GridOptionsBuilder<T> {
            this.width = width;
            return this;
        }

        setHeight(height: number): GridOptionsBuilder<T> {
            this.height = height;
            return this;
        }

        setDataIdProperty(dataIdProperty: string): GridOptionsBuilder<T> {
            this.dataIdProperty = dataIdProperty;
            return this;
        }

        setAutoRenderGridOnDataChanges(autoRenderGridOnDataChanges: boolean): GridOptionsBuilder<T> {
            this.autoRenderGridOnDataChanges = autoRenderGridOnDataChanges;
            return this;
        }

        setCheckableRows(checkableRows: boolean): GridOptionsBuilder<T> {
            this.checkableRows = checkableRows;
            return this;
        }

        disableMultipleSelection(disabledMultipleSelection: boolean): GridOptionsBuilder<T> {
            this.disabledMultipleSelection = disabledMultipleSelection;
            return this;
        }

        setDragAndDrop(dragAndDrop: boolean): GridOptionsBuilder<T> {
            this.dragAndDrop = dragAndDrop;
            return this;
        }

        build(): GridOptions<T> {
            return new GridOptions<T>(this);
        }
    }

    export class GridOptions<T extends Slick.SlickData> implements Slick.GridOptions<T> {

        asyncEditorLoading: boolean;

        asyncEditorLoadDelay: number;

        asyncPostRenderDelay: number;

        autoEdit: boolean;

        autoHeight: boolean;

        cellFlashingCssClass: string;

        cellHighlightCssClass: string;

        dataItemColumnValueExtractor: any;

        defaultColumnWidth: number;

        defaultFormatter: Slick.Formatter<T>;

        editable: boolean;

        editCommandHandler: any;

        editorFactory: Slick.EditorFactory;

        editorLock: Slick.EditorLock<T>;

        enableAddRow: boolean;

        enableAsyncPostRender: boolean;

        enableCellRangeSelection: any;

        enableCellNavigation: boolean;

        enableColumnReorder: boolean;

        enableRowReordering: any;

        enableTextSelectionOnCells: boolean;

        explicitInitialization: boolean;

        forceFitColumns: boolean;

        forceSyncScrolling: boolean;

        formatterFactory: Slick.FormatterFactory<T>;

        fullWidthRows: boolean;

        headerRowHeight: number;

        leaveSpaceForNewRows: boolean;

        multiColumnSort: boolean;

        multiSelect: boolean;

        rowHeight: number;

        selectedCellCssClass: string;

        showHeaderRow: boolean;

        syncColumnCellResize: boolean;

        topPanelHeight: number;

        // Additional properties
        hideColumnHeaders: boolean;

        width: number;

        height: number;

        dataIdProperty: string;

        autoRenderGridOnDataChanges: boolean;

        checkableRows: boolean;

        disabledMultipleSelection: boolean;

        dragAndDrop: boolean;

        constructor(builder: GridOptionsBuilder<T>) {
            this.asyncEditorLoading = builder.asyncEditorLoading;
            this.asyncEditorLoadDelay = builder.asyncEditorLoadDelay;
            this.asyncPostRenderDelay = builder.asyncPostRenderDelay;
            this.autoEdit = builder.autoEdit;
            this.autoHeight = builder.autoHeight;
            this.cellFlashingCssClass = builder.cellFlashingCssClass;
            this.cellHighlightCssClass = builder.cellHighlightCssClass;
            this.dataItemColumnValueExtractor = builder.dataItemColumnValueExtractor;
            this.defaultColumnWidth = builder.defaultColumnWidth;
            this.defaultFormatter = builder.defaultFormatter;
            this.editable = builder.editable;
            this.editCommandHandler = builder.editCommandHandler;
            this.editorFactory = builder.editorFactory;
            this.editorLock = builder.editorLock;
            this.enableAddRow = builder.enableAddRow;
            this.enableAsyncPostRender = builder.enableAsyncPostRender;
            this.enableCellRangeSelection = builder.enableCellRangeSelection;
            this.enableCellNavigation = builder.enableCellNavigation;
            this.enableColumnReorder = builder.enableColumnReorder;
            this.enableRowReordering = builder.enableRowReordering;
            this.enableTextSelectionOnCells = builder.enableTextSelectionOnCells;
            this.explicitInitialization = builder.explicitInitialization;
            this.forceFitColumns = builder.forceFitColumns;
            this.forceSyncScrolling = builder.forceSyncScrolling;
            this.formatterFactory = builder.formatterFactory;
            this.fullWidthRows = builder.fullWidthRows;
            this.headerRowHeight = builder.headerRowHeight;
            this.leaveSpaceForNewRows = builder.leaveSpaceForNewRows;
            this.multiColumnSort = builder.multiColumnSort;
            this.multiSelect = builder.multiSelect;
            this.rowHeight = builder.rowHeight;
            this.selectedCellCssClass = builder.selectedCellCssClass;
            this.showHeaderRow = builder.showHeaderRow;
            this.syncColumnCellResize = builder.syncColumnCellResize;
            this.topPanelHeight = builder.topPanelHeight;

            this.hideColumnHeaders = builder.hideColumnHeaders;
            this.width = builder.width;
            this.height = builder.height;
            this.dataIdProperty = builder.dataIdProperty;
            this.autoRenderGridOnDataChanges = builder.autoRenderGridOnDataChanges;
            this.checkableRows = builder.checkableRows;
            this.disabledMultipleSelection = builder.disabledMultipleSelection;
            this.dragAndDrop = builder.dragAndDrop;
        }

        isAsyncEditorLoading(): boolean {
            return this.asyncEditorLoading;
        }

        getAsyncEditorLoadDelay(): number {
            return this.asyncEditorLoadDelay;
        }

        getAsyncPostRenderDelay(): number {
            return this.asyncPostRenderDelay;
        }

        isAutoEdit(): boolean {
            return this.autoEdit;
        }

        isAutoHeight(): boolean {
            return this.autoHeight;
        }

        getCellFlashingCssClass(): string {
            return this.cellFlashingCssClass;
        }

        getCellHighlightCssClass(): string {
            return this.cellHighlightCssClass;
        }

        getDataItemColumnValueExtractor(): any {
            return this.dataItemColumnValueExtractor;
        }

        getDefaultColumnWidth(): number {
            return this.defaultColumnWidth;
        }

        getDefaultFormatter(): Slick.Formatter<T> {
            return this.defaultFormatter;
        }

        isEditable(): boolean {
            return this.editable;
        }

        getEditCommandHandler(): any {
            return this.editCommandHandler;
        }

        getEditorFactory(): Slick.EditorFactory {
            return this.editorFactory;
        }

        getEditorLock(): Slick.EditorLock<T> {
            return this.editorLock;
        }

        isEnableAddRow(): boolean {
            return this.enableAddRow;
        }

        isEnableAsyncPostRender(): boolean {
            return this.enableAsyncPostRender;
        }

        getEnableCellRangeSelection(): any {
            return this.enableCellRangeSelection;
        }

        isEnableCellNavigation(): boolean {
            return this.enableCellNavigation;
        }

        isEnableColumnReorder(): boolean {
            return this.enableColumnReorder;
        }

        getEnableRowReordering(): any {
            return this.enableRowReordering;
        }

        isEnableTextSelectionOnCells(): boolean {
            return this.enableTextSelectionOnCells;
        }

        isExplicitInitialization(): boolean {
            return this.explicitInitialization;
        }

        isForceFitColumns(): boolean {
            return this.forceFitColumns;
        }

        isForceSyncScrolling(): boolean {
            return this.forceSyncScrolling;
        }

        getFormatterFactory(): Slick.FormatterFactory<T> {
            return this.formatterFactory;
        }

        isFullWidthRows(): boolean {
            return this.fullWidthRows;
        }

        getHeaderRowHeight(): number {
            return this.headerRowHeight;
        }

        isLeaveSpaceForNewRows(): boolean {
            return this.leaveSpaceForNewRows;
        }

        isMultiColumnSort(): boolean {
            return this.multiColumnSort;
        }

        isMultiSelect(): boolean {
            return this.multiSelect;
        }

        getRowHeight(): number {
            return this.rowHeight;
        }

        getSelectedCellCssClass(): string {
            return this.selectedCellCssClass;
        }

        getShowHeaderRow(): boolean {
            return this.showHeaderRow;
        }

        isSyncColumnCellResize(): boolean {
            return this.syncColumnCellResize;
        }

        getTopPanelHeight(): number {
            return this.topPanelHeight;
        }

        isHideColumnHeaders(): boolean {
            return this.hideColumnHeaders;
        }

        getWidth(): number {
            return this.width;
        }

        getHeight(): number {
            return this.height;
        }

        getDataIdProperty(): string {
            return this.dataIdProperty;
        }

        isAutoRenderGridOnDataChanges(): boolean {
            return this.autoRenderGridOnDataChanges;
        }

        isCheckableRows(): boolean {
            return this.checkableRows;
        }

        isMultipleSelectionDisabled(): boolean {
            return this.disabledMultipleSelection;
        }

        isDragAndDrop(): boolean {
            return this.dragAndDrop;
        }

        setAsyncEditorLoading(asyncEditorLoading: boolean): GridOptions<T> {
            this.asyncEditorLoading = asyncEditorLoading;
            return this;
        }

        setAsyncEditorLoadDelay(asyncEditorLoadDelay: number): GridOptions<T> {
            this.asyncEditorLoadDelay = asyncEditorLoadDelay;
            return this;
        }

        setAsyncPostRenderDelay(asyncPostRenderDelay: number): GridOptions<T> {
            this.asyncPostRenderDelay = asyncPostRenderDelay;
            return this;
        }

        setAutoEdit(autoEdit: boolean): GridOptions<T> {
            this.autoEdit = autoEdit;
            return this;
        }

        setAutoHeight(autoHeight: boolean): GridOptions<T> {
            this.autoHeight = autoHeight;
            return this;
        }

        setCellFlashingCssClass(cellFlashingCssClass: string): GridOptions<T> {
            this.cellFlashingCssClass = cellFlashingCssClass;
            return this;
        }

        setCellHighlightCssClass(cellHighlightCssClass: string): GridOptions<T> {
            this.cellHighlightCssClass = cellHighlightCssClass;
            return this;
        }

        setDataItemColumnValueExtractor(dataItemColumnValueExtractor: any): GridOptions<T> {
            this.dataItemColumnValueExtractor = dataItemColumnValueExtractor;
            return this;
        }

        setDefaultColumnWidth(defaultColumnWidth: number): GridOptions<T> {
            this.defaultColumnWidth = defaultColumnWidth;
            return this;
        }

        setDefaultFormatter(defaultFormatter: Slick.Formatter<T>): GridOptions<T> {
            this.defaultFormatter = defaultFormatter;
            return this;
        }

        setEditable(editable: boolean): GridOptions<T> {
            this.editable = editable;
            return this;
        }

        setEditCommandHandler(editCommandHandler: any): GridOptions<T> {
            this.editCommandHandler = editCommandHandler;
            return this;
        }

        setEditorFactory(editorFactory: Slick.EditorFactory): GridOptions<T> {
            this.editorFactory = editorFactory;
            return this;
        }

        setEditorLock(editorLock: Slick.EditorLock<T>): GridOptions<T> {
            this.editorLock = editorLock;
            return this;
        }

        setEnableAddRow(enableAddRow: boolean): GridOptions<T> {
            this.enableAddRow = enableAddRow;
            return this;
        }

        setEnableAsyncPostRender(enableAsyncPostRender: boolean): GridOptions<T> {
            this.enableAsyncPostRender = enableAsyncPostRender;
            return this;
        }

        setEnableCellRangeSelection(enableCellRangeSelection: any): GridOptions<T> {
            this.enableCellRangeSelection = enableCellRangeSelection;
            return this;
        }

        setEnableCellNavigation(enableCellNavigation: boolean): GridOptions<T> {
            this.enableCellNavigation = enableCellNavigation;
            return this;
        }

        setEnableColumnReorder(enableColumnReorder: boolean): GridOptions<T> {
            this.enableColumnReorder = enableColumnReorder;
            return this;
        }

        setEnableRowReordering(enableRowReordering: any): GridOptions<T> {
            this.enableRowReordering = enableRowReordering;
            return this;
        }

        setEnableTextSelectionOnCells(enableTextSelectionOnCells: boolean): GridOptions<T> {
            this.enableTextSelectionOnCells = enableTextSelectionOnCells;
            return this;
        }

        setExplicitInitialization(explicitInitialization: boolean): GridOptions<T> {
            this.explicitInitialization = explicitInitialization;
            return this;
        }

        setForceFitColumns(forceFitColumns: boolean): GridOptions<T> {
            this.forceFitColumns = forceFitColumns;
            return this;
        }

        setForceSyncScrolling(forceSyncScrolling: boolean): GridOptions<T> {
            this.forceSyncScrolling = forceSyncScrolling;
            return this;
        }

        setFormatterFactory(formatterFactory: Slick.FormatterFactory<T>): GridOptions<T> {
            this.formatterFactory = formatterFactory;
            return this;
        }

        setFullWidthRows(fullWidthRows: boolean): GridOptions<T> {
            this.fullWidthRows = fullWidthRows;
            return this;
        }

        setHeaderRowHeight(headerRowHeight: number): GridOptions<T> {
            this.headerRowHeight = headerRowHeight;
            return this;
        }

        setLeaveSpaceForNewRows(leaveSpaceForNewRows: boolean): GridOptions<T> {
            this.leaveSpaceForNewRows = leaveSpaceForNewRows;
            return this;
        }

        setMultiColumnSort(multiColumnSort: boolean): GridOptions<T> {
            this.multiColumnSort = multiColumnSort;
            return this;
        }

        setMultiSelect(multiSelect: boolean): GridOptions<T> {
            this.multiSelect = multiSelect;
            return this;
        }

        setRowHeight(rowHeight: number): GridOptions<T> {
            this.rowHeight = rowHeight;
            return this;
        }

        setSelectedCellCssClass(selectedCellCssClass: string): GridOptions<T> {
            this.selectedCellCssClass = selectedCellCssClass;
            return this;
        }

        setShowHeaderRow(showHeaderRow: boolean): GridOptions<T> {
            this.showHeaderRow = showHeaderRow;
            return this;
        }

        setSyncColumnCellResize(syncColumnCellResize: boolean): GridOptions<T> {
            this.syncColumnCellResize = syncColumnCellResize;
            return this;
        }

        setTopPanelHeight(topPanelHeight: number): GridOptions<T> {
            this.topPanelHeight = topPanelHeight;
            return this;
        }

        setHideColumnHeaders(hideColumnHeaders: boolean): GridOptions<T> {
            this.hideColumnHeaders = hideColumnHeaders;
            return this;
        }

        setWidth(width: number): GridOptions<T> {
            this.width = width;
            return this;
        }

        setHeight(height: number): GridOptions<T> {
            this.height = height;
            return this;
        }

        setDataIdProperty(dataIdProperty: string): GridOptions<T> {
            this.dataIdProperty = dataIdProperty;
            return this;
        }

        setAutoRenderGridOnDataChanges(autoRenderGridOnDataChanges: boolean): GridOptions<T> {
            this.autoRenderGridOnDataChanges = autoRenderGridOnDataChanges;
            return this;
        }

        setCheckableRows(checkableRows: boolean): GridOptions<T> {
            this.checkableRows = checkableRows;
            return this;
        }

        disableMultipleSelection(disabledMultipleSelection: boolean): GridOptions<T> {
            this.disabledMultipleSelection = disabledMultipleSelection;
            return this;
        }

        setDragAndDrop(dragAndDrop: boolean): GridOptions<T> {
            this.dragAndDrop = dragAndDrop;
            return this;
        }
    }
}