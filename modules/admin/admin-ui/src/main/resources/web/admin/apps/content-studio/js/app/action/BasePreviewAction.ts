import "../../api.ts";

import Action = api.ui.Action;
import RenderingMode = api.rendering.RenderingMode;

interface OpenedWindow {
    openedWindow: Window;
    isBlocked: boolean;
}

export class BasePreviewAction extends Action {

    private notifyBlocked: () => void;

    constructor(label: string, shortcut?: string, global?: boolean) {
        super(label, shortcut, global);
        // Notification is shown not less than once in a minute, if triggered
        this.notifyBlocked = api.util.AppHelper.debounce(() => {
            const message = 'Pop-up Blocker is enabled in browser settings! Please add selected sites to the exception list.';
            api.notify.showWarning(message, false);
        }, 60000, true);
    }

    private popupCheck(win: Window) {
        const isBlocked = !win || win.closed || typeof win.closed == 'undefined';

        if (isBlocked) {
            this.notifyBlocked();
        }

        return isBlocked;
    }

    // should be called only in async block
    protected openWindow(content: api.content.ContentSummary) {
        const targetWindow = this.openBlankWindow(content);
        if (!targetWindow.isBlocked) {
            this.updateLocation(targetWindow.openedWindow, content, false);
        }
    }

    // should be called only in async block
    protected openWindows(contents: api.content.ContentSummary[]) {
        contents.forEach((content) => this.openWindow(content));
    }

    // should be called only in async block
    protected openBlankWindow(content: api.content.ContentSummary): OpenedWindow {
        const openedWindow = window.open('', content.getId());
        const isBlocked = this.popupCheck(openedWindow);
        return {openedWindow, isBlocked};
    }

    protected updateLocation(targetWindow: Window, content: api.content.ContentSummary, focus: boolean = true) {
        targetWindow.location.href = api.rendering.UriHelper.getPortalUri(content.getPath().toString(),
            RenderingMode.PREVIEW, api.content.Branch.DRAFT);
        if (focus) {
            targetWindow.focus(); // behavior depends on user settings for firefox
        }
    }
}
