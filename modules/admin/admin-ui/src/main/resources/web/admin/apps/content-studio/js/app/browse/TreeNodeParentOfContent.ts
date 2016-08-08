import "../../api.ts";

import TreeNode = api.ui.treegrid.TreeNode;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class TreeNodeParentOfContent {

    private data: ContentSummaryAndCompareStatus;

    private node: TreeNode<ContentSummaryAndCompareStatus>;

    constructor(data: ContentSummaryAndCompareStatus, node: TreeNode<ContentSummaryAndCompareStatus>) {
        this.data = data;
        this.node = node;
    }

    getData(): ContentSummaryAndCompareStatus {
        return this.data;
    }

    getNode(): TreeNode<ContentSummaryAndCompareStatus> {
        return this.node;
    }
}
