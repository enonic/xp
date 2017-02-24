module api.ui.selector {

    import TreeNode = api.ui.treegrid.TreeNode;

    export interface OptionDataHelper<DATA> {

        hasChildren(data: DATA): boolean;

        getDataId(data: DATA): string;
    }
}