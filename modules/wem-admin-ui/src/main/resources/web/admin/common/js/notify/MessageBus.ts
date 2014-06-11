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

}
