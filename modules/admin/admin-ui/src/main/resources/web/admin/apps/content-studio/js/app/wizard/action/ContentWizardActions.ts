import "../../../api.ts";
import {ContentWizardPanel} from "../ContentWizardPanel";
import {DuplicateContentAction} from "./DuplicateContentAction";
import {DeleteContentAction} from "./DeleteContentAction";
import {PublishAction} from "./PublishAction";
import {PublishTreeAction} from "./PublishTreeAction";
import {UnpublishAction} from "./UnpublishAction";
import {PreviewAction} from "./PreviewAction";
import {ShowLiveEditAction} from "./ShowLiveEditAction";
import {ShowFormAction} from "./ShowFormAction";
import {ShowSplitEditAction} from "./ShowSplitEditAction";
import SaveAction = api.app.wizard.SaveAction;
import CloseAction = api.app.wizard.CloseAction;
import SaveAndCloseAction = api.app.wizard.SaveAndCloseAction;

export class ContentWizardActions extends api.app.wizard.WizardActions<api.content.Content> {

    private save: api.ui.Action;

    private close: api.ui.Action;

    private saveAndClose: api.ui.Action;

    private delete: api.ui.Action;

    private duplicate: api.ui.Action;

    private publish: api.ui.Action;

    private publishTree: api.ui.Action;

    private unpublish: api.ui.Action;

    private publishMobile:api.ui.Action;

    private preview: api.ui.Action;

    private showLiveEditAction: api.ui.Action;

    private showFormAction: api.ui.Action;

    private showSplitEditAction: api.ui.Action;

    private deleteOnlyMode: boolean = false;

    constructor(wizardPanel: ContentWizardPanel) {
        super(
            new SaveAction(wizardPanel, "Save draft"),
            new DeleteContentAction(wizardPanel),
            new DuplicateContentAction(wizardPanel),
            new PreviewAction(wizardPanel),
            new PublishAction(wizardPanel),
            new PublishTreeAction(wizardPanel),
            new UnpublishAction(wizardPanel),
            new CloseAction(wizardPanel),
            new ShowLiveEditAction(wizardPanel),
            new ShowFormAction(wizardPanel),
            new ShowSplitEditAction(wizardPanel),
            new SaveAndCloseAction(wizardPanel),
            new PublishAction(wizardPanel)
        );

        this.save = this.getActions()[0];
        this.delete = this.getActions()[1];
        this.duplicate = this.getActions()[2];
        this.preview = this.getActions()[3];
        this.publish = this.getActions()[4];
        this.publishTree = this.getActions()[5];
        this.unpublish = this.getActions()[6];
        this.close = this.getActions()[7];
        this.showLiveEditAction = this.getActions()[8];
        this.showFormAction = this.getActions()[9];
        this.showSplitEditAction = this.getActions()[10];
        this.saveAndClose = this.getActions()[11];
        this.publishMobile = this.getActions()[12];
    }

    enableActionsForNew() {
        this.save.setEnabled(true);
        this.delete.setEnabled(true);
    }

    enableActionsForExisting(existing: api.content.Content) {
        this.save.setEnabled(existing.isEditable());
        this.delete.setEnabled(existing.isDeletable());

        this.enableActionsForExistingByPermissions(existing);
    }

    setDeleteOnlyMode(content: api.content.Content, valueOn: boolean = true) {
        if (this.deleteOnlyMode == valueOn) {
            return;
        }
        this.deleteOnlyMode = valueOn;

        this.save.setEnabled(!valueOn);
        this.duplicate.setEnabled(!valueOn);
        this.publish.setEnabled(!valueOn);
        this.unpublish.setEnabled(!valueOn);
        this.publishMobile.setEnabled(!valueOn);
        this.publishMobile.setVisible(!valueOn);

        if (valueOn) {
            this.enableDeleteIfAllowed(content);
        } else {
            this.delete.setEnabled(true);
            this.enableActionsForExistingByPermissions(content);
        }
    }

    private enableDeleteIfAllowed(content: api.content.Content) {
        new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
            let hasDeletePermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.DELETE,
                loginResult, content.getPermissions());
            this.delete.setEnabled(hasDeletePermission);
        });
    }

    private enableActionsForExistingByPermissions(existing: api.content.Content) {
        new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {

            let hasModifyPermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.MODIFY,
                loginResult, existing.getPermissions());
            let hasDeletePermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.DELETE,
                loginResult, existing.getPermissions());
            let hasPublishPermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.PUBLISH,
                loginResult, existing.getPermissions());

            if (!hasModifyPermission) {
                this.save.setEnabled(false);
                this.saveAndClose.setEnabled(false);
            }
            if (!hasDeletePermission) {
                this.delete.setEnabled(false);
            }
            if (!hasPublishPermission) {
                this.publish.setEnabled(false);
                this.unpublish.setEnabled(false);
                this.publishTree.setEnabled(false);
                this.publishMobile.setEnabled(false);
                this.publishMobile.setVisible(false);
            } else {
                // check if already published to show unpublish button
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetchByContent(existing)
                    .then((contentAndCompare: api.content.ContentSummaryAndCompareStatus) => {

                        let status = contentAndCompare.getCompareStatus();
                        let isPublished = status !== api.content.CompareStatus.NEW &&
                                          status != api.content.CompareStatus.UNKNOWN;
                    });
            }

            if (existing.hasParent()) {
                new api.content.resource.GetContentByPathRequest(existing.getPath().getParentPath()).sendAndParse().then(
                    (parent: api.content.Content) => {
                        new api.content.resource.GetContentPermissionsByIdRequest(parent.getContentId()).sendAndParse().then(
                            (accessControlList: api.security.acl.AccessControlList) => {
                                let hasParentCreatePermission = api.security.acl.PermissionHelper.hasPermission(
                                    api.security.acl.Permission.CREATE,
                                    loginResult,
                                    accessControlList);

                                if (!hasParentCreatePermission) {
                                    this.duplicate.setEnabled(false);
                                }
                            });
                    });
            } else {
                new api.content.resource.GetContentRootPermissionsRequest().sendAndParse().then(
                    (accessControlList: api.security.acl.AccessControlList) => {
                        let hasParentCreatePermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.CREATE,
                            loginResult,
                            accessControlList);

                        if (!hasParentCreatePermission) {
                            this.duplicate.setEnabled(false);
                        }
                    });
            }

        });
    }

    getDeleteAction(): api.ui.Action {
        return this.delete;
    }

    getSaveAction(): api.ui.Action {
        return this.save;
    }

    getDuplicateAction(): api.ui.Action {
        return this.duplicate;
    }

    getCloseAction(): api.ui.Action {
        return this.close;
    }

    getPublishAction(): api.ui.Action {
        return this.publish;
    }

    getPublishTreeAction(): api.ui.Action {
        return this.publishTree;
    }

    getUnpublishAction(): api.ui.Action {
        return this.unpublish;
    }

    getPreviewAction(): api.ui.Action {
        return this.preview;
    }

    getShowLiveEditAction(): api.ui.Action {
        return this.showLiveEditAction;
    }

    getShowFormAction(): api.ui.Action {
        return this.showFormAction;
    }

    getShowSplitEditAction(): api.ui.Action {
        return this.showSplitEditAction;
    }

    getPublishMobileAction():api.ui.Action {
        return this.publishMobile;
    }
}
