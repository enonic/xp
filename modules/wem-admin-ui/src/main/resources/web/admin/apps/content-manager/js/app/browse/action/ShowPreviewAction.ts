module app.browse.action {

    import Action = api.ui.Action;

    export class ShowPreviewAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("PREVIEW");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new ShowPreviewEvent(contentSummaries).fire();
            });
        }
    }
}
