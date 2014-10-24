module app.browse.action {

    import Action = api.ui.Action;

    export class DuplicateContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Duplicate");
            this.setEnabled(false);
            this.onExecuted(() => {
                grid.getSelectedDataList().forEach((elem) => {
                    this.duplicate(elem.getContentSummary());
                });
            });
        }

        private duplicate(source: api.content.ContentSummary) {
            new api.content.DuplicateContentRequest(source.getContentId()).
                sendAndParse().then((content: api.content.Content) => {

                    api.notify.showFeedback('Content [' + source.getPath() + '] was duplicated!');
                    new api.content.ContentDuplicatedEvent(content, source).fire();
                })
        }
    }
}
