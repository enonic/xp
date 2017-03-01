import '../../api.ts';

import Action = api.ui.Action;
import DialogButton = api.ui.dialog.DialogButton;
import SpanEl = api.dom.SpanEl;
import CompareStatus = api.content.CompareStatus;
import PublishStatus = api.content.PublishStatus;
import MenuButton = api.ui.button.MenuButton;
import ActionButton = api.ui.button.ActionButton;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class ContentWizardToolbarPublishControls extends api.dom.DivEl {

    private publishButton: MenuButton;
    private publishAction: Action;
    private publishTreeAction: Action;
    private unpublishAction: Action;
    private publishMobileAction: Action;
    private contentStateSpan: SpanEl;
    private contentCanBePublished: boolean = false;
    private userCanPublish: boolean = true;
    private leafContent: boolean = true;
    private content: ContentSummaryAndCompareStatus;
    private publishButtonForMobile: ActionButton;

    constructor(publish: Action, publishTree: Action, unpublish: Action, publishMobile: Action) {
        super('toolbar-publish-controls');

        this.publishAction = publish;
        this.publishAction.setIconClass('publish-action');
        this.publishTreeAction = publishTree;
        this.unpublishAction = unpublish;
        this.publishMobileAction = publishMobile;

        this.publishButton = new MenuButton(publish, [publishTree, unpublish]);
        this.publishButton.addClass('content-wizard-toolbar-publish-button');

        this.contentStateSpan = new SpanEl('content-status');

        this.publishButtonForMobile = new ActionButton(publishMobile);
        this.publishButtonForMobile.addClass('mobile-edit-publish-button');
        this.publishButtonForMobile.setVisible(false);

        this.appendChildren(this.contentStateSpan, this.publishButton);
    }

    public setContent(content: ContentSummaryAndCompareStatus, refresh: boolean = true): ContentWizardToolbarPublishControls {
        this.content = content;
        if (refresh) {
            this.refreshState();
        }
        return this;
    }

    public setContentCanBePublished(value: boolean, refresh: boolean = true): ContentWizardToolbarPublishControls {
        this.contentCanBePublished = value;
        if (refresh) {
            this.refreshState();
        }
        return this;
    }

    public setUserCanPublish(value: boolean, refresh: boolean = true): ContentWizardToolbarPublishControls {
        this.userCanPublish = value;
        if (refresh) {
            this.refreshState();
        }
        return this;
    }

    public setLeafContent(leafContent: boolean, refresh: boolean = true): ContentWizardToolbarPublishControls {
        this.leafContent = leafContent;
        if (refresh) {
            this.refreshState();
        }
        return this;
    }

    private refreshState() {
        let canBePublished = !this.isOnline() && this.contentCanBePublished && this.userCanPublish;
        let canTreeBePublished = !this.leafContent && this.contentCanBePublished && this.userCanPublish;
        let canBeUnpublished = this.getCompareStatus() !== CompareStatus.NEW && this.getCompareStatus() !== CompareStatus.UNKNOWN &&
                               this.userCanPublish;

        this.publishAction.setEnabled(canBePublished);
        this.publishTreeAction.setEnabled(canTreeBePublished);
        this.unpublishAction.setEnabled(canBeUnpublished);
        this.publishMobileAction.setEnabled(canBePublished);
        this.publishMobileAction.setVisible(canBePublished);

        this.contentStateSpan.setHtml(this.getContentStateValueForSpan(this.content), false);
        this.publishButtonForMobile.setLabel('Publish ' + api.content.CompareStatusFormatter.formatStatusFromContent(this.content) +
                                             ' item');
    }

    public isOnline(): boolean {
        return this.getCompareStatus() === CompareStatus.EQUAL;
    }

    public isPendingDelete(): boolean {
        return this.getCompareStatus() === CompareStatus.PENDING_DELETE;
    }

    private getCompareStatus(): CompareStatus {
        return this.content ? this.content.getCompareStatus() : null;
    }

    private getPublishStatus(): PublishStatus {
        return this.content ? this.content.getPublishStatus() : null;
    }

    public enableActionsForExisting(existing: api.content.Content) {
        new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
            let hasPublishPermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.PUBLISH,
                loginResult, existing.getPermissions());
            this.setUserCanPublish(hasPublishPermission);
        });
    }

    private getContentStateValueForSpan(content: ContentSummaryAndCompareStatus): string {

        const publishStatus: PublishStatus = this.getPublishStatus();

        let status = new api.dom.SpanEl();
        if (this.getCompareStatus() === CompareStatus.EQUAL) {
            status.addClass('online');
        }
        if (publishStatus && (publishStatus === PublishStatus.PENDING || publishStatus === PublishStatus.EXPIRED)) {
            status.addClass(api.content.PublishStatusFormatter.formatStatus(publishStatus).toLowerCase());
            status.setHtml(api.content.CompareStatusFormatter.formatStatusFromContent(content) + ' (' +
                           api.content.PublishStatusFormatter.formatStatus(publishStatus) + ')');
        } else {
            status.setHtml(api.content.CompareStatusFormatter.formatStatusFromContent(content));
        }
        return 'Item is ' + status.toString();
    }

    public getPublishButtonForMobile(): ActionButton {
        return this.publishButtonForMobile;
    }
}
