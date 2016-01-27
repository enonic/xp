module app.wizard.action {

    import RenderingMode = api.rendering.RenderingMode;

    export class PreviewAction extends app.action.BasePreviewAction {

        constructor(wizard: app.wizard.ContentWizardPanel) {
            super("Preview");
            this.onExecuted(() => {
                var previewWindow = this.openBlankWindow(wizard.getPersistedItem());
                if (wizard.hasUnsavedChanges()) {
                        wizard.setRequireValid(true);
                        wizard.saveChanges().
                            then(content => this.updateLocation(previewWindow, content) ).
                            catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                            done();
                    } else {
                        this.updateLocation(previewWindow, wizard.getPersistedItem());
                    }
                }
            );
        }
    }
}
