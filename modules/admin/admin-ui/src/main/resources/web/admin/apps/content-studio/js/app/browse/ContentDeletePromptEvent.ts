module app.browse {

    import Event = api.event.Event;

    export class ContentDeletePromptEvent extends BaseContentModelEvent {

        private yesCallback: (exclude?: api.content.CompareStatus[]) => void;

        private noCallback: () => void;

        setYesCallback(callback: (exclude?: api.content.CompareStatus[])=>void): ContentDeletePromptEvent {
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
            Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentDeletePromptEvent) => void) {
            Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
