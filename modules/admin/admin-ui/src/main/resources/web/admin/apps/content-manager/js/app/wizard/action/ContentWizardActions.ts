module app.wizard.action {

    export class ContentWizardActions extends api.app.wizard.WizardActions<api.content.Content> {

        private save: api.ui.Action;

        private close: api.ui.Action;

        private saveAndClose: api.ui.Action;

        private delete: api.ui.Action;

        private duplicate: api.ui.Action;

        private publish: api.ui.Action;

        private preview: api.ui.Action;

        private showLiveEditAction: api.ui.Action;

        private showFormAction: api.ui.Action;

        private showSplitEditAction: api.ui.Action;

        constructor(wizardPanel: app.wizard.ContentWizardPanel) {
            this.save = new api.app.wizard.SaveAction(wizardPanel, "Save draft");
            this.duplicate = new DuplicateContentAction(wizardPanel);
            this.delete = new DeleteContentAction(wizardPanel);
            this.close = new api.app.wizard.CloseAction(wizardPanel);
            this.saveAndClose = new api.app.wizard.SaveAndCloseAction(wizardPanel);
            this.publish = new PublishAction(wizardPanel);
            this.preview = new PreviewAction(wizardPanel);
            this.showLiveEditAction = new ShowLiveEditAction(wizardPanel);
            this.showFormAction = new ShowFormAction(wizardPanel);
            this.showSplitEditAction = new ShowSplitEditAction(wizardPanel);

            super(this.save, this.delete, this.duplicate,
                this.preview, this.publish, this.close,
                this.showLiveEditAction, this.showFormAction,
                this.showSplitEditAction, this.saveAndClose);
        }

        enableActionsForNew() {
            this.save.setEnabled(true);
            this.duplicate.setEnabled(false);
            this.delete.setEnabled(true)
        }

        enableActionsForExisting(existing: api.content.Content) {
            this.save.setEnabled(existing.isEditable());
            this.duplicate.setEnabled(true);
            this.delete.setEnabled(existing.isDeletable());

            this.enableActionsForExistingByPermissions(existing);
        }

        private enableActionsForExistingByPermissions(existing: api.content.Content) {
            new api.security.auth.IsAuthenticatedRequest().
                sendAndParse().
                then((loginResult: api.security.auth.LoginResult) => {

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
                    }

                    if (existing.hasParent()) {
                        new api.content.GetContentByPathRequest(existing.getPath().getParentPath()).
                            sendAndParse().
                            then((parent: api.content.Content) => {
                                new api.content.GetContentPermissionsByIdRequest(parent.getContentId()).
                                    sendAndParse().
                                    then((accessControlList: api.security.acl.AccessControlList) => {
                                        var hasParentCreatePermission = api.security.acl.PermissionHelper.hasPermission(api.security.acl.Permission.CREATE,
                                            loginResult,
                                            accessControlList);

                                        if (!hasParentCreatePermission) {
                                            this.duplicate.setEnabled(false);
                                        }
                                    })
                            })
                    } else {
                        new api.content.GetContentRootPermissionsRequest().
                            sendAndParse().
                            then((accessControlList: api.security.acl.AccessControlList) => {
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
}
