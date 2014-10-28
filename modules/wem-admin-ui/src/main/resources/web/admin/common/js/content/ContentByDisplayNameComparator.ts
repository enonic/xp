module api.content {

    import TreeNode = api.ui.treegrid.TreeNode;

    export class ContentByDisplayNameComparator implements api.Comparator<TreeNode<ContentSummaryAndCompareStatus>> {

        compare(a: TreeNode<ContentSummaryAndCompareStatus>, b: TreeNode<ContentSummaryAndCompareStatus>): number {
            var firstName = a.getData().getContentSummary().getDisplayName();
            var secondName = b.getData().getContentSummary().getDisplayName();
            return firstName.localeCompare(secondName);
        }
    }
}