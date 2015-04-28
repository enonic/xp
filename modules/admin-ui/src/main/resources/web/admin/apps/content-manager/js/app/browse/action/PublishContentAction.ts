module app.browse.action {

    import Action = api.ui.Action;
    import ContentSummary = api.content.ContentSummary;
    import PublishContentRequest = api.content.PublishContentRequest;

    export class PublishContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Publish");
            this.setEnabled(false);
            this.onExecuted(() => {
                /*new PublishContentRequest().setIds(grid.getSelectedDataList().map((el) => {
                    return new api.content.ContentId(el.getContentSummary().getId());
                })).send().done((jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) => {
                    PublishContentRequest.feedback(jsonResponse);
                 });*/
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new ContentPublishPromptEvent(contentSummaries).fire();
            });
        }
    }
}
