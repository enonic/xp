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
            var filteredVersions : ContentVersion[] = allVersions.length ? [allVersions[0]] : [];

            for (var i = 1; i < allVersions.length; i++) {
                if (Math.abs(allVersions[i-1].modified.getTime() - allVersions[i].modified.getTime()) > 500) {
                    filteredVersions.push(allVersions[i]);
                }
            }

            activeVersions.forEach((activeVersion: ContentVersion) => {
                filteredVersions.filter((version: ContentVersion) => {
                    return version.id == activeVersion.id;
                }).forEach((version: ContentVersion) => {
                    version.workspaces = activeVersion.workspaces;
                });
            });
            return filteredVersions;
        }

    }

}