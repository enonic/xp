module api.ui.selector {

    import TreeNode = api.ui.treegrid.TreeNode;

    export interface OptionDataHelper<DATA> {

        hasChildren(data: DATA): boolean;

        getDataId(data: DATA): string;

        isDisabled(data: DATA): boolean;

        isDescendingPath(childOption: DATA, parentOption: DATA);
    }
}
