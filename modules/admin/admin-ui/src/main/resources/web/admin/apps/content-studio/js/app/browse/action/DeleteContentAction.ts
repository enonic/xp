module app.browse.action {

    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import CompareStatus = api.content.CompareStatus;
    import Action = api.ui.Action;

    export class DeleteContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contents: api.content.ContentSummaryAndCompareStatus[]
                    = grid.getSelectedDataList();
                new ContentDeletePromptEvent(contents)
                    .setNoCallback(null)
                    .setYesCallback(() => {
                        var excludeStatuses = [CompareStatus.EQUAL, CompareStatus.PENDING_DELETE, CompareStatus.NEWER],
                        deselected = [];
                        grid.getSelectedDataList().forEach((content: ContentSummaryAndCompareStatus) => {
                            if (excludeStatuses.indexOf(content.getCompareStatus()) < 0) {
                                deselected.push(content.getId());
                            }
                        });
                    grid.deselectNodes(deselected);
                    }).fire();
            });
        }
    }
}
