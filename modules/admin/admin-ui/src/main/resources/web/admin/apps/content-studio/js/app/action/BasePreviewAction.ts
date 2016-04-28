import "../../api.ts";

import Action = api.ui.Action;
import RenderingMode = api.rendering.RenderingMode;

export class BasePreviewAction extends Action {

    protected openWindows(contents: api.content.ContentSummary[]) { // should be called only in async block
        var targetWindows = this.openBlankWindows(contents);
        for (var key in targetWindows) {
            this.updateLocation(targetWindows[key], contents[key], false);
        }
    }

    protected openBlankWindows(contents: api.content.ContentSummary[]): any[] { // should be called only in async block
        return contents.map(content => this.openBlankWindow(content));
    }

    protected openBlankWindow(content: api.content.ContentSummary): any { // should be called only in async block
        return window.open('', content.getId());
    }

    protected updateLocation(targetWindow, content: api.content.ContentSummary, focus: boolean = true) {
        targetWindow.location.href = api.rendering.UriHelper.getPortalUri(content.getPath().toString(),
            RenderingMode.PREVIEW, api.content.Branch.DRAFT);
        if (focus) {
            targetWindow.focus(); // behavior depends on user settings for firefox
        }
    }
}
