module app.browse.action {

    import Action = api.ui.Action;

    export class OpenContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Open", "mod+o");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: api.content.ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                new ViewContentEvent(contentSummaries).fire();
            });
        }
    }
}
