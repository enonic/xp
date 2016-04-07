module app.wizard.action {

    import ContentId = api.content.ContentId;
    import ContentPath = api.content.ContentPath;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class DeleteContentAction extends api.ui.Action {

        constructor(wizardPanel: app.wizard.ContentWizardPanel) {
            super("Delete", "mod+del", true);
            this.onExecuted(() => {
                new app.remove.ContentDeleteDialog()
                    .setContentToDelete([new ContentSummaryAndCompareStatus().
                                            setContentSummary(wizardPanel.getPersistedItem()).
                                            setCompareStatus(wizardPanel.getContentCompareStatus())
                                        ])
                    .open();
            });
        }
    }

}
