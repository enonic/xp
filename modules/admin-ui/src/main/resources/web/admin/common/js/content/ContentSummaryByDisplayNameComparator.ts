module api.content {

    import TreeNode = api.ui.treegrid.TreeNode;

    import ContentTypeSummary = api.schema.content.ContentTypeSummary;

    export class ContentSummaryByDisplayNameComparator implements api.Comparator<ContentTypeSummary> {

        compare(item1: ContentTypeSummary, item2: ContentTypeSummary):number {
            if (item1.getDisplayName().toLowerCase() > item2.getDisplayName().toLowerCase()) {
                return 1;
            } else if (item1.getDisplayName().toLowerCase() < item2.getDisplayName().toLowerCase()) {
                return -1;
            } else if (item1.getName() > item2.getName()) {
                return 1;
            } else if (item1.getName() < item2.getName()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}