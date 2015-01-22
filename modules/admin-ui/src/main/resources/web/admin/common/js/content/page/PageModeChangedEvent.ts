module api.content.page {

    export class PageModeChangedEvent {

        private previousMode: PageMode;

        private newMode: PageMode;

        constructor(previousMode: PageMode, newMode: PageMode) {
            this.previousMode = previousMode;
            this.newMode = newMode;
        }

        getPreviousMode(): PageMode {
            return this.previousMode;
        }

        getNewMode(): PageMode {
            return this.newMode;
        }
    }
}