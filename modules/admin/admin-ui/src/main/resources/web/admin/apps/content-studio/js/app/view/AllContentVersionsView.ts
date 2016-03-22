module app.view {

    import ContentVersion = api.content.ContentVersion;
    import ContentId = api.content.ContentId;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class AllContentVersionsView extends api.ui.selector.list.ListBox<ContentVersion> {

        private contentId: ContentId;
        private status: api.content.CompareStatus;
        private loadedListeners: {(): void}[] = [];
        private activeVersion: ContentVersion;

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
            var itemEl = new api.dom.LiEl("content-version-item");

            var contentVersionStatus = this.getStatus(item);
            if(!!contentVersionStatus) {
                var statusDiv = new api.dom.DivEl("status " + contentVersionStatus.workspace);
                statusDiv.setHtml(contentVersionStatus.status);
                itemEl.appendChild(statusDiv);
            }

            var descriptionDiv = this.createDescriptionBlock(item),
                versionInfoDiv = this.createVersionInfoBlock(item),
                closeButton = this.createCloseButton(versionInfoDiv);

            itemEl.appendChildren(closeButton, descriptionDiv, versionInfoDiv);

            itemEl.onClicked(() => {
               versionInfoDiv.removeClass("hidden");
               closeButton.removeClass("hidden");
            });

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
                spread<ContentVersion[]>((allVersions: ContentVersion[], activeVersions: ContentVersion[]) => {
                    this.activeVersion = activeVersions[0];
                    return this.enrichWithWorkspaces(allVersions, activeVersions);
                });
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
            this.setActiveVersion();
        }

        private setActiveVersion() {
            this.getItemView(this.activeVersion).addClass("active");
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
                   return true;
                }
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

        private createCloseButton(elementToHide: api.dom.Element): api.dom.Element {
            var closeButton = new api.dom.DivEl("close-version-info-button hidden");
            closeButton.onClicked((event) => {
                elementToHide.addClass("hidden");
                closeButton.addClass("hidden");
                event.preventDefault();
                event.stopPropagation();
            });

            return closeButton;
        }

        private createDescriptionBlock(item: ContentVersion): api.dom.Element {
            var descriptionDiv = new api.content.ContentVersionViewer();
            descriptionDiv.addClass("description");
            descriptionDiv.setObject(item);
            return descriptionDiv;
        }

        private createVersionInfoBlock(item: ContentVersion): api.dom.Element {
            var versionInfoDiv = new api.dom.DivEl("version-info hidden");

            var timestampDiv = new api.dom.DivEl("version-info-timestamp");
            timestampDiv.appendChildren(new api.dom.SpanEl("label").setHtml("Timestamp: "), new api.dom.SpanEl().setHtml(api.util.DateHelper.formatUTCDateTime(item.modified)));

            var versionIdDiv = new api.dom.DivEl("version-info-version-id");
            versionIdDiv.appendChildren(new api.dom.SpanEl("label").setHtml("VersionId: "), new api.dom.SpanEl().setHtml(item.id));

            var displayNameDiv = new api.dom.DivEl("version-info-display-name");
            displayNameDiv.appendChildren(new api.dom.SpanEl("label").setHtml("Display Name: "), new api.dom.SpanEl().setHtml(item.displayName));

            var isActive = item.id === this.activeVersion.id;
            var restoreButton = new api.ui.button.ActionButton(new api.ui.Action( isActive ? "This version is active" : "Restore this version").onExecuted((action: api.ui.Action) => {
                //restore version
            }), false);

            if(isActive) {
                restoreButton.addClass("active");
                restoreButton.setEnabled(false);
            }

            versionInfoDiv.appendChildren(timestampDiv, versionIdDiv, displayNameDiv, restoreButton);

            return versionInfoDiv;
        }
    }

    export class ContentVersionStatus {
        workspace: string;

        status: string;
    }
}