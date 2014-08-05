module api.liveedit.text {


    export class TextComponentStartEditingEvent extends api.event.Event {

        private view: TextComponentView;

        constructor(view: TextComponentView) {
            super();
            this.view = view;
        }

        getView(): TextComponentView {
            return this.view;
        }

        static on(handler: (event: TextComponentStartEditingEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: TextComponentStartEditingEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}