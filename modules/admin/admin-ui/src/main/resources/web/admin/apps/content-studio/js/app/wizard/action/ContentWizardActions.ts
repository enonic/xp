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

export class ContentWizardActions extends api.app.wizard.WizardActions<api.content.Content> {

    private save: api.ui.Action;

    private close: api.ui.Action;

    private saveAndClose: api.ui.Action;

    private delete: api.ui.Action;

    private duplicate: api.ui.Action;

    private publish: api.ui.Action;
    
    private publishTree: api.ui.Action;

    private unpublish: api.ui.Action;

    private preview: api.ui.Action;

    private showLiveEditAction: api.ui.Action;

    private showFormAction: api.ui.Action;

    private showSplitEditAction: api.ui.Action;

    private deleteOnlyMode: boolean = false;

    constructor(wizardPanel: ContentWizardPanel) {
        this.save = new api.app.wizard.SaveAction(wizardPanel, "Save draft");
        this.duplicate = new DuplicateContentAction(wizardPanel);
        this.delete = new DeleteContentAction(wizardPanel);
        this.close = new api.app.wizard.CloseAction(wizardPanel);
        this.saveAndClose = new api.app.wizard.SaveAndCloseAction(wizardPanel);
        this.publish = new PublishAction(wizardPanel);
        this.publishTree = new PublishTreeAction(wizardPanel);
        this.unpublish = new UnpublishAction(wizardPanel);
        this.preview = new PreviewAction(wizardPanel);
        this.showLiveEditAction = new ShowLiveEditAction(wizardPanel);
        this.showFormAction = new ShowFormAction(wizardPanel);
        this.showSplitEditAction = new ShowSplitEditAction(wizardPanel);

        super(this.save, this.delete, this.duplicate, this.preview, 
            this.publish, this.publishTree, this.unpublish, this.close,
            this.showLiveEditAction, this.showFormAction, 
            this.showSplitEditAction, this.saveAndClose);
    }

    enableActionsForNew() {
        this.save.setEnabled(true);
        this.delete.setEnabled(true)
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

        if (valueOn) {
            this.enableDeleteIfAllowed(content);
        }
        else {
            this.delete.setEnabled(true);
            this.enableActionsForExistingByPermissions(content);
        }
    }

    private enableDeleteIfAllowed(content: api.content.Content) {
        new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
            var hasDeletePermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.DELETE,
                loginResult, content.getPermissions());
            this.delete.setEnabled(hasDeletePermission);
        });
    }

    private enableActionsForExistingByPermissions(existing: api.content.Content) {
        new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {

            var hasModifyPermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.MODIFY,
                loginResult, existing.getPermissions());
            var hasDeletePermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.DELETE,
                loginResult, existing.getPermissions());
            var hasPublishPermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.PUBLISH,
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
                this.publishTree.setEnabled(false);
            } else {
                // check if already published to show unpublish button
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetchByContent(existing)
                    .then((contentAndCompare: api.content.ContentSummaryAndCompareStatus) => {

                        var status = contentAndCompare.getCompareStatus();
                        var isPublished = status !== api.content.CompareStatus.NEW &&
                                          status != api.content.CompareStatus.UNKNOWN;

                        this.unpublish.setVisible(isPublished);
                    });
            }

            if (existing.hasParent()) {
                new api.content.resource.GetContentByPathRequest(existing.getPath().getParentPath()).sendAndParse().then(
                    (parent: api.content.Content) => {
                        new api.content.resource.GetContentPermissionsByIdRequest(parent.getContentId()).sendAndParse().then(
                            (accessControlList: api.security.acl.AccessControlList) => {
                                var hasParentCreatePermission = api.security.acl.PermissionHelper.hasPermission(
                                    api.security.acl.Permission.CREATE,
                                    loginResult,
                                    accessControlList);

                                if (!hasParentCreatePermission) {
                                    this.duplicate.setEnabled(false);
                                }
                            })
                    })
            } else {
                new api.content.resource.GetContentRootPermissionsRequest().sendAndParse().then(
                    (accessControlList: api.security.acl.AccessControlList) => {
                        var hasParentCreatePermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.CREATE,
                            loginResult,
                            accessControlList);

                        if (!hasParentCreatePermission) {
                            this.duplicate.setEnabled(false);
                        }
                    })
            }

        })
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
}
