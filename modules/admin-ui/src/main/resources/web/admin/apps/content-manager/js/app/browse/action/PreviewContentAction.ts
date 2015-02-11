module app.browse.action {

    import Action = api.ui.Action;
    import RenderingMode = api.rendering.RenderingMode;
    import ContentSummary = api.content.ContentSummary;

    export class PreviewContentAction extends Action {

        constructor(grid: ContentTreeGrid) {
            super("Preview", "");
            this.setEnabled(false);
            this.onExecuted(() => {
                var contentSummaries: ContentSummary[]
                    = grid.getSelectedDataList().map((elem) => {
                    return elem.getContentSummary();
                });
                this.showPreviewDialog(contentSummaries[0]);
            });
        }

        showPreviewDialog(content: api.content.ContentSummary) {
            window.open(api.rendering.UriHelper.getPortalUri(content.getPath().toString(), RenderingMode.PREVIEW,
                api.content.Workspace.DRAFT), 'preview').focus();
        }
    }
}
