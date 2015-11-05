module app.browse.action {

    import Action = api.ui.Action;

    export class EditContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Edit", "mod+e");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contents: api.content.ContentSummaryAndCompareStatus[]
                    = grid.getSelectedDataList();
                new api.content.EditContentEvent(contents).fire();
            });
        }
    }
}
