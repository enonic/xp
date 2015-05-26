module app.browse.action {

    import Action = api.ui.Action;
    import ContentSummary = api.content.ContentSummary;
    import PublishContentRequest = api.content.PublishContentRequest;

    export class PublishContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Publish");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new ContentPublishPromptEvent(contentSummaries).fire();
            });
        }
    }
}
