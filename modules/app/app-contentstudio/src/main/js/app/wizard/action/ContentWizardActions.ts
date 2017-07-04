import '../../../api.ts';
import {ContentWizardPanel} from '../ContentWizardPanel';
import {DuplicateContentAction} from './DuplicateContentAction';
import {DeleteContentAction} from './DeleteContentAction';
import {PublishAction} from './PublishAction';
import {PublishTreeAction} from './PublishTreeAction';
import {CreateIssueAction} from './CreateIssueAction';
import {UnpublishAction} from './UnpublishAction';
import {PreviewAction} from './PreviewAction';
import {ShowLiveEditAction} from './ShowLiveEditAction';
import {ShowFormAction} from './ShowFormAction';
import {ShowSplitEditAction} from './ShowSplitEditAction';
import {UndoPendingDeleteAction} from './UndoPendingDeleteAction';
import Action = api.ui.Action;
import SaveAction = api.app.wizard.SaveAction;
import CloseAction = api.app.wizard.CloseAction;
import SaveAndCloseAction = api.app.wizard.SaveAndCloseAction;
import i18n = api.util.i18n;

export class ContentWizardActions extends api.app.wizard.WizardActions<api.content.Content> {

    private save: Action;

    private close: Action;

    private saveAndClose: Action;

    private delete: Action;

    private duplicate: Action;

    private publish: Action;

    private publishTree: Action;

    private createIssue: Action;

    private unpublish: Action;

    private publishMobile:Action;

    private preview: Action;

    private showLiveEditAction: Action;

    private showFormAction: Action;

    private showSplitEditAction: Action;

    private undoPendingDelete: Action;

    private deleteOnlyMode: boolean = false;

    private wizardPanel: ContentWizardPanel;

    constructor(wizardPanel: ContentWizardPanel) {
        super(
            new SaveAction(wizardPanel, i18n('action.saveDraft')),
            new DeleteContentAction(wizardPanel),
            new DuplicateContentAction(wizardPanel),
            new PreviewAction(wizardPanel),
            new PublishAction(wizardPanel),
            new PublishTreeAction(wizardPanel),
            new CreateIssueAction(wizardPanel),
            new UnpublishAction(wizardPanel),
            new CloseAction(wizardPanel),
            new ShowLiveEditAction(wizardPanel),
            new ShowFormAction(wizardPanel),
            new ShowSplitEditAction(wizardPanel),
            new SaveAndCloseAction(wizardPanel),
            new PublishAction(wizardPanel),
            new UndoPendingDeleteAction(wizardPanel)
        );

        this.wizardPanel = wizardPanel;

        [
            this.save,
            this.delete,
            this.duplicate,
            this.preview,
            this.publish,
            this.publishTree,
            this.createIssue,
            this.unpublish,
            this.close,
            this.showLiveEditAction,
            this.showFormAction,
            this.showSplitEditAction,
            this.saveAndClose,
            this.publishMobile,
            this.undoPendingDelete,
        ] = this.getActions();
    }

    refreshPendingDeleteDecorations() {
        let compareStatus = this.wizardPanel.getCompareStatus();
        let isPendingDelete = api.content.CompareStatusChecker.isPendingDelete(compareStatus);

        this.undoPendingDelete.setVisible(isPendingDelete);
        [
            this.save,
            this.delete,
            this.duplicate,
            this.unpublish
        ].forEach(action => action.setVisible(!isPendingDelete));

        this.preview.setVisible(this.preview.isEnabled() && !isPendingDelete);
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
        if (this.deleteOnlyMode === valueOn) {
            return;
        }
        this.deleteOnlyMode = valueOn;

        this.save.setEnabled(!valueOn);
        this.duplicate.setEnabled(!valueOn);
        this.publish.setEnabled(!valueOn);
        this.createIssue.setEnabled(!valueOn);
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
                this.createIssue.setEnabled(true);
                this.unpublish.setEnabled(false);
                this.publishTree.setEnabled(false);
                this.publishMobile.setEnabled(false);
                this.publishMobile.setVisible(false);
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

    getDeleteAction(): Action {
        return this.delete;
    }

    getSaveAction(): Action {
        return this.save;
    }

    getDuplicateAction(): Action {
        return this.duplicate;
    }

    getCloseAction(): Action {
        return this.close;
    }

    getPublishAction(): Action {
        return this.publish;
    }

    getPublishTreeAction(): Action {
        return this.publishTree;
    }

    getCreateIssueAction(): Action {
        return this.createIssue;
    }

    getUnpublishAction(): Action {
        return this.unpublish;
    }

    getPreviewAction(): Action {
        return this.preview;
    }

    getShowLiveEditAction(): Action {
        return this.showLiveEditAction;
    }

    getShowFormAction(): Action {
        return this.showFormAction;
    }

    getShowSplitEditAction(): Action {
        return this.showSplitEditAction;
    }

    getPublishMobileAction():Action {
        return this.publishMobile;
    }

    getUndoPendingDeleteAction(): Action {
        return this.undoPendingDelete;
    }
}
