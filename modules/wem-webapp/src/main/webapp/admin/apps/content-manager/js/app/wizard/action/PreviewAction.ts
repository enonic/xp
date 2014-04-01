module app.wizard.action {

    import RenderingMode = api.util.RenderingMode;

    export class PreviewAction extends api.ui.Action {

        private static preview: app.view.ContentItemPreviewPanel;

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
            window.open(api.util.getPortalUri(content.getPath().toString(), RenderingMode.PREVIEW), 'preview');
        }

    }

}
