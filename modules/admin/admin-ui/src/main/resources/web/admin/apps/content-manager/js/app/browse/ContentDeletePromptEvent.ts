module app.browse {

    export class ContentDeletePromptEvent extends BaseContentModelEvent {

        private yesCallback: () => void;

        private noCallback: () => void;

        setYesCallback(callback: ()=>void): ContentDeletePromptEvent {
            this.yesCallback = callback;
            return this;
        }

        setNoCallback(callback: () => void): ContentDeletePromptEvent {
            this.noCallback = callback;
            return this;
        }

        getYesCallback(): () => void {
            return this.yesCallback;
        }

        getNoCallback(): () => void {
            return this.noCallback;
        }

        static on(handler: (event: ContentDeletePromptEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentDeletePromptEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
