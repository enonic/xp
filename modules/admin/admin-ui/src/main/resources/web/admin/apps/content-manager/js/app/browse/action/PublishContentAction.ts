module app.browse.action {

    import Action = api.ui.Action;
    import ContentSummary = api.content.ContentSummary;
    import PublishContentRequest = api.content.PublishContentRequest;

    export class PublishContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Publish");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contents: api.content.ContentSummaryAndCompareStatus[]
                    = grid.getSelectedDataList();
                new ContentPublishPromptEvent(contents).fire();
            });
        }
    }
}