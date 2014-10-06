module app.view {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentVersion = api.content.ContentVersion;

    export class AllContentVersionsTreeGrid extends ContentVersionsTreeGrid {

        constructor() {
            super();
        }

        fetchChildren(parentNode?: TreeNode<ContentVersion>): wemQ.Promise<ContentVersion[]> {
            if (this.contentId) {
                var activePromise = new api.content.GetActiveContentVersionsRequest(this.contentId);
                var allPromise = new api.content.GetContentVersionsRequest(this.contentId);
                return wemQ.all([allPromise.sendAndParse(), activePromise.sendAndParse()]).
                    spread<ContentVersion[]>((allVersions: ContentVersion[], activeVersions: ContentVersion[]) =>
                    this.enrichWithWorkspaces(allVersions, activeVersions));
            } else {
                throw new Error("Required contentId not set for ActiveContentVersionsTreeGrid")
            }
        }

        private enrichWithWorkspaces(allVersions: ContentVersion[], activeVersions: ContentVersion[]): ContentVersion[] {
            activeVersions.forEach((activeVersion: ContentVersion) => {
                for (var i = 0; i < allVersions.length; i++) {
                    var allVersion = allVersions[i];
                    if (activeVersion.id == allVersion.id) {
                        allVersion.workspaces = activeVersion.workspaces;
                        break;
                    }
                }
            });
            return allVersions;
        }

    }

}