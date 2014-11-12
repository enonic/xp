module api.ui.treegrid {

    export class TreeRoot<DATA> {

        private defaultRoot: TreeNode<DATA>;

        private filteredRoot: TreeNode<DATA>;

        private filtered: boolean;

        private defaultSelection: TreeNode<DATA>[];

        private filteredSelection: TreeNode<DATA>[];

        private stashedSelection: TreeNode<DATA>[];


        constructor() {

            this.defaultRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();
            this.filteredRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();

            this.filtered = false;

            this.defaultSelection = [];

            this.filteredSelection = [];

            this.stashedSelection = [];

        }

        getDefaultRoot(): TreeNode<DATA> {
            return this.defaultRoot;
        }

        resetDefaultRoot() {
            this.defaultRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();
        }

        getFilteredRoot(): TreeNode<DATA> {
            return this.filteredRoot;
        }

        resetFilteredRoot() {
            this.filteredRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();
        }

        resetCurrentRoot() {
            if (this.isFiltered()) {
                this.resetFilteredRoot();
            } else {
                this.resetDefaultRoot();
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
                this.stashFilteredSelection();
            } else if (this.filtered && !filtered) {
                // stash selection on switch
                this.stashFilteredSelection();
            }

            this.filtered = filtered;
        }

        getDefaultSelection(): TreeNode<DATA>[] {
            return this.defaultSelection;
        }

        setDefaultSelection(selection: TreeNode<DATA>[]) {
            this.defaultSelection = selection;

            this.clearStashedSelection();
        }

        getFilteredSelection(): TreeNode<DATA>[] {
            return this.filteredSelection;
        }

        setFilteredSelection(selection: TreeNode<DATA>[]) {
            this.filteredSelection = selection;

            this.clearStashedSelection();
        }

        getCurrentSelection(): TreeNode<DATA>[] {
            return this.filtered ? this.filteredSelection
                                 : this.defaultSelection;
        }

        setCurrentSelection(selection: TreeNode<DATA>[]) {
            if (this.filtered) {
                this.setFilteredSelection(selection);
            } else {
                this.setDefaultSelection(selection);
            }
        }

        stashFilteredSelection() {
            this.stashedSelection = this.stashedSelection.concat(this.filteredSelection);

            this.clearStashedSelection();

            this.filteredSelection = [];
        }

        getFullSelection(uniqueOnly: boolean = true): TreeNode<DATA>[] {
            var fullSelection = this.defaultSelection.
                concat(this.filteredSelection).
                concat(this.stashedSelection);
            if (uniqueOnly) {
                var fullIds = fullSelection.map((el) => { return el.getDataId(); });
                fullSelection = fullSelection.filter((value, index, self) => {
                    return fullIds.indexOf(value.getDataId()) === index;
                });
            }

            return fullSelection;
        }

        private clearStashedSelection() {
            var defaultIds = this.defaultSelection.map((el) => { return el.getDataId(); }),
                stashedIds = this.stashedSelection.map((el) => { return el.getDataId(); });

            this.stashedSelection= this.stashedSelection.filter((value, index, self) => {
                // remove duplicated nodes and those, that are already in `defaultSelection`
                return (defaultIds.indexOf(value.getDataId()) < 0) &&
                    (stashedIds.indexOf(value.getDataId()) === index);
            });
        }

        removeSelection(dataId: string) {
            this.defaultSelection = this.defaultSelection.filter((el) => { return el.getDataId() !== dataId; });
            this.filteredSelection = this.filteredSelection.filter((el) => { return el.getDataId() !== dataId; });
            this.stashedSelection = this.stashedSelection.filter((el) => { return el.getDataId() !== dataId; });
        }
    }
}
