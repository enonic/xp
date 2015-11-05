module app.browse.action {

    import Action = api.ui.Action;

    export class DeleteContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contents: api.content.ContentSummaryAndCompareStatus[]
                    = grid.getSelectedDataList();
                new ContentDeletePromptEvent(contents).fire();
            });
        }
    }
}
