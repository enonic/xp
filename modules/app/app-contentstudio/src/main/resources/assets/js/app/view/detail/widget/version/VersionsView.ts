import '../../../../../api.ts';
import {ContentVersionViewer} from './ContentVersionViewer';

import ContentVersion = api.content.ContentVersion;
import ContentId = api.content.ContentId;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import i18n = api.util.i18n;

export class VersionsView
    extends api.ui.selector.list.ListBox<ContentVersion> {

    private content: ContentSummaryAndCompareStatus;
    private loadedListeners: { (): void }[] = [];
    private activeVersion: ContentVersion;

    private static branchMaster: string = 'master';
    private static branchDraft: string = 'draft';

    constructor() {
        super('all-content-versions');
    }

    setContentData(item: ContentSummaryAndCompareStatus) {
        this.content = item;
    }

    getContentId(): ContentId {
        return this.content ? this.content.getContentId() : null;
    }

    getCompareStatus(): CompareStatus {
        return this.content ? this.content.getCompareStatus() : null;
    }

    reload(): wemQ.Promise<void> {
        return this.loadData().then((contentVersions: ContentVersion[]) => {
            this.updateView(contentVersions);
            this.notifyLoaded();
        });
    }

    createItemView(item: ContentVersion, readOnly: boolean): api.dom.Element {
        let itemContainer = new api.dom.LiEl('content-version-item');

        this.createStatusBlock(item, itemContainer);
        this.createDataBlocks(item, itemContainer);
        this.addOnClickHandler(itemContainer);

        return itemContainer;
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
        if (this.getContentId()) {
            return new api.content.resource.GetContentVersionsForViewRequest(this.getContentId()).sendAndParse().then(
                (contentVersions: api.content.ContentVersions) => {
                    this.activeVersion = contentVersions.getActiveVersion();
                    return contentVersions.getContentVersions();
                });
        } else {
            throw new Error('Required contentId not set for ActiveContentVersionsTreeGrid');
        }
    }

    private updateView(contentVersions: ContentVersion[]) {
        this.clearItems();
        this.setItems(contentVersions);
        this.getItemView(this.activeVersion).addClass('active');
    }

    private getStatus(contentVersion: ContentVersion): ContentVersionStatus {
        if (this.getCompareStatus() == null) {
            return null;
        }
        let result = null;

        let hasMaster = contentVersion.workspaces.some((workspace) => {
            return workspace === VersionsView.branchMaster;
        });

        contentVersion.workspaces.some((workspace: string) => {
            if (!hasMaster || workspace === VersionsView.branchMaster) {
                result = {workspace: workspace, status: this.getState(workspace)};
                return true;
            }
        });

        return result;
    }

    private getState(workspace: string): string {
        if (workspace === VersionsView.branchMaster) {
            return api.content.CompareStatusFormatter.formatStatus(api.content.CompareStatus.EQUAL);
        } else {
            return api.content.CompareStatusFormatter.formatStatusFromContent(this.content);
        }
    }

    private createStatusBlock(item: ContentVersion, itemEl: api.dom.Element) {
        let contentVersionStatus = this.getStatus(item);
        if (!!contentVersionStatus) {
            let statusDiv = new api.dom.DivEl('status ' + contentVersionStatus.workspace);
            statusDiv.setHtml(contentVersionStatus.status);
            itemEl.appendChild(statusDiv);
        }
    }

    private createDataBlocks(item: ContentVersion, itemEl: api.dom.Element) {
        let descriptionDiv = this.createDescriptionBlock(item);
        let versionInfoDiv = this.createVersionInfoBlock(item);
        let closeButton = this.createCloseButton();

        itemEl.appendChildren(closeButton, descriptionDiv, versionInfoDiv);
    }

    private createCloseButton(): api.dom.Element {
        return new api.dom.DivEl('close-version-info-button hidden');
    }

    private createDescriptionBlock(item: ContentVersion): api.dom.Element {
        let descriptionDiv = new ContentVersionViewer();
        descriptionDiv.addClass('description');
        descriptionDiv.setObject(item);
        return descriptionDiv;
    }

    private createVersionInfoBlock(item: ContentVersion): api.dom.Element {
        let versionInfoDiv = new api.dom.DivEl('version-info hidden');

        let timestampDiv = new api.dom.DivEl('version-info-timestamp');
        timestampDiv.appendChildren(new api.dom.SpanEl('label').setHtml(i18n('field.timestamp') + ': '),
            new api.dom.SpanEl().setHtml(api.ui.treegrid.DateTimeFormatter.createHtml(item.modified)));

        let versionIdDiv = new api.dom.DivEl('version-info-version-id');
        versionIdDiv.appendChildren(new api.dom.SpanEl('label').setHtml(i18n('field.version.id') + ': '),
            new api.dom.SpanEl().setHtml(item.id));

        let displayNameDiv = new api.dom.DivEl('version-info-display-name');
        displayNameDiv.appendChildren(new api.dom.SpanEl('label').setHtml(i18n('field.displayName') + ': '),
            new api.dom.SpanEl().setHtml(item.displayName));

        let isActive = item.id === this.activeVersion.id;
        let restoreButton = new api.ui.button.ActionButton(
            new api.ui.Action(isActive ? i18n('field.version.active') : i18n('field.version.restore'))
                .onExecuted((action: api.ui.Action) => {
                    if (!isActive) {
                        new api.content.resource.SetActiveContentVersionRequest(item.id, this.getContentId()).sendAndParse().then(
                            (contentId: ContentId) => {
                                api.notify.NotifyManager.get().showFeedback(i18n('notify.version.changed', item.id));
                                new api.content.event.ActiveContentVersionSetEvent(this.getContentId(), item.id).fire();
                            });
                    }
                }), false);

        if (isActive) {
            restoreButton.addClass('active');
        }

        restoreButton.onClicked((event: MouseEvent) => {
            event.preventDefault();
            event.stopPropagation();
        });

        versionInfoDiv.appendChildren(timestampDiv, versionIdDiv, displayNameDiv, restoreButton);

        return versionInfoDiv;
    }

    private addOnClickHandler(itemContainer: api.dom.Element) {
        itemContainer.onClicked(() => {
            this.collapseAllContentVersionItemViewsExcept(itemContainer);
            itemContainer.toggleClass('expanded');
        });
    }

    private collapseAllContentVersionItemViewsExcept(itemContainer: api.dom.Element) {
        wemjq(this.getHTMLElement()).find('.content-version-item').not(itemContainer.getHTMLElement()).removeClass('expanded');
    }
}

export class ContentVersionStatus {
    workspace: string;

    status: string;
}
