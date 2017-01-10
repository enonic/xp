module api.ui.treegrid {

    export class TreeRoot<DATA> {

        private defaultRoot: TreeNode<DATA>;

        private filteredRoot: TreeNode<DATA>;

        private filtered: boolean;

        private newlySelected: boolean;

        private currentSelection: TreeNode<DATA>[];

        private stashedSelection: TreeNode<DATA>[];

        constructor() {

            this.defaultRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();
            this.filteredRoot = new TreeNodeBuilder<DATA>().setExpanded(true).build();

            this.filtered = false;

            this.currentSelection = [];

            this.stashedSelection = [];

            this.updateNewlySelected([]);
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

        isNewlySelected(): boolean {
            return this.newlySelected;
        }

        // Should be called before selection changed
        private updateNewlySelected(newSelection: TreeNode<DATA>[]) {
            const isEmpty = (selection: TreeNode<DATA>[]) => (!selection || selection.length === 0);
            const isUnary = (selection: TreeNode<DATA>[]) => (selection.length === 1);
            const isNew = (selection: TreeNode<DATA>) => {
                return this.getFullSelection().map(el => el.getDataId()).every(id => id !== selection.getDataId());
            };

            const curr = this.currentSelection;
            const stash = this.stashedSelection;

            if (isUnary(newSelection) && isNew(newSelection[0])) {
                this.newlySelected = isEmpty(stash);
            } else { // isMultiary or isEmpty
                this.newlySelected = isEmpty(curr) && isEmpty(stash);
            }
        }

        getCurrentSelection(): TreeNode<DATA>[] {
            return this.currentSelection;
        }

        setCurrentSelection(selection: TreeNode<DATA>[]) {
            this.updateNewlySelected(selection);

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
            let fullSelection = this.currentSelection.
                concat(this.stashedSelection);
            if (uniqueOnly) {
                let fullIds = fullSelection.map((el) => {
                    return el.getDataId();
                });
                fullSelection = fullSelection.filter((value, index) => {
                    return fullIds.indexOf(value.getDataId()) === index;
                });
            }

            fullSelection = fullSelection.filter((value) => {
                return !!value.getDataId();
            });

            return fullSelection;
        }

        private cleanStashedSelection() {
            let currentIds = this.currentSelection.map((el) => {
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

        removeSelections(dataIds: string[]) {
            this.currentSelection = this.currentSelection.filter((el) => {
                return dataIds.indexOf(el.getDataId()) < 0;
            });
            this.stashedSelection = this.stashedSelection.filter((el) => {
                return dataIds.indexOf(el.getDataId()) < 0;
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
