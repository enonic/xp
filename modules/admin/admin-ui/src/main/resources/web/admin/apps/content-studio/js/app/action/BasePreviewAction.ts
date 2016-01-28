module app.action {

    import Action = api.ui.Action;
    import RenderingMode = api.rendering.RenderingMode;

    export class BasePreviewAction extends Action {

        protected openWindow(content: api.content.ContentSummary, focus: boolean = true) { // should be called only in async block
            var targetWindow = this.openBlankWindow(content);
            this.updateLocation(targetWindow, content, focus);
        }

        protected openBlankWindow(content: api.content.ContentSummary) { // should be called only in async block
            return window.open('', content.getId());
        }

        protected updateLocation(targetWindow, content: api.content.ContentSummary, focus: boolean = true) {
            targetWindow.location.href = api.rendering.UriHelper.getPortalUri(content.getPath().toString(),
                RenderingMode.PREVIEW, api.content.Branch.DRAFT);
            if(focus) {
                targetWindow.focus(); // behavior depends on user settings for firefox
            }
        }
    }
}
