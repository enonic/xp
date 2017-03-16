import '../../api.ts';
import {ContentWizardActions} from './action/ContentWizardActions';

import Action = api.ui.Action;
import DialogButton = api.ui.dialog.DialogButton;
import SpanEl = api.dom.SpanEl;
import CompareStatus = api.content.CompareStatus;
import PublishStatus = api.content.PublishStatus;
import MenuButton = api.ui.button.MenuButton;
import ActionButton = api.ui.button.ActionButton;

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
    private contentCompareStatus: CompareStatus;
    private publishStatus: PublishStatus;
    private publishButtonForMobile: ActionButton;

    constructor(actions: ContentWizardActions) {
        super('toolbar-publish-controls');

        this.publishAction = actions.getPublishAction();
        this.publishAction.setIconClass('publish-action');
        this.publishTreeAction = actions.getPublishTreeAction();
        this.unpublishAction = actions.getUnpublishAction();
        this.publishMobileAction = actions.getPublishMobileAction();

        this.publishButton = new MenuButton(this.publishAction, [this.publishTreeAction, this.unpublishAction]);
        this.publishButton.addClass('content-wizard-toolbar-publish-button');

        this.contentStateSpan = new SpanEl('content-status');

        this.publishButtonForMobile = new ActionButton(this.publishMobileAction);
        this.publishButtonForMobile.addClass('mobile-edit-publish-button');
        this.publishButtonForMobile.setVisible(false);

        this.appendChildren(this.contentStateSpan, this.publishButton);
    }

    public setCompareStatus(compareStatus: CompareStatus, refresh: boolean = true): ContentWizardToolbarPublishControls {
        this.contentCompareStatus = compareStatus;
        if (refresh) {
            this.refreshState();
        }
        return this;
    }

    public setPublishStatus(publishStatus: PublishStatus, refresh: boolean = true): ContentWizardToolbarPublishControls {
        this.publishStatus = publishStatus;
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
        let canBeUnpublished = api.content.CompareStatusChecker.isPublished(this.contentCompareStatus) && this.userCanPublish;

        this.publishAction.setEnabled(canBePublished);
        this.publishTreeAction.setEnabled(canTreeBePublished);
        this.unpublishAction.setEnabled(canBeUnpublished);
        this.publishMobileAction.setEnabled(canBePublished);
        this.publishMobileAction.setVisible(canBePublished);

        this.contentStateSpan.setHtml(this.getContentStateValueForSpan(this.contentCompareStatus, this.publishStatus), false);
        this.publishButtonForMobile.setLabel('Publish ' + api.content.CompareStatusFormatter.formatStatus(this.contentCompareStatus) +
                                             ' item');
    }

    public isOnline(): boolean {
        return api.content.CompareStatusChecker.isOnline(this.contentCompareStatus);
    }

    public isPendingDelete(): boolean {
        return api.content.CompareStatusChecker.isPendingDelete(this.contentCompareStatus);
    }

    public enableActionsForExisting(existing: api.content.Content) {
        new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
            let hasPublishPermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.PUBLISH,
                loginResult, existing.getPermissions());
            this.setUserCanPublish(hasPublishPermission);
        });
    }

    private getContentStateValueForSpan(compareStatus: CompareStatus, publishStatus: PublishStatus): string {
        let status = new api.dom.SpanEl();
        if (compareStatus === CompareStatus.EQUAL) {
            status.addClass('online');
        }
        if (publishStatus && (publishStatus === PublishStatus.PENDING || publishStatus === PublishStatus.EXPIRED)) {
            status.addClass(api.content.PublishStatusFormatter.formatStatus(publishStatus).toLowerCase());
            status.setHtml(api.content.CompareStatusFormatter.formatStatus(compareStatus) + ' (' +
                           api.content.PublishStatusFormatter.formatStatus(publishStatus) + ')');
        } else {
            status.setHtml(api.content.CompareStatusFormatter.formatStatus(compareStatus));
        }
        return 'Item is ' + status.toString();
    }

    public getPublishButtonForMobile(): ActionButton {
        return this.publishButtonForMobile;
    }
}
