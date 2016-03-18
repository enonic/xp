module app.view {

    import ContentVersion = api.content.ContentVersion;
    import ContentId = api.content.ContentId;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class AllContentVersionsView extends api.ui.selector.list.ListBox<ContentVersion> {

        private contentId: ContentId;
        private status: api.content.CompareStatus;
        private loadedListeners: {(): void}[] = [];

        private static branchMaster = "master";

        constructor() {
            super("all-content-versions");
        }

        setContentData(item: ContentSummaryAndCompareStatus) {
            this.contentId = item.getContentId();
            this.status = item.getCompareStatus();
        }

        reload(): wemQ.Promise<void> {
            return this.loadData().then((contentVersions: ContentVersion[]) => {
                this.updateView(contentVersions);
                this.notifyLoaded();
            })
        }

        createItemView(item: ContentVersion, readOnly: boolean): api.dom.Element {
            var itemEl = new api.dom.DivEl("content-version-item");

            var contentVersionStatus = this.getStatus(item);
            if(!!contentVersionStatus) {
                var statusDiv = new api.dom.DivEl("status " + contentVersionStatus.workspace);
                statusDiv.setHtml(contentVersionStatus.status);
                itemEl.appendChild(statusDiv);
            }

            var descriptionDiv = new api.content.ContentVersionViewer();
            descriptionDiv.addClass("description");
            descriptionDiv.setObject(item);
            itemEl.appendChild(descriptionDiv);

            return itemEl;
        }

        getItemId(item: ContentVersion): string {
            return item.id;
        }

        onLoaded(listener: () => void) {
            this.loadedListeners.push(listener);
        }

        unLoaded(listener: () => void) {
            this.loadedListeners = this.loadedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyLoaded() {
            this.loadedListeners.forEach((listener) => {
                listener();
            });
        }


        private loadData(): wemQ.Promise<ContentVersion[]> {
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
            var filteredVersions: ContentVersion[] = allVersions.length ? [allVersions[0]] : [];

            for (var i = 1; i < allVersions.length; i++) {
                if (Math.abs(allVersions[i - 1].modified.getTime() - allVersions[i].modified.getTime()) > 500) {
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

        private updateView(contentVersions: ContentVersion[]) {
            this.clearItems();
            this.setItems(contentVersions);
        }

        private getStatus(contentVersion: ContentVersion): ContentVersionStatus {
            if (this.status == undefined) {
                return null;
            }
            var result = null;

            var hasMaster = contentVersion.workspaces.some((workspace) => {
                return workspace == AllContentVersionsView.branchMaster;
            });

            contentVersion.workspaces.some((workspace: string) => {
                if (!hasMaster || workspace == AllContentVersionsView.branchMaster) {
                   result = { workspace: workspace, status: this.getState(workspace) };
                }
                return true;
            });

            return result;
        }

        private getState(workspace): string {
            if (workspace == AllContentVersionsView.branchMaster) {
                return api.content.CompareStatusFormatter.formatStatus(api.content.CompareStatus.EQUAL);
            }
            else {
                return api.content.CompareStatusFormatter.formatStatus(this.status);
            }
        }
    }

    export class ContentVersionStatus {
        workspace: string;

        status: string;
    }
}