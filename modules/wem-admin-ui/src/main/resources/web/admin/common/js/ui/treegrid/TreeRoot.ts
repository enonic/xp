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

            this.clearStashedSelection();
        }

        stashSelection() {
            this.stashedSelection = this.stashedSelection.concat(this.currentSelection);
            this.currentSelection = [];

            this.clearStashedSelection();
        }

        getFullSelection(uniqueOnly: boolean = true): TreeNode<DATA>[] {
            var fullSelection = this.currentSelection.
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
            var currentIds = this.currentSelection.map((el) => { return el.getDataId(); }),
                stashedIds = this.stashedSelection.map((el) => { return el.getDataId(); });

            this.stashedSelection= this.stashedSelection.filter((value, index, self) => {
                // remove duplicated nodes and those, that are already in `currentSelection`
                return (currentIds.indexOf(value.getDataId()) < 0) &&
                    (stashedIds.indexOf(value.getDataId()) === index);
            });
        }

        removeSelection(dataId: string) {
            this.currentSelection = this.currentSelection.filter((el) => { return el.getDataId() !== dataId; });
            this.stashedSelection = this.stashedSelection.filter((el) => { return el.getDataId() !== dataId; });
        }
    }
}
