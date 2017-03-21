module api.notify {

    export function showSuccess(message: string, autoHide: boolean = true) {
        NotifyManager.get().showSuccess(message, autoHide);
    }

    export function showFeedback(message: string, autoHide: boolean = true) {
        NotifyManager.get().showFeedback(message, autoHide);
    }

    export function showError(message: string, autoHide: boolean = true) {
        NotifyManager.get().showError(message, autoHide);
    }

    export function showWarning(message: string, autoHide: boolean = true) {
        NotifyManager.get().showWarning(message, autoHide);
    }

}
