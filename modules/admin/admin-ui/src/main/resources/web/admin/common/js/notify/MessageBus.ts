module api.notify {

    export function showSuccess(message: string, autoHide: boolean = true): string {
        return NotifyManager.get().showSuccess(message, autoHide);
    }
    export function showFeedback(message: string, autoHide: boolean = true): string {
        return NotifyManager.get().showFeedback(message, autoHide);
    }

    export function showError(message: string, autoHide: boolean = true): string {
        return NotifyManager.get().showError(message, autoHide);
    }

    export function showWarning(message: string, autoHide: boolean = true): string {
        return NotifyManager.get().showWarning(message, autoHide);
    }

}
