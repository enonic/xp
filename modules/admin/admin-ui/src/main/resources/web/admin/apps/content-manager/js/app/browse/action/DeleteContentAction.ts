module app.browse.action {

    import Action = api.ui.Action;

    export class DeleteContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new ContentDeletePromptEvent(contentSummaries).fire();
            });
        }
    }
}
