module api.ui.selector {
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    export class OptionsTreeGrid<OPTION_DISPLAY_VALUE> extends TreeGrid<Option<OPTION_DISPLAY_VALUE>> {

        static MAX_FETCH_SIZE: number = 10;
        private loader: OptionDataLoader<OPTION_DISPLAY_VALUE>;
        private treeDataHelper: OptionDataHelper<OPTION_DISPLAY_VALUE>;
        private readonlyChecker: (optionToCheck: OPTION_DISPLAY_VALUE) => boolean;
        private isSelfLoading: boolean;
        private defaultOption: OPTION_DISPLAY_VALUE;
        private isDefaultOptionActive: boolean;

        constructor(columns: api.ui.grid.GridColumn<any>[],
                    gridOptions: api.ui.grid.GridOptions<any>,
                    loader: OptionDataLoader<OPTION_DISPLAY_VALUE>,
                    treeDataHelper: OptionDataHelper<OPTION_DISPLAY_VALUE>) {

            let builder: TreeGridBuilder<Option<OPTION_DISPLAY_VALUE>> =
                new TreeGridBuilder<Option<OPTION_DISPLAY_VALUE>>().setColumns(columns)
                    .setOptions(gridOptions)
                    .setPartialLoadEnabled(true)
                    .setLoadBufferSize(20)// rows count
                    .setAutoLoad(false)
                    .prependClasses('dropdown-tree-grid')
                    .setRowHeight(45)
                    .setHotkeysEnabled(true)
                    .disableMultipleSelection(true)
                    .setShowToolbar(false);

            builder.getOptions().setDataItemColumnValueExtractor(builder.nodeExtractor);

            super(builder);
            this.loader = loader;
            this.treeDataHelper = treeDataHelper;
            this.isSelfLoading = true;
            this.highlightingEnabled = false;

            this.initEventHandlers();
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[]) {
            this.isSelfLoading = false;
            this.getGrid().getDataView().setItems(this.dataToTreeNodes(options, this.getRoot().getCurrentRoot()), 'dataId');
        }

        setReadonlyChecker(checker: (optionToCheck: OPTION_DISPLAY_VALUE) => boolean) {
            this.readonlyChecker = checker;
        }

        queryScrollable(): api.dom.Element {
            let gridClasses = (' ' + this.getGrid().getEl().getClass()).replace(/\s/g, '.');
            let viewport = api.dom.Element.fromString(gridClasses + ' .slick-viewport', false);
            return viewport;
        }

        reload(parentNodeData?: Option<OPTION_DISPLAY_VALUE>): wemQ.Promise<void> {
            return super.reload(parentNodeData).then(() => {
                if (this.defaultOption && !this.isDefaultOptionActive) {
                    this.scrollToDefaultOption(this.getRoot().getCurrentRoot(), 0);
                    this.isDefaultOptionActive = true;
                }
            });
        }

        private initEventHandlers() {
            let onBecameActive = (active: boolean) => {
                if (active) {
                    this.getGrid().resizeCanvas();
                    this.unActiveChanged(onBecameActive);
                }
            };
            // update columns when grid becomes active for the first time
            this.onActiveChanged(onBecameActive);

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (this.isInRenderingView()) {
                    this.getGrid().resizeCanvas();
                }
            });
        }

        hasChildren(option: Option<OPTION_DISPLAY_VALUE>): boolean {
            if (!this.isSelfLoading) {
                return false;
            }
            return this.treeDataHelper.hasChildren(option.displayValue);
        }

        getDataId(option: Option<OPTION_DISPLAY_VALUE>): string {
            return this.treeDataHelper.getDataId(option.displayValue);
        }

        isEmptyNode(node: TreeNode<Option<OPTION_DISPLAY_VALUE>>): boolean {
            return !node.getData().displayValue;
        }

        fetch(node: TreeNode<Option<OPTION_DISPLAY_VALUE>>, dataId?: string): wemQ.Promise<Option<OPTION_DISPLAY_VALUE>> {
            return this.loader.fetch(node).then((data: OPTION_DISPLAY_VALUE) => {
                return this.optionDataToTreeNodeOption(data);
            });
        }

        fetchChildren(parentNode?: TreeNode<Option<OPTION_DISPLAY_VALUE>>): wemQ.Promise<Option<OPTION_DISPLAY_VALUE>[]> {
            this.isSelfLoading = true;
            parentNode = parentNode ? parentNode : this.getRoot().getCurrentRoot();

            let from = parentNode.getChildren().length;
            if (from > 0 && !parentNode.getChildren()[from - 1].getData().displayValue) {
                parentNode.getChildren().pop();
                from--;
            }

            return this.loader.fetchChildren(parentNode, from, OptionsTreeGrid.MAX_FETCH_SIZE).then(
                (loadedData: OptionDataLoaderData<OPTION_DISPLAY_VALUE>) => {
                    let newOptions = this.optionsDataToTreeNodeOption(loadedData.getData());
                    let options = parentNode.getChildren().map((el) => el.getData()).slice(0, from).concat(newOptions);

                    parentNode.setMaxChildren(loadedData.getTotalHits());

                    return this.loader.checkReadonly(loadedData.getData()).then((readonlyIds: string[]) => {
                        newOptions.forEach((option: Option<OPTION_DISPLAY_VALUE>) => {
                            const markedReadonly = readonlyIds.some((id: string) => {
                                if (this.treeDataHelper.getDataId(option.displayValue) === id) {
                                    option.readOnly = true;
                                    return true;
                                }
                            });
                            if (!markedReadonly) {
                                if (this.readonlyChecker && this.readonlyChecker(option.displayValue)) {
                                    option.readOnly = true;
                                }
                            }
                        });

                        if (from + loadedData.getHits() < loadedData.getTotalHits()) {
                            options.push(this.makeEmptyData());
                        }
                        return options;
                    });
                });
        }

        presetDefaultOption(data: OPTION_DISPLAY_VALUE) {
            this.defaultOption = data;
            this.isDefaultOptionActive = false;
        }

        private scrollToDefaultOption(parentNode: TreeNode<Option<OPTION_DISPLAY_VALUE>>, startFrom: number) {
            const length = parentNode.getChildren().length;
            const defaultOptionId = this.treeDataHelper.getDataId(this.defaultOption);
            for (let i = startFrom; i < length; i++) {
                const child = parentNode.getChildren()[i];
                const childOption = child.getData().displayValue;
                if (childOption) {
                    if (this.treeDataHelper.getDataId(childOption) == defaultOptionId) {
                        this.scrollToRow(this.getGrid().getDataView().getRowById(child.getId()), true); // found target data node
                        return;
                    }
                    if (this.treeDataHelper.isDescendingPath(this.defaultOption, childOption)) {
                        // found ancestor of target data node
                        this.expandNode(child).then(() => {
                            this.scrollToDefaultOption(child, 0); // expand target data node ancestor and keep searching
                        });
                        return;
                    }
                }
            }

            // if reached here  - no matches were found, need to load more children
            this.fetchBatchOfChildren(parentNode);
        }

        private fetchBatchOfChildren(parentNode: TreeNode<Option<OPTION_DISPLAY_VALUE>>) {
            const length = parentNode.getChildren().length;
            const from = parentNode.getChildren()[length - 1].getData().displayValue ? length : length - 1;
            if (from < parentNode.getMaxChildren()) {
                this.fetchChildren(parentNode).then((children: Option<OPTION_DISPLAY_VALUE>[]) => {
                    let fetchedChildrenNodes = this.dataToTreeNodes(children, parentNode);
                    parentNode.setChildren(fetchedChildrenNodes);
                    this.initData(this.getRoot().getCurrentRoot().treeToList());

                    this.scrollToDefaultOption(parentNode, from);
                });
            }
        }

        private optionsDataToTreeNodeOption(data: OPTION_DISPLAY_VALUE[]): Option<OPTION_DISPLAY_VALUE>[] {
            return data.map((item) => this.optionDataToTreeNodeOption(item));
        }

        private optionDataToTreeNodeOption(data: OPTION_DISPLAY_VALUE): Option<OPTION_DISPLAY_VALUE> {
            return {
                value: this.treeDataHelper.getDataId(data),
                displayValue: data
            };
        }

        private makeEmptyData(): Option<OPTION_DISPLAY_VALUE> {
            return {
                value: null,
                displayValue: null
            };
        }

        protected handleItemMetadata(row: number) {
            let node = this.getItem(row);
            if (this.isEmptyNode(node)) {
                return {cssClasses: 'empty-node'};
            }

            if (node.getData().readOnly) {
                if (this.treeDataHelper.getDataId(node.getData().displayValue) !=
                    this.treeDataHelper.getDataId(this.defaultOption)) {
                    return {cssClasses: "readonly' title='This content is read-only'"};
                } else {
                    return {cssClasses: "active readonly' title='This content is read-only'"};
                }
            }

            return null;
        }
    }
}
