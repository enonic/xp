module app.view {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentVersion = api.content.ContentVersion;

    export class AllContentVersionsTreeGrid extends ContentVersionsTreeGrid {

        constructor() {
            super();
        }

        fetchChildren(parentNode?: TreeNode<ContentVersion>): wemQ.Promise<ContentVersion[]> {
            if (this.contentId) {
                return new api.content.GetContentVersionsRequest(this.contentId).sendAndParse();
            } else {
                return super.fetchChildren(parentNode);
            }
        }

    }

}