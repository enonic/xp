module app.browse.action {

    import Action = api.ui.Action;

    export class ShowNewContentDialogAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("New", "alt+n");
            this.setEnabled(true);
            this.onExecuted(() => {
                var contents: api.content.ContentSummaryAndCompareStatus[]
                    = grid.getSelectedDataList();
                new ShowNewContentDialogEvent(contents.length > 0 ? contents[0] : null).fire();
            });
        }
    }
}
