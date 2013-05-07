module API.notify {

    export function showFeedback(message:string):void {
        newInfo(message).send();
    }

    export function updateAppTabCount(appId, tabCount:Number):void {
        // TODO: This needs to be reworked
    }

}
