module app.browse.action {

    import Action = api.ui.Action;
    import RenderingMode = api.rendering.RenderingMode;
    import ContentSummary = api.content.ContentSummary;

    export class PreviewContentAction extends app.action.BasePreviewAction {

        constructor(grid: ContentTreeGrid) {
            super("Preview", "");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                }),
                previewContent = contentSummaries[0];

                this.openWindow(previewContent);
            });
        }
    }
}
