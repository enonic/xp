module api.content.util {

    import TreeNode = api.ui.treegrid.TreeNode;

    export class ContentNodeByModifiedTimeComparator implements api.Comparator<TreeNode<ContentSummaryAndCompareStatus>> {

        compare(a:TreeNode<ContentSummaryAndCompareStatus>, b:TreeNode<ContentSummaryAndCompareStatus>):number {
            let firstDate = !a.getData().getContentSummary() ? null : a.getData().getContentSummary().getModifiedTime(),
                secondDate = !b.getData().getContentSummary() ? null : b.getData().getContentSummary().getModifiedTime();
            return firstDate < secondDate ? 1 : (firstDate > secondDate) ? -1 : 0;
        }
    }
}