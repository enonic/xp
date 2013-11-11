module api_notify {

    export function showFeedback(message:string):void {
        newInfo(message).send();
    }

    export function showError(message:string):void {
        newError(message).send();
    }

    export function updateAppTabCount(appId, tabCount:Number):void {
        // TODO: This needs to be reworked
    }

}
