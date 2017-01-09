module api.content.util {

    import TreeNode = api.ui.treegrid.TreeNode;

    export class ContentNodeByDisplayNameComparator implements api.Comparator<TreeNode<ContentSummaryAndCompareStatus>> {

        compare(a: TreeNode<ContentSummaryAndCompareStatus>, b: TreeNode<ContentSummaryAndCompareStatus>): number {
            let firstName: string;
            let secondName: string;
            if (!a.getData().getContentSummary()) {
                return 1;
            } else {
                firstName = a.getData().getContentSummary().getDisplayName() || '';
            }
            if (!b.getData().getContentSummary()) {
                return -1;
            } else {
                secondName = b.getData().getContentSummary().getDisplayName() || '';
            }
            return firstName.localeCompare(secondName);
        }
    }
}