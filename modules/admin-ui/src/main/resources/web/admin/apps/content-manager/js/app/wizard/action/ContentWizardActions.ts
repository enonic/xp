module app.wizard.action {

    export class ContentWizardActions implements api.app.wizard.WizardActions<api.content.Content> {

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

        private actions: api.ui.Action[];

        constructor(wizardPanel: app.wizard.ContentWizardPanel) {
            this.save = new api.app.wizard.SaveAction(wizardPanel);
            this.duplicate = new DuplicateContentAction(wizardPanel);
            this.delete = new DeleteContentAction(wizardPanel);
            this.close = new api.app.wizard.CloseAction(wizardPanel);
            this.saveAndClose = new api.app.wizard.SaveAndCloseAction(wizardPanel);
            this.publish = new PublishAction(wizardPanel);
            this.preview = new PreviewAction(wizardPanel);
            this.showLiveEditAction = new ShowLiveEditAction(wizardPanel);
            this.showFormAction = new ShowFormAction(wizardPanel);
            this.showSplitEditAction = new ShowSplitEditAction(wizardPanel);
            this.actions = [
                this.save, this.duplicate, this.delete,
                this.close, this.publish, this.preview,
                this.showLiveEditAction, this.showFormAction,
                this.showSplitEditAction, this.saveAndClose
            ];
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

        getActions(): api.ui.Action[] {
            return this.actions;
        }
    }
}
