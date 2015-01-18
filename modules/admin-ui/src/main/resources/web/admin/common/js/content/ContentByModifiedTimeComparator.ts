module api.content {

    import TreeNode = api.ui.treegrid.TreeNode;

    export class ContentByModifiedTimeComparator implements api.Comparator<TreeNode<ContentSummaryAndCompareStatus>> {

        compare(a: TreeNode<ContentSummaryAndCompareStatus>, b: TreeNode<ContentSummaryAndCompareStatus>): number {
            var firstDate = !a.getData().getContentSummary() ? null : a.getData().getContentSummary().getModifiedTime(),
                secondDate = !b.getData().getContentSummary() ? null : b.getData().getContentSummary().getModifiedTime();
            return firstDate < secondDate ? 1 : (firstDate > secondDate) ? -1 : 0;
        }
    }
}