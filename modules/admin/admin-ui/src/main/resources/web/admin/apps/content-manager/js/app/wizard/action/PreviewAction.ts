module app.wizard.action {

    import RenderingMode = api.rendering.RenderingMode;

    export class PreviewAction extends api.ui.Action {

        private static preview: app.view.ContentItemPreviewPanel;

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Preview");
            this.onExecuted(() => {
                    if (wizard.hasUnsavedChanges()) {
                        wizard.setRequireValid(true);
                        var previewDialog = window.open('', 'preview'); // opening preview this way because
                                                                        // if open in q.then(...) browser sees window as pop-up
                        wizard.updatePersistedItem().
                            then((content) => {
                                previewDialog.location.href = this.getPreviewDialogURI(content);
                            }).
                            catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                            done();
                    } else {
                        this.showPreviewDialog(wizard.getPersistedItem());
                    }
                }
            );
        }

        showPreviewDialog(content: api.content.Content) {
            window.open(this.getPreviewDialogURI(content), 'preview');
        }

        private getPreviewDialogURI(content: api.content.Content): string {
            return api.rendering.UriHelper.getPortalUri(content.getPath().toString(), RenderingMode.PREVIEW,
                api.content.Branch.DRAFT);
        }

    }

}
