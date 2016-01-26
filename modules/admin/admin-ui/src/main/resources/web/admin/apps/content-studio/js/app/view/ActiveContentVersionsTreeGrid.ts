module app.view {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentVersion = api.content.ContentVersion;

    export class ActiveContentVersionsTreeGrid extends ContentVersionsTreeGrid {

        constructor() {
            super();
        }

        fetchChildren(parentNode?: TreeNode<ContentVersion>): wemQ.Promise<ContentVersion[]> {
            if (this.contentId) {
                return new api.content.GetActiveContentVersionsRequest(this.contentId).sendAndParse();
            } else {
                throw new Error("Required contentId not set for ActiveContentVersionsTreeGrid")
            }
        }

    }

}