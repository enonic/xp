module api.ui.treegrid {

    export class TreeRoot<DATA> {

        private defaultRoot: TreeNode<DATA>;

        private filteredRoot: TreeNode<DATA>;

        private filtered: boolean;

        private currentSelection: TreeNode<DATA>[];

        private stashedSelection: TreeNode<DATA>[];


        constructor() {

            this.defaultRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();
            this.filteredRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();

            this.filtered = false;

            this.currentSelection = [];

            this.stashedSelection = [];

        }

        getDefaultRoot(): TreeNode<DATA> {
            return this.defaultRoot;
        }

        resetDefaultRoot(rootData?: DATA) {
            this.defaultRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();
            if (rootData) {
                this.defaultRoot.setData(rootData);
            }
        }

        getFilteredRoot(): TreeNode<DATA> {
            return this.filteredRoot;
        }

        resetFilteredRoot(rootData?: DATA) {
            this.filteredRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();
            if (rootData) {
                this.filteredRoot.setData(rootData);
            }
        }

        resetCurrentRoot(rootData?: DATA) {
            if (this.isFiltered()) {
                this.resetFilteredRoot(rootData);
            } else {
                this.resetDefaultRoot(rootData);
            }

        }

        getCurrentRoot(): TreeNode<DATA> {
            return this.filtered ? this.filteredRoot : this.defaultRoot;
        }

        isFiltered(): boolean {
            return this.filtered;
        }

        setFiltered(filtered: boolean = true) {
            if (filtered) {
                // reset the filter on switch to filter
                this.filteredRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();
                this.stashSelection();
            } else if (this.filtered && !filtered) {
                // stash selection on switch from filter to default
                this.stashSelection();
            }

            this.filtered = filtered;
        }

        getCurrentSelection(): TreeNode<DATA>[] {
            return this.currentSelection;
        }

        setCurrentSelection(selection: TreeNode<DATA>[]) {
            this.currentSelection = selection;

            this.cleanStashedSelection();
        }

        getStashedSelection(): TreeNode<DATA>[] {
            return this.stashedSelection;
        }

        stashSelection() {
            this.stashedSelection = this.stashedSelection.concat(this.currentSelection);
            this.currentSelection = [];

            this.cleanStashedSelection();
        }

        getFullSelection(uniqueOnly: boolean = true): TreeNode<DATA>[] {
            var fullSelection = this.currentSelection.
                concat(this.stashedSelection);
            if (uniqueOnly) {
                var fullIds = fullSelection.map((el) => {
                    return el.getDataId();
                });
                fullSelection = fullSelection.filter((value, index, self) => {
                    return fullIds.indexOf(value.getDataId()) === index;
                });
            }

            return fullSelection;
        }

        private cleanStashedSelection() {
            var currentIds = this.currentSelection.map((el) => {
                    return el.getDataId();
                }),
                stashedIds = this.stashedSelection.map((el) => {
                    return el.getDataId();
                });

            this.stashedSelection = this.stashedSelection.filter((value, index, self) => {
                // remove duplicated nodes and those, that are already in `currentSelection`
                return (currentIds.indexOf(value.getDataId()) < 0) &&
                       (stashedIds.indexOf(value.getDataId()) === index);
            });
        }

        clearStashedSelection() {
            this.stashedSelection = [];
        }

        removeSelection(dataId: string) {
            this.currentSelection = this.currentSelection.filter((el) => {
                return el.getDataId() !== dataId;
            });
            this.stashedSelection = this.stashedSelection.filter((el) => {
                return el.getDataId() !== dataId;
            });
        }

        updateSelection(dataId: string, data: DATA) {
            this.currentSelection.forEach((el) => {
                if (el.getDataId() === dataId) { el.setData(data); }
            });
            this.stashedSelection.forEach((el) => {
                if (el.getDataId() === dataId) { el.setData(data); }
            });
        }
    }
}
