module app.wizard.action {

    export class PreviewAction extends api.ui.Action {

        private static dialog: api.ui.dialog.ModalDialog;
        private static preview: app.browse.ContentItemPreviewPanel;

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Preview");
            this.addExecutionListener(() => {
                    if (wizard.hasUnsavedChanges()) {
                        wizard.setPersistAsDraft(false);
                        wizard.updatePersistedItem().done(this.showPreviewDialog);
                    } else {
                        this.showPreviewDialog(wizard.getPersistedItem());
                    }
                }
            );
        }

        showPreviewDialog(content: api.content.Content) {

            if (!PreviewAction.dialog) {
                var title = content.getDisplayName() + ' preview';
                var dialogConfig = {
                    title: title,
                    height: 600,
                    width: 800
                };
                var dialog = new api.ui.dialog.ModalDialog(dialogConfig);
                dialog.setCancelAction(new api.ui.Action('Close', 'esc').addExecutionListener(() => {
                    dialog.close();
                }));
                dialog.addClass('wizard-preview-dialog');
                api.dom.Body.get().appendChild(dialog);

                var preview = new app.browse.ContentItemPreviewPanel();
                dialog.appendChildToContentPanel(preview);

                PreviewAction.dialog = dialog;
                PreviewAction.preview = preview;
            }

            PreviewAction.preview.setItem(new api.app.view.ViewItem(content).setPath(content.getPath().toString()));
            PreviewAction.dialog.open();
        }
    }

}
