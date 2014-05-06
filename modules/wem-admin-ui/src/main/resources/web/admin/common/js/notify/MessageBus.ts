module api.notify {

    export function showFeedback(message: string): string {
        return NotifyManager.get().showFeedback(message);
    }

    export function showError(message: string, autoHide: boolean = true): string {
        return NotifyManager.get().showError(message, autoHide);
    }

    export function showWarning(message: string): string {
        return NotifyManager.get().showWarning(message);
    }

    export function updateAppTabCount(appId, tabCount: Number): void {
        // TODO: This needs to be reworked
    }

}
