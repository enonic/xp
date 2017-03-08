module api.ui.treegrid {

    import Item = api.item.Item;

    import Element = api.dom.Element;
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
    import TreeGridToolbarActions = api.ui.treegrid.actions.TreeGridToolbarActions;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;
    import AppHelper = api.util.AppHelper;

    /*
     * There are several methods that should be overridden:
     * 1. hasChildren(data: DATA)  -- Should be implemented if a grid has a tree structure and supports expand/collapse.
     * 2. fetch(data?: DATA) -- Should fetch full data with a valid hasChildren() value;
     * 3. fetchChildren(parentData?: DATA) -- Should fetch children of a parent data;
     * 4. fetchRoot() -- Fetches root nodes. by default return fetchChildren() with an empty parameter.
     */
    export class TreeGrid<DATA> extends api.ui.panel.Panel {

        public static LEVEL_STEP_INDENT: number = 16;

        private columns: GridColumn<DATA>[] = [];

        private gridOptions: GridOptions<DATA>;

        private grid: Grid<TreeNode<DATA>>;

        private gridData: DataView<TreeNode<DATA>>;

        private root: TreeRoot<DATA>;

        private toolbar: TreeGridToolbar;

        private contextMenu: TreeGridContextMenu;

        private expandAll: boolean;

        private active: boolean;

        private loadedListeners: Function[] = [];

        private contextMenuListeners: Function[] = [];

        private selectionChangeListeners: Function[] = [];

        private highlightingChangeListeners: Function[] = [];

        private dataChangeListeners: {(event: DataChangedEvent<DATA>): void}[] = [];

        private activeChangedListeners: {(active: boolean): void}[] = [];

        private loadBufferSize: number;

        private loading: boolean = false;

        private scrollable: api.dom.Element;

        private quietErrorHandling: boolean;

        private errorPanel: ValidationRecordingViewer;

        private highlightedNode: TreeNode<DATA>;

        protected highlightingEnabled: boolean = true;

        private interval: number;

        constructor(builder: TreeGridBuilder<DATA>) {

            super(builder.getClasses());

            this.expandAll = builder.isExpandAll();
            this.quietErrorHandling = builder.getQuietErrorHandling();

            // root node with undefined item
            this.root = new TreeRoot<DATA>();

            this.gridData = new DataView<TreeNode<DATA>>();
            this.gridData.setFilter((node: TreeNode<DATA>) => {
                return node.isVisible();
            });
            this.gridData.setItemMetadataHandler(this.handleItemMetadata.bind(this));

            this.columns = this.updateColumnsFormatter(builder.getColumns());
            this.gridOptions = builder.getOptions();
            this.grid = new Grid<TreeNode<DATA>>(this.gridData, this.columns, this.gridOptions);

            // Custom row selection required for valid behaviour
            this.grid.setSelectionModel(<Slick.RowSelectionModel<TreeNode<DATA>, any>>new Slick.RowSelectionModel({
                selectActiveRow: false
            }));

            /*
             * Default checkbox plugin should be unselected, because the
             * cell navigation is disabled. Enabling it will break the
             * key custom key navigation. Without it plugin is having
             * some spacebar handling error, due to active cell can't be set.
             */
            this.initSelectorPlugin();

            this.grid.syncGridSelection(false);

            if (builder.getContextMenu()) {
                this.setContextMenu(builder.getContextMenu());
            }

            this.initToolbar(builder.isShowToolbar());

            this.appendChild(this.grid);

            if (this.quietErrorHandling) {
                this.appendChild(this.errorPanel = new api.form.ValidationRecordingViewer());
                this.errorPanel.hide();
            }

            if (builder.isPartialLoadEnabled()) {
                this.loadBufferSize = builder.getLoadBufferSize();
            }

            this.initEventListeners(builder);
        }

        private initSelectorPlugin() {
            let selectorPlugin = this.grid.getCheckboxSelectorPlugin();
            if (selectorPlugin) {
                this.grid.unregisterPlugin(<Slick.Plugin<TreeNode<DATA>>>this.grid.getCheckboxSelectorPlugin());
            }
        }

        private initToolbar(showToolbar: boolean) {
            if (showToolbar) {
                this.toolbar = new TreeGridToolbar(new TreeGridToolbarActions(this), this);
                this.appendChild(this.toolbar);
                // make sure it won't left from the cloned grid
                this.removeClass('no-toolbar');
            } else {
                this.addClass('no-toolbar');
            }
        }

        protected setColumns(columns: GridColumn<TreeNode<DATA>>[], toBegin: boolean = false) {
            this.getGrid().setColumns(columns, toBegin);
            this.highlightCurrentNode();
        }

        public getFirstSelectedOrHighlightedNode(): TreeNode<DATA> {
            return this.highlightedNode ? this.highlightedNode : this.getRoot().getFullSelection()[0];
        }

        private onSelectRange(event: ExtendedKeyboardEvent, navigateFn: Function) {
            if (this.isActive()) {
                let row;
                if (this.highlightedNode) {
                    this.recursivelyExpandHighlightedNode();
                    row = this.getRowIndexByNode(this.highlightedNode);
                    if (!this.grid.isRowSelected(row)) {
                        this.grid.selectRow(row);
                    }
                } else if (this.grid.getSelectedRows().length === 1) {
                    row = this.grid.getSelectedRows()[0];
                }
                this.scrollToRow(navigateFn(row));
            }
            event.preventDefault();
            event.stopImmediatePropagation();
        }

        private initEventListeners(builder: TreeGridBuilder<DATA>) {

            let keyBindings: KeyBinding[] = [];

            this.onClicked(() => {
                this.grid.focus();
            });

            if (builder.isAutoLoad()) {
                this.onAdded(() => {
                    this.reload().then(() => this.grid.resizeCanvas());
                });
            }

            this.bindClickEvents();

            this.grid.onShown(() => {
                this.scrollable = this.queryScrollable();
            });

            this.onShown(() => {
                this.bindKeys(builder, keyBindings);
            });

            this.onRendered(() => {
                this.onRenderedHandler(builder);
            });

            this.onRemoved(() => {
                this.onRemovedHandler(builder, keyBindings);
            });

            this.grid.subscribeOnSelectedRowsChanged((event, rows) => {
                this.notifySelectionChanged(event, rows.rows);
            });

            this.onLoaded(() => this.unmask());
        }

        private onRenderedHandler(builder: TreeGridBuilder<DATA>) {
            this.grid.resizeCanvas();
            if (builder.isPartialLoadEnabled()) {
                if (this.interval) {
                    clearInterval(this.interval);
                }
                this.interval = setInterval(this.postLoad.bind(this), 200);
            }
        }

        private onRemovedHandler(builder: TreeGridBuilder<DATA>, keyBindings: KeyBinding[]) {
            if (builder.isHotkeysEnabled()) {
                KeyBindings.get().unbindKeys(keyBindings);
            }

            if (builder.isPartialLoadEnabled() && this.interval) {
                clearInterval(this.interval);
            }
        };

        private bindClickEvents() {
            let clickHandler = ((event, data) => {
                if (!this.isActive()) {
                    return;
                }

                const elem = new ElementHelper(event.target);

                if (this.contextMenu) {
                    this.contextMenu.hide();
                }

                if (event.shiftKey) {
                    this.onClickWithShift(event, data);
                    return;
                }

                if (event.metaKey || event.ctrlKey) {
                    this.onClickWithCmd(data);
                    return;
                }

                this.setActive(false);

                if (elem.hasClass('expand')) {
                    this.onExpand(elem, data);
                    return;
                }

                if (elem.hasClass('collapse')) {
                    this.onCollapse(elem, data);
                    return;
                }

                this.setActive(true);

                // Checkbox is clicked
                let isCheckboxClicked = elem.hasClass('slick-cell-checkboxsel') || elem.hasAnyParentClass('slick-cell-checkboxsel');

                if (!this.highlightingEnabled || this.gridOptions.isMultipleSelectionDisabled() || isCheckboxClicked) {
                    this.onRowSelected(data);
                    return;
                }

                this.onRowHighlighted(elem, data);

                if (!elem.hasClass('sort-dialog-trigger')) {
                    new TreeGridItemClickedEvent(!!this.getFirstSelectedOrHighlightedNode()).fire();
                }
            });

            this.grid.subscribeOnClick(clickHandler);
        }

        private onClickWithShift(event: any, data: Slick.OnClickEventData) {
            const node = this.gridData.getItem(data.row);
            const thereIsHighlightedNode = !!this.highlightedNode && !this.isNodeHighlighted(node) && this.highlightedNode.isVisible();
            const isMultiSelect = !this.gridOptions.isMultipleSelectionDisabled();

            if (!this.grid.isRowSelected(data.row) && (this.grid.getSelectedRows().length >= 1 || thereIsHighlightedNode)) {
                if (isMultiSelect) {
                    let firstSelectedRow;
                    let highlightFrom;
                    let highlightTo;

                    if (thereIsHighlightedNode) {
                        const highlightedRow = this.getRowIndexByNode(this.highlightedNode);
                        highlightFrom = highlightedRow < data.row ? highlightedRow : data.row;
                        highlightTo = data.row > highlightedRow ? data.row : highlightedRow;
                    } else {
                        firstSelectedRow = this.grid.getSelectedRows()[0];
                        highlightFrom = firstSelectedRow < data.row ? firstSelectedRow : data.row;
                        highlightTo = data.row > firstSelectedRow ? data.row : firstSelectedRow;
                    }

                    this.unhighlightCurrentRow();

                    for (let i = highlightFrom; i <= highlightTo; i++) {
                        if (!this.grid.isRowSelected(i)) {
                            this.grid.toggleRow(i);
                        }
                    }
                    event.stopPropagation();
                    event.preventDefault();
                    return;
                } else {
                    this.deselectAll();
                }
            }
            this.grid.toggleRow(data.row);
        }

        private onClickWithCmd(data: Slick.OnClickEventData) {
            const node = this.gridData.getItem(data.row);
            if (!this.grid.isRowSelected(data.row) && this.highlightedNode !== node) {
                this.unhighlightCurrentRow(true);
            }
            this.grid.toggleRow(data.row);
        }

        private onExpand(elem: ElementHelper, data: Slick.OnClickEventData) {
            const node = this.gridData.getItem(data.row);
            elem.removeClass('expand').addClass('collapse');
            this.expandNode(node).then(() => {
                this.highlightCurrentNode();
            });
        }

        private recursivelyExpandHighlightedNode() {
            if (!this.highlightedNode || this.highlightedNode.isVisible()) {
                return;
            }
            let parent: TreeNode<DATA> = this.highlightedNode.getParent();
            while (!this.highlightedNode.isVisible()) {
                this.expandNode(parent);
                parent = parent.getParent();
            }

        }

        private onCollapse(elem: ElementHelper, data: Slick.OnClickEventData) {
            const node = this.gridData.getItem(data.row);
            elem.removeClass('collapse').addClass('expand');
            this.collapseNode(node);
        }

        private onRowSelected(data: Slick.OnClickEventData) {
            const node = this.gridData.getItem(data.row);

            if (this.gridOptions.isMultipleSelectionDisabled()) {
                this.root.clearStashedSelection();
                this.grid.selectRow(data.row);
                return;
            }

            if (this.grid.getSelectedRows().length > 1) {
                this.unhighlightRows(true);
            } else if (!this.grid.isRowSelected(data.row) && this.highlightedNode !== node) {
                this.unhighlightCurrentRow(true);
            }

            this.grid.toggleRow(data.row);
        }

        private onRowHighlighted(elem: ElementHelper, data: Slick.OnClickEventData) {
            const node = this.gridData.getItem(data.row);
            const clickedRow = wemjq(elem.getHTMLElement()).closest('.slick-row');
            const isRowSelected = this.grid.isRowSelected(data.row);
            const isMultipleRowsSelected = this.grid.getSelectedRows().length > 1;
            const isRowHighlighted = clickedRow.hasClass('selected');

            if (elem.hasClass('sort-dialog-trigger') && (isRowSelected || isRowHighlighted)) {
                if (isMultipleRowsSelected) {
                    this.grid.selectRow(data.row);
                }
                return;
            }

            // Clear selection and highlighting if something was selected or highlighted from before
            if (this.isSelectionNotEmpty() || isRowHighlighted) {
                this.unselectAllRows();
                this.root.clearStashedSelection();
                this.triggerSelectionChangedListeners();
            }

            if (!(isRowHighlighted || isRowSelected)) {
                this.highlightRowByNode(node);
            } else if (isMultipleRowsSelected) {
                this.grid.selectRow(data.row);
            }
        }

        private isSelectionNotEmpty() {
            return this.grid.getSelectedRows().length > 0 || this.root.getStashedSelection().length > 0;
        }

        private bindKeys(builder: TreeGridBuilder<DATA>, keyBindings: KeyBinding[]) {
            this.grid.resizeCanvas();
            if (builder.isHotkeysEnabled()) {

                if (!this.gridOptions.isMultipleSelectionDisabled()) {
                    keyBindings = [
                        new KeyBinding('shift+up', (event: ExtendedKeyboardEvent) => {
                            this.onSelectRange(event, this.grid.addSelectedUp.bind(this.grid));
                        }),
                        new KeyBinding('shift+down', (event: ExtendedKeyboardEvent) => {
                            this.onSelectRange(event, this.grid.addSelectedDown.bind(this.grid));
                        })
                    ];
                }

                keyBindings = keyBindings.concat([
                    new KeyBinding('up', this.onUpKeyPress.bind(this)),
                    new KeyBinding('down', this.onDownKeyPress.bind(this)),
                    new KeyBinding('left', this.onLeftKeyPress.bind(this)),
                    new KeyBinding('right', this.onRightKeyPress.bind(this)),
                    new KeyBinding('mod+a', this.onAwithModKeyPress.bind(this)),
                    new KeyBinding('space', this.onSpaceKeyPress.bind(this)),
                    new KeyBinding('enter', this.onEnterKeyPress.bind(this), KeyBindingAction.KEYUP)
                ]);

                KeyBindings.get().bindKeys(keyBindings);
            }
        }

        private onUpKeyPress() {
            if (this.isActive()) {
                this.recursivelyExpandHighlightedNode();

                if (this.contextMenu) {
                    this.contextMenu.hide();
                }
                if (this.gridOptions.isMultipleSelectionDisabled()) {
                    this.scrollToRow(this.grid.moveSelectedUp());
                } else {
                    this.navigateUp();
                }
            }
        }

        private onDownKeyPress() {
            if (this.isActive()) {
                this.recursivelyExpandHighlightedNode();

                if (this.contextMenu) {
                    this.contextMenu.hide();
                }
                if (this.gridOptions.isMultipleSelectionDisabled()) {
                    this.scrollToRow(this.grid.moveSelectedDown());
                } else {
                    this.navigateDown();
                }
            }
        }

        private onLeftKeyPress() {
            let selected = this.grid.getSelectedRows();
            if (selected.length !== 1 && !this.highlightedNode) {
                return;
            }

            this.recursivelyExpandHighlightedNode();
            if (this.contextMenu) {
                this.contextMenu.hide();
            }
            let node = this.gridData.getItem(selected[0]) || this.highlightedNode;
            if (node && this.isActive()) {
                if (node.isExpanded()) {
                    this.setActive(false);
                    this.collapseNode(node);
                    if (!selected[0]) {
                        this.highlightRowByNode(node);
                    }
                } else if (node.getParent() !== this.root.getCurrentRoot()) {
                    node = node.getParent();
                    this.setActive(false);
                    let row = this.getRowIndexByNode(node);
                    this.collapseNode(node);
                    if (selected[0]) {
                        this.unselectAllRows();
                        this.grid.selectRow(row, true);
                    } else {
                        this.highlightRowByNode(node);
                    }
                }
            }
        }

        private onRightKeyPress() {
            let selected = this.grid.getSelectedRows();
            if (selected.length !== 1 && !this.highlightedNode) {
                return;
            }

            this.recursivelyExpandHighlightedNode();
            if (this.contextMenu) {
                this.contextMenu.hide();
            }
            let node = this.gridData.getItem(selected[0]) || this.highlightedNode;
            if (node && this.hasChildren(node.getData())
                && !node.isExpanded() && this.isActive()) {

                this.setActive(false);
                this.invalidate();
                this.expandNode(node).then(() => {
                    if (!selected[0]) {
                        this.highlightCurrentNode();
                    }
                });
            }
        }

        expandRow(row: number) {
            let node = this.gridData.getItem(row);
            this.expandNode(node);
        }

        collapseRow(row: number) {
            let node = this.gridData.getItem(row);
            this.collapseNode(node);
        }

        private onAwithModKeyPress = (event: ExtendedKeyboardEvent) => {
            let selected = this.grid.getSelectedRows();
            if (selected.length === this.gridData.getLength()) {
                this.deselectAll();
            } else {
                this.selectAll();
            }

            event.preventDefault();
            event.stopImmediatePropagation();
        }

        private onSpaceKeyPress() {
            if (this.highlightedNode) {
                this.recursivelyExpandHighlightedNode();
                let row = this.getRowIndexByNode(this.highlightedNode);
                this.grid.toggleRow(row);
            } else if (this.grid.getSelectedRows().length > 0) {
                this.deselectAll();
            }
        }

        private onEnterKeyPress() {
            if (this.highlightedNode) {
                this.editItem(this.highlightedNode);
            }
        }

        protected editItem(node: TreeNode<DATA>) {
            return;
        }

        public setContextMenu(contextMenu: TreeGridContextMenu) {
            this.contextMenu = contextMenu;
            this.grid.subscribeOnContextMenu((event) => {
                event.preventDefault();
                this.setActive(false);
                let cell = this.grid.getCellFromEvent(event);
                if (!this.grid.isRowSelected(cell.row)) {
                    this.highlightRowByNode(this.gridData.getItem(cell.row));
                }
                this.contextMenu.showAt(event.pageX, event.pageY);
                this.notifyContextMenuShown(event.pageX, event.pageY);
                this.setActive(true);
            });

        }

        private navigateUp() {
            let selectedCount = this.grid.getSelectedRows().length;
            if (!this.highlightedNode && selectedCount === 0) {
                return;
            }

            let selectedIndex = this.highlightedNode ?
                                this.getRowIndexByNode(this.highlightedNode) : this.grid.getSelectedRows()[selectedCount - 1];

            if (selectedIndex > 0) {
                this.unselectAllRows();
                selectedIndex--;
                this.highlightRowByNode(this.gridData.getItem(selectedIndex));
                this.scrollToRow(selectedIndex, true);
            }
        }

        private navigateDown() {
            let selectedIndex = this.highlightedNode ? this.getRowIndexByNode(this.highlightedNode) : -1;
            if (this.grid.getSelectedRows().length > 0) {
                selectedIndex = this.grid.getSelectedRows()[0];
            }

            if (this.gridData.getLength() > 0 && selectedIndex < this.gridData.getLength() - 1) {
                this.unselectAllRows();
                selectedIndex++;
                this.highlightRowByNode(this.gridData.getItem(selectedIndex));
                this.scrollToRow(selectedIndex, true);
            }
        }

        private getRowIndexByNode(node: TreeNode<DATA>): number {
            let rowIndex = this.gridData.getRowById(node.getId());
            if (isNaN(rowIndex)) {
                // When search is applied content nodes get different Ids,
                // so we should try to search by dataId and not by nodeId

                let nodesByDataId = this.grid.getDataView().getItems().filter(item => item.getDataId() == node.getDataId());
                if (!nodesByDataId || nodesByDataId.length === 0 || !nodesByDataId[0].isVisible()) {
                    return null;
                }

                rowIndex = this.grid.getDataView().getItems().map(item => item.getDataId()).indexOf(node.getDataId());
            }

            return rowIndex;
        }

        private getRowByNode(node: TreeNode<DATA>): JQuery {
            let rowIndex = this.getRowIndexByNode(node);
            let cell = this.grid.getCellNode(rowIndex, 0);

            return wemjq(cell).closest('.slick-row');
        }

        protected highlightCurrentNode(silent: boolean = false) {
            if (!this.highlightedNode) {
                return;
            }

            this.highlightRowByNode(this.highlightedNode);
            this.notifyHighlightingChanged();
        }

        private highlightRowByNode(node: TreeNode<DATA>) {
            if (!this.highlightingEnabled) {
                return;
            }
            if (!this.highlightedNode || this.highlightedNode !== node) {
                this.unhighlightCurrentRow();
                this.highlightedNode = node;
                this.notifyHighlightingChanged();
            }

            let row = this.getRowByNode(node);
            if (!!row) {
                row.addClass('selected');
            }
        }

        private unhighlightCurrentRow(skipEvent: boolean = false) {
            if (!this.highlightedNode) {
                return;
            }
            let row = this.getRowByNode(this.highlightedNode);
            this.unhighlightRow(row, skipEvent);
        }

        private unhighlightRow(row: JQuery, skipEvent: boolean = false) {
            this.removeHighlighting(skipEvent);
            if (!row) {
                return;
            }
            row.removeClass('selected');
        }

        removeHighlighting(skipEvent: boolean = false) {
            this.highlightedNode = null;

            if (!skipEvent) {
                this.notifyHighlightingChanged();
            }
        }

        private unhighlightRows(skipEvent: boolean = false) {
            if (!this.highlightedNode) {
                return;
            }

            wemjq(this.grid.getHTMLElement()).find('.slick-row.selected').removeClass('selected');
            this.removeHighlighting(skipEvent);
        }

        private unselectAllRows() {
            this.unhighlightRows();
            if (this.grid.getSelectedRows().length > 0) {
                this.grid.clearSelection();
            }
        }

        public isInRenderingView(): boolean {
            // TreeGrid in visible tab or TreeGrid is active
            return this.isVisible() && this.isActive();
        }

        private updateColumnsFormatter(columns: GridColumn<TreeNode<DATA>>[]) {
            if (columns.length > 0) {
                let formatter = columns[0].getFormatter();
                let toggleFormatter = (row: number, cell: number, value: any, columnDef: any, node: TreeNode<DATA>) => {
                    let toggleSpan = new api.dom.SpanEl('toggle icon');
                    if (this.hasChildren(node.getData())) {
                        let toggleClass = node.isExpanded() ? 'collapse' : 'expand';
                        toggleSpan.addClass(toggleClass);
                    }
                    toggleSpan.getEl().setMarginLeft(TreeGrid.LEVEL_STEP_INDENT * (node.calcLevel() - 1) + 'px');

                    return toggleSpan.toString() + formatter(row, cell, value, columnDef, node);
                };

                columns[0].setFormatter(toggleFormatter);
            }

            return columns;
        }

        isEmptyNode(node: TreeNode<DATA>): boolean {
            return false;
        }

        getEmptyNodesCount(): number {

            let viewportRange = this.grid.getViewport();
            let lastIndex = this.gridData.getItems().length - 1;
            // first and last rows, that are visible in grid
            let firstVisible = viewportRange.top;
            // interval borders to search for the empty node
            let from = firstVisible;
            let emptyNodesCount = 0;

            for (let i = from; i <= lastIndex; i++) {
                if (!!this.gridData.getItem(i) && this.gridData.getItem(i).getDataId() === '') {
                    emptyNodesCount++;
                }
            }

            return emptyNodesCount;

        }

        mask() {
            this.grid.mask();
        }

        unmask() {
            this.grid.unmask();
        }

        getGrid(): Grid<TreeNode<DATA>> {
            return this.grid;
        }

        getOptions(): GridOptions<DATA> {
            return this.gridOptions;
        }

        getColumns(): GridColumn<TreeNode<DATA>>[] {
            return this.grid.getColumns();
        }

        getContextMenu(): TreeGridContextMenu {
            return this.contextMenu;
        }

        getRoot(): TreeRoot<DATA> {
            return this.root;
        }

        isNewlySelected(): boolean {
            return this.getRoot().isNewlySelected();
        }

        isActive(): boolean {
            return this.active;
        }

        setActive(active: boolean = true) {
            if (this.active !== active) {
                this.active = active;
                this.notifyActiveChanged(active);
            }
        }

        onActiveChanged(listener: (active: boolean) => void) {
            this.activeChangedListeners.push(listener);
        }

        unActiveChanged(listener: (active: boolean) => void) {
            this.activeChangedListeners = this.activeChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyActiveChanged(active: boolean) {
            this.activeChangedListeners.forEach((listener) => {
                listener(active);
            });
        }

        getToolbar(): TreeGridToolbar {
            return this.toolbar;
        }

        hasToolbar(): boolean {
            return !!this.toolbar;
        }

        scrollToRow(row: number, skipSelectionCheck: boolean = false) {
            if (!this.scrollable) {
                // not present until shown
                return;
            }
            let scrollEl = this.scrollable.getEl();

            if (row > -1 && (skipSelectionCheck || this.grid.getSelectedRows().length > 0)) {
                if (scrollEl.getScrollTop() > row * 45) {
                    scrollEl.setScrollTop(row * 45);
                } else if (scrollEl.getScrollTop() + scrollEl.getHeight() < (row + 1) * 45) {
                    scrollEl.setScrollTop((row + 1) * 45 - scrollEl.getHeight());
                }
            }
        }

        queryScrollable(): api.dom.Element {
            let gridClasses = (' ' + this.grid.getEl().getClass()).replace(/\s/g, '.');
            let viewport = Element.fromString('.tree-grid ' + gridClasses + ' .slick-viewport', false);
            return viewport;
        }

        private loadEmptyNode(node: TreeNode<DATA>) {
            if (!this.getDataId(node.getData())) {
                this.fetchChildren(node.getParent()).then((dataList: DATA[]) => {
                    let oldChildren = node.getParent().getChildren();
                    // Ensure to remove empty node from the end if present
                    if (oldChildren.length > 0 && oldChildren[oldChildren.length - 1].getDataId() === '') {
                        oldChildren.pop();
                    }
                    let fetchedChildren = this.dataToTreeNodes(dataList, node.getParent());
                    let needToCheckFetchedChildren = this.areAllOldChildrenSelected(oldChildren);
                    let newChildren = oldChildren.concat(fetchedChildren.slice(oldChildren.length));
                    node.getParent().setChildren(newChildren);
                    this.initData(this.root.getCurrentRoot().treeToList());
                    if (needToCheckFetchedChildren) {
                        this.select(fetchedChildren);
                    } else {
                        this.triggerSelectionChangedListeners();
                    }
                }).catch((reason: any) => {
                    this.handleError(reason);
                }).then(() => {
                    this.notifyLoaded();
                    this.loading = false;
                });
            }
        }

        private select(fetchedChildren: TreeNode<DATA>[]) {
            let rowsToSelect: number[] = [];
            fetchedChildren.forEach((node: TreeNode<DATA>) => {
                let row = this.getRowIndexByNode(node);
                if (row) {
                    rowsToSelect.push(row);
                }
            });
            this.grid.addSelectedRows(rowsToSelect);
        }

        private areAllOldChildrenSelected(oldChildren: TreeNode<DATA>[]): boolean {
            if (oldChildren && oldChildren.length > 0) {
                return oldChildren.every(node =>
                    this.grid.isRowSelected(this.getRowIndexByNode(node))
                );
            } else {
                return false;
            }
        }

        private postLoad() {
            // Skip if not visible or active (is loading something)
            const disabled = !this.isInRenderingView() || this.loading;

            if (disabled) {
                return;
            }

            const viewportRange = this.grid.getViewport();
            const lastIndex = this.gridData.getItems().length - 1;
            // first and last rows, that are visible in grid
            const firstVisible = viewportRange.top;
            const lastVisible = Math.min(viewportRange.bottom, lastIndex);
            // interval borders to search for the empty node
            const from = firstVisible;
            const to = Math.min(lastVisible + this.loadBufferSize, lastIndex);

            for (let i = from; i <= to; i++) {
                if (this.gridData.getItem(i) && this.gridData.getItem(i).getDataId() === '') {
                    this.loading = true;
                    this.loadEmptyNode(this.gridData.getItem(i));
                    break;
                }
            }
        }

        /**
         * Used to determine if a data have child nodes.
         * Must be overridden for the grids with a tree structure.
         */
        hasChildren(data: DATA): boolean {
            return false;
        }

        /**
         * Used to get the data identifier or key.
         * Must be overridden.
         */
        getDataId(data: DATA): string {
            throw new Error('Must be implemented by inheritors');
        }

        isEmpty(): boolean {
            return this.getGrid().getDataLength() == 0;
        }

        /**
         * Fetches a single element.
         * Can be used to update/add a single node without
         * retrieving a a full data, or for the purpose of the
         * infinite scroll.
         */
        fetch(node: TreeNode<DATA>, dataId?: string): wemQ.Promise<DATA> {
            let deferred = wemQ.defer<DATA>();
            // Empty logic
            deferred.resolve(null);
            return deferred.promise;
        }

        /**
         * Used as a default children fetcher.
         * Must be overridden to use predefined root nodes.
         */
        fetchChildren(parentNode?: TreeNode<DATA>): wemQ.Promise<DATA[]> {
            let deferred = wemQ.defer<DATA[]>();
            // Empty logic
            deferred.resolve([]);
            return deferred.promise;
        }

        /**
         * Used as a default root fetcher.
         * Can be overridden to use predefined root nodes.
         * By default, return empty fetchChildren request.
         */
        fetchRoot(): wemQ.Promise<DATA[]> {
            return this.fetchChildren();
        }

        private fetchData(parentNode?: TreeNode<DATA>): wemQ.Promise<DATA[]> {
            return parentNode ? this.fetchChildren(parentNode) : this.fetchRoot();
        }

        dataToTreeNode(data: DATA, parent: TreeNode<DATA>): TreeNode<DATA> {
            return new TreeNodeBuilder<DATA>().setData(data, this.getDataId(data)).setExpanded(this.expandAll).setParent(parent).build();
        }

        dataToTreeNodes(dataArray: DATA[], parent: TreeNode<DATA>): TreeNode<DATA>[] {
            let nodes: TreeNode<DATA>[] = [];
            dataArray.forEach((data) => {
                nodes.push(this.dataToTreeNode(data, parent));
            });
            return nodes;
        }

        filter(dataList: DATA[]) {
            this.setActive(false);
            this.root.setFiltered(true);
            this.root.getCurrentRoot().setChildren(this.dataToTreeNodes(dataList, this.root.getCurrentRoot()));
            this.initData(this.root.getCurrentRoot().treeToList());
            this.invalidate();
            this.setActive(true);
        }

        resetFilter() {
            this.setActive(false);

            if (this.root.isFiltered()) {
                this.root.setFiltered(false);
                this.initData(this.root.getCurrentRoot().treeToList());
                this.invalidate();
                this.setActive(true);
                this.notifyLoaded();
            } else {
                // replace with refresh in future
                this.reload();
            }
        }

        selectNode(dataId: string) {
            let root = this.root.getCurrentRoot();
            let node = root.findNode(dataId);

            if (node) {
                this.unhighlightCurrentRow(true);

                let row = this.getRowIndexByNode(node);
                this.grid.selectRow(row);
            }
        }

        refreshNodeById(dataId: string) {
            let root = this.root.getCurrentRoot();
            let node = root.findNode(dataId);

            if (node) {
                this.refreshNode(node);
            }
        }

        selectAll() {
            this.unhighlightCurrentRow(true);
            let rows = [];
            for (let i = 0; i < this.gridData.getLength(); i++) {
                if (!api.util.StringHelper.isEmpty(this.gridData.getItem(i).getDataId())) {
                    rows.push(i);
                }
            }
            this.grid.setSelectedRows(rows);
        }

        deselectAll() {
            this.unhighlightCurrentRow(true);
            this.grid.clearSelection();
        }

        deselectNodes(dataIds: string[]) {
            let oldSelected = this.root.getFullSelection();
            let newSelected = [];
            let newSelectedRows = [];

            for (let i = 0; i < oldSelected.length; i++) {
                if (dataIds.indexOf(oldSelected[i].getDataId()) < 0) {
                    newSelected.push(oldSelected[i]);
                    newSelectedRows.push(this.getRowIndexByNode(oldSelected[i]));
                }
            }

            this.root.removeSelections(dataIds);

            if (oldSelected.length !== newSelected.length) {
                this.grid.setSelectedRows(newSelectedRows);
            }
        }

        getSelectedNodes(): TreeNode<DATA>[] {
            return this.grid.getSelectedRowItems();
        }

        getSelectedDataList(): DATA[] {
            return this.highlightedNode ?
                [this.highlightedNode.getData()] :
                   this.root.getFullSelection().map((node: TreeNode<DATA>) => {
                       return node.getData();
                   });
        }

        // Hard reset

        reload(parentNodeData?: DATA): wemQ.Promise<void> {
            let expandedNodesDataId = this.grid.getDataView().getItems().filter((item) => {
                return item.isExpanded();
            }).map((item) => {
                return item.getDataId();
            });

            let selection = this.root.getCurrentSelection();

            this.root.resetCurrentRoot(parentNodeData);
            this.initData([]);

            this.highlightedNode = null;

            this.mask();

            return this.reloadNode(null, expandedNodesDataId)
                .then(() => {
                    this.root.setCurrentSelection(selection);
                    this.initData(this.root.getCurrentRoot().treeToList());
                    this.updateExpanded();
                }).catch((reason: any) => {
                    this.initData([]);
                    this.handleError(reason);
                }).then(() => {
                    this.updateExpanded();
                }).then(() => this.notifyLoaded());
        }

        protected handleError(reason: any, message?: String) {
            this.grid.show();
            if (this.quietErrorHandling) {
                this.errorPanel.setError(message || reason);
                this.grid.hide();
                this.errorPanel.show();
            } else {
                api.DefaultErrorHandler.handle(reason);
            }
        }

        protected hideErrorPanel() {
            this.grid.show();
            if (this.quietErrorHandling) {
                this.errorPanel.hide();
            }
        }

        private reloadNode(parentNode?: TreeNode<DATA>, expandedNodesDataId?: String[]): wemQ.Promise<void> {

            let deferred = wemQ.defer<void>();
            let promises = [];

            this.fetchData(parentNode).then((dataList: DATA[]) => {
                let hasNotEmptyChildren = false;

                parentNode = parentNode || this.root.getCurrentRoot();
                parentNode.getChildren().length = 0;

                dataList.forEach((data: DATA) => {
                    let child = this.dataToTreeNode(data, parentNode);
                    let dataId = this.getDataId(data);
                    child.setExpanded(this.expandAll || expandedNodesDataId.indexOf(dataId) > -1);
                    parentNode.addChild(child);

                    if (child.isExpanded() && this.hasChildren(data)) {
                        hasNotEmptyChildren = true;
                        promises.push(this.reloadNode(child, expandedNodesDataId));
                    }
                });

                if (!hasNotEmptyChildren) {
                    deferred.resolve(null);
                } else {
                    wemQ.all(promises).spread(() => {
                        deferred.resolve(null);
                    }).catch((reason: any) => {
                        deferred.reject(reason);
                    }).done();
                }
            }).catch((reason: any) => {
                this.handleError(reason);
                deferred.reject(reason);
            }).done();

            return deferred.promise;
        }

        refreshNode(node?: TreeNode<DATA>): void {
            let root = this.root.getCurrentRoot();
            this.setActive(false);

            node = node || root;
            node.regenerateIds();
            root.setExpanded(true);
            this.initData(root.treeToList());

            this.invalidate();

            this.setActive(true);

            this.notifyLoaded();
        }

        // Soft reset, that saves node status
        refresh(): void {
            let root = this.root.getCurrentRoot();

            this.setActive(false);

            this.grid.invalidate();

            root.setExpanded(true);
            this.initData(root.treeToList());
            this.invalidate();

            this.setActive(true);

            this.notifyLoaded();
        }

        updateNode(data: DATA, oldDataId?: string): wemQ.Promise<void> {

            let dataId = oldDataId || this.getDataId(data);
            let nodeToUpdate = this.root.getCurrentRoot().findNode(dataId);

            if (!nodeToUpdate) {
                throw new Error('TreeNode to update not found: ' + dataId);
            }

            return this.fetchAndUpdateNodes([nodeToUpdate], oldDataId ? this.getDataId(data) : undefined);
        }

        updateNodes(data: DATA, oldDataId?: string): wemQ.Promise<void> {

            let dataId = oldDataId || this.getDataId(data);
            let nodesToUpdate = this.root.getCurrentRoot().findNodes(dataId);

            if (!nodesToUpdate) {
                throw new Error('TreeNode to update not found: ' + dataId);
            }

            return this.fetchAndUpdateNodes(nodesToUpdate, oldDataId ? this.getDataId(data) : undefined);
        }

        private fetchAndUpdateNodes(nodesToUpdate: TreeNode<DATA>[], dataId?: string): wemQ.Promise<void> {
            return this.fetch(nodesToUpdate[0], dataId)
                .then((data: DATA) => {
                    nodesToUpdate.forEach((node) => {
                        if (dataId) {
                            node.setDataId(dataId);
                        }
                        node.setData(data);
                        if (this.expandAll) {
                            node.setExpanded(this.expandAll);
                        }
                        node.setDataId(this.getDataId(data));
                        node.clearViewers();

                        if (node.isVisible()) {
                            let rowIndex = this.getRowIndexByNode(node);
                            let selected = this.grid.isRowSelected(rowIndex);
                            let highlighted = this.isNodeHighlighted(node);
                            this.gridData.updateItem(node.getId(), node);
                            if (selected) {
                                this.grid.addSelectedRow(rowIndex);
                            } else if (highlighted) {
                                this.removeHighlighting(true);
                                this.highlightRowByNode(node);
                            }
                        }
                    });

                }).catch((reason: any) => {
                    this.handleError(reason);
                });
        }

        deleteNode(data: DATA): void {
            this.deleteRootNode(this.root.getDefaultRoot(), data);
            if (this.root.isFiltered()) {
                this.deleteRootNode(this.root.getFilteredRoot(), data);
            }
        }

        private deleteRootNode(root: TreeNode<DATA>, data: DATA): void {
            const dataId = this.getDataId(data);

            AppHelper.whileTruthy(() => root.findNode(dataId), (node) => {
                if (node.hasChildren()) {
                    node.getChildren().forEach((child: TreeNode<DATA>) => {
                        this.deleteNode(child.getData());
                    });
                }
                if (this.gridData.getItemById(node.getId())) {
                    this.gridData.deleteItem(node.getId());
                }

                const parent = node.getParent();
                if (node && parent) {
                    parent.removeChild(node);
                    parent.setMaxChildren(parent.getMaxChildren() - 1);
                    this.notifyDataChanged(new DataChangedEvent<DATA>([node], DataChangedEvent.DELETED));
                }
            });

            this.root.removeSelections([dataId]);
        }

        /**
         * @param data
         * @param nextToSelection - by default node is appended as child to selection or root, set this to true to append to the same level
         * @param stashedParentNode
         */
        appendNode(data: DATA, nextToSelection: boolean = false, prepend: boolean = true,
                   stashedParentNode?: TreeNode<DATA>): wemQ.Promise<void> {
            let parentNode = this.getParentNode(nextToSelection, stashedParentNode);
            let index = prepend ? 0 : Math.max(0, parentNode.getChildren().length - 1);
            return this.insertNode(data, nextToSelection, index, stashedParentNode);
        }

        getParentNode(nextToSelection: boolean = false, stashedParentNode?: TreeNode<DATA>) {
            let root = stashedParentNode || this.root.getCurrentRoot();
            let parentNode: TreeNode<DATA>;

            parentNode = this.getFirstSelectedOrHighlightedNode();

            if (parentNode) {
                if (nextToSelection) {
                    parentNode = parentNode.getParent() || this.root.getCurrentRoot();
                }
            } else {
                parentNode = root;
            }
            return parentNode;
        }

        insertNode(data: DATA, nextToSelection: boolean = false, index: number = 0,
                   stashedParentNode?: TreeNode<DATA>): wemQ.Promise<void> {
            let deferred = wemQ.defer<void>();
            let root = stashedParentNode || this.root.getCurrentRoot();
            let parentNode = this.getParentNode(nextToSelection, stashedParentNode);

            let isRootParentNode: boolean = (parentNode === root);

            if (!parentNode.hasChildren() && !isRootParentNode) {
                this.fetchData(parentNode)
                    .then((dataList: DATA[]) => {
                        if (parentNode.hasChildren()) {
                            this.doInsertNodeToParentWithChildren(parentNode, data, root, index, stashedParentNode, isRootParentNode);

                        } else {
                            parentNode.setChildren(this.dataToTreeNodes(dataList, parentNode));
                            this.initData(root.treeToList());
                            let node = root.findNode(this.getDataId(data));
                            if (!node) {
                                parentNode.insertChild(this.dataToTreeNode(data, root), index);
                                node = root.findNode(this.getDataId(data));
                            }

                            if (node) {
                                if (!stashedParentNode) {
                                    this.gridData.setItems(root.treeToList());
                                }
                                this.notifyDataChanged(new DataChangedEvent<DATA>([node], DataChangedEvent.ADDED));

                                if (parentNode !== root) {
                                    this.refreshNodeData(parentNode).then((refreshedNode: TreeNode<DATA>) => {
                                        if (!stashedParentNode) {
                                            this.updateSelectedNode(refreshedNode);
                                        }
                                    });
                                }
                            }
                        }
                        deferred.resolve(null);
                    }).catch((reason: any) => {
                    this.handleError(reason);
                    deferred.reject(reason);
                });
            } else {
                this.doInsertNodeToParentWithChildren(parentNode, data, root, index, stashedParentNode, isRootParentNode);
                deferred.resolve(null);
            }

            return deferred.promise;
        }

        private doInsertNodeToParentWithChildren(parentNode: TreeNode<DATA>,
                                                 data: DATA,
                                                 root: TreeNode<DATA>,
                                                 index: number,
                                                 stashedParentNode: TreeNode<DATA>,
                                                 isRootParentNode: boolean) {
            parentNode.insertChild(this.dataToTreeNode(data, root), index);

            let node = root.findNode(this.getDataId(data));
            if (node) {
                if (!stashedParentNode) {
                    this.gridData.setItems(root.treeToList());
                }
                if (isRootParentNode) {
                    this.sortNodeChildren(parentNode);
                } else {
                    if (!stashedParentNode) {
                        this.updateSelectedNode(parentNode);
                    }
                }
            }
        }

        deleteNodes(dataList: DATA[]): void {
            this.deleteRootNodes(this.root.getDefaultRoot(), dataList);
            if (this.root.isFiltered()) {
                this.deleteRootNodes(this.root.getFilteredRoot(), dataList);
            }
        }

        private deleteRootNodes(root: TreeNode<DATA>, dataList: DATA[]): void {
            let updated: TreeNode<DATA>[] = [];
            let deleted: TreeNode<DATA>[] = [];

            dataList.forEach((data: DATA) => {
                let node = root.findNode(this.getDataId(data));
                if (node && node.getParent()) {
                    let parent = node.getParent();
                    this.deleteRootNode(root, node.getData());
                    updated.push(parent);
                    deleted.push(node);
                    updated.filter((el) => {
                        return el.getDataId() !== node.getDataId();
                    });
                }
            });
            root.treeToList().forEach((child: TreeNode<DATA>) => {
                this.refreshNodeData(child);
            });
            this.notifyDataChanged(new DataChangedEvent<DATA>(deleted, DataChangedEvent.DELETED));
        }

        initData(nodes: TreeNode<DATA>[]) {
            this.gridData.setItems(nodes, 'id');
            this.notifyDataChanged(new DataChangedEvent<DATA>(nodes, DataChangedEvent.ADDED));
            this.resetCurrentSelection(nodes);
        }

        private resetCurrentSelection(nodes: TreeNode<DATA>[]) {
            let selection: any = [];
            let selectionIds = this.root.getFullSelection().map(el => el.getDataId());

            selectionIds.forEach((selectionId) => {
                nodes.forEach((node, index) => {
                    if (node.getDataId() === selectionId) {
                        selection.push(index);
                    }
                });
            });

            this.grid.setSelectedRows(selection);
        }

        expandNode(node?: TreeNode<DATA>, expandAll: boolean = false): wemQ.Promise<boolean> {
            let deferred = wemQ.defer<boolean>();

            node = node || this.root.getCurrentRoot();

            if (node) {
                node.setExpanded(true);

                if (node.hasChildren()) {
                    this.initData(this.root.getCurrentRoot().treeToList());
                    this.updateExpanded();
                    if (expandAll) {
                        node.getChildren().forEach((child: TreeNode<DATA>) => {
                            this.expandNode(child);
                        });
                    }
                    deferred.resolve(true);
                } else {
                    this.mask();
                    this.fetchData(node)
                        .then((dataList: DATA[]) => {
                            node.setChildren(this.dataToTreeNodes(dataList, node));
                            this.initData(this.root.getCurrentRoot().treeToList());
                            this.updateExpanded();
                            if (expandAll) {
                                node.getChildren().forEach((child: TreeNode<DATA>) => {
                                    this.expandNode(child);
                                });
                            }
                            deferred.resolve(true);
                        }).catch((reason: any) => {
                        this.handleError(reason);
                        deferred.resolve(false);
                    }).done(() => this.notifyLoaded());
                }
            }

            return deferred.promise;
        }

        isAllSelected(): boolean {
            if (this.grid.isAllSelected()) {
                return true;
            }

            let selectedNodes = this.grid.getSelectedRows();

            if (!selectedNodes) {
                return false;
            }

            let nonEmptyNodes = this.gridData.getItems().filter((data: TreeNode<DATA>) => {
                return (!!data && data.getDataId() !== '');
            });

            return nonEmptyNodes.length === selectedNodes.length;
        }

        protected updateExpanded() {
            this.invalidate();
            this.setActive(true);
        }

        private updateSelectedNode(node: TreeNode<DATA>) {
            this.getGrid().clearSelection();
            this.refreshNode(node);
            let row = this.getRowIndexByNode(node);
            this.grid.selectRow(row);
        }

        private collapseNode(node: TreeNode<DATA>) {
            node.setExpanded(false);

            // Save the selected collapsed rows in cache
            this.root.stashSelection();

            this.gridData.refresh();
            this.invalidate();
            this.triggerSelectionChangedListeners();
            this.setActive(true);
        }

        notifyLoaded(): void {
            this.loadedListeners.forEach((listener) => {
                listener(this);
            });
        }

        onLoaded(listener: () => void) {
            this.loadedListeners.push(listener);
            return this;
        }

        unLoaded(listener: () => void) {
            this.loadedListeners = this.loadedListeners.filter((curr) => {
                return curr !== listener;
            });
            return this;
        }

        getItem(rowIndex: number): TreeNode<DATA> {
            return this.gridData.getItem(rowIndex);
        }

        private notifySelectionChanged(event: any, rows: number[]): void {
            let currentSelection: TreeNode<DATA>[] = [];
            if (rows) {
                rows.forEach((rowIndex) => {
                    currentSelection.push(this.gridData.getItem(rowIndex));
                });
            }

            this.root.setCurrentSelection(currentSelection);

            this.triggerSelectionChangedListeners();
        }

        private notifyHighlightingChanged(): void {
            this.highlightingChangeListeners.forEach((listener: Function) => {
                listener(this.highlightedNode);
            });
        }

        triggerSelectionChangedListeners() {
            this.selectionChangeListeners.forEach((listener: Function) => {
                listener(this.root.getCurrentSelection(), this.root.getFullSelection(), !!this.highlightedNode);
            });
        }

        onHighlightingChanged(listener: (node: TreeNode<DATA>) => void) {
            this.highlightingChangeListeners.push(listener);
            return this;
        }

        unHighlightingChanged(listener: (node: TreeNode<DATA>) => void) {
            this.highlightingChangeListeners = this.highlightingChangeListeners.filter((curr) => {
                return curr != listener;
            });
            return this;
        }

        onSelectionChanged(listener: (currentSelection: TreeNode<DATA>[], fullSelection: TreeNode<DATA>[], highlighted: boolean) => void) {
            this.selectionChangeListeners.push(listener);
            return this;
        }

        unSelectionChanged(listener: (currentSelection: TreeNode<DATA>[], fullSelection: TreeNode<DATA>[], highlighted: boolean) => void) {
            this.selectionChangeListeners = this.selectionChangeListeners.filter((curr) => {
                return curr !== listener;
            });
            return this;
        }

        private notifyContextMenuShown(x: number, y: number) {
            let showContextMenuEvent = new ContextMenuShownEvent(x, y);
            this.contextMenuListeners.forEach((listener) => {
                listener(showContextMenuEvent);
            });
        }

        protected getErrorPanel(): ValidationRecordingViewer {
            return this.errorPanel;
        }

        onContextMenuShown(listener: () => void) {
            this.contextMenuListeners.push(listener);
            return this;
        }

        unContextMenuShown(listener: () => void) {
            this.contextMenuListeners = this.contextMenuListeners.filter((curr) => {
                return curr !== listener;
            });
            return this;
        }

        notifyDataChanged(event: DataChangedEvent<DATA>) {
            this.dataChangeListeners.forEach((listener) => {
                listener(event);
            });
        }

        onDataChanged(listener: (event: DataChangedEvent<DATA>) => void) {
            this.dataChangeListeners.push(listener);
            return this;
        }

        unDataChanged(listener: (event: DataChangedEvent<DATA>) => void) {
            this.dataChangeListeners = this.dataChangeListeners.filter((curr) => {
                return curr !== listener;
            });
            return this;
        }

        isFiltered() {
            return this.root.isFiltered();
        }

        invalidate() {
            this.grid.invalidate();

            this.highlightCurrentNode();
        }

        initAndRender() {
            this.initData(this.getRoot().getCurrentRoot().treeToList());
            this.invalidate();
        }

        refreshNodeData(parentNode: TreeNode<DATA>): wemQ.Promise<TreeNode<DATA>> {
            return null;
        }

        sortNodeChildren(node: TreeNode<DATA>): void {
            // must be implemented by children
        }

        isNodeHighlighted(node: TreeNode<DATA>) {
            return node == this.highlightedNode;
        }

        protected handleItemMetadata(row: number) {
            const node = this.gridData.getItem(row);
            if (this.isEmptyNode(node)) {
                return {cssClasses: 'empty-node'};
            }

            return null;
        }
    }
}
