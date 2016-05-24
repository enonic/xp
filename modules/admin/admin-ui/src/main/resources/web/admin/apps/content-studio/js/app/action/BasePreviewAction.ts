import "../../api.ts";

import Action = api.ui.Action;
import RenderingMode = api.rendering.RenderingMode;

interface OpenedWindow {
    openedWindow: Window,
    isBlocked: boolean
}

export class BasePreviewAction extends Action {
    
    private notifyBlocked: () => void;
    
    constructor(label: string, shortcut?: string, global?: boolean) {
        super(label, shortcut, global);
        // Notification is shown not less than once in a minute, if triggered
        this.notifyBlocked = api.util.AppHelper.debounce(() => {
            const message = "Pop-up Blocker is enabled in browser settings! Please add selected sites to the exception list.";
            api.notify.showWarning(message, false);
        }, 60000, true);
    }

    private popupCheck(win: Window) {
        const isBlocked = !win || win.closed || typeof win.closed == "undefined";

        if (isBlocked) {
            this.notifyBlocked();
        }
        
        return isBlocked;
    }

    protected openWindows(contents: api.content.ContentSummary[]) { // should be called only in async block
        const targetWindows = this.openBlankWindows(contents);
        targetWindows.forEach((value, index) => {
            if (!value.isBlocked) {
                this.updateLocation(value.openedWindow, contents[index], false);
            }
        });
    }

    protected openBlankWindows(contents: api.content.ContentSummary[]): OpenedWindow[] { // should be called only in async block
        return contents.map(content => this.openBlankWindow(content));
    }

    protected openBlankWindow(content: api.content.ContentSummary): OpenedWindow { // should be called only in async block
        const openedWindow = window.open('', content.getId());
        const isBlocked = this.popupCheck(openedWindow);
        return { openedWindow, isBlocked };
    }

    protected updateLocation(targetWindow, content: api.content.ContentSummary, focus: boolean = true) {
        targetWindow.location.href = api.rendering.UriHelper.getPortalUri(content.getPath().toString(),
            RenderingMode.PREVIEW, api.content.Branch.DRAFT);
        if (focus) {
            targetWindow.focus(); // behavior depends on user settings for firefox
        }
    }
}
