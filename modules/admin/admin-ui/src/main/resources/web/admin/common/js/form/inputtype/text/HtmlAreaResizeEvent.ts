module api.form.inputtype.text {
    import Event = api.event.Event;

    export class HtmlAreaResizeEvent extends Event {
        private htmlArea: HtmlArea;

        constructor(htmlArea: HtmlArea) {
            super();
            this.htmlArea = htmlArea;
        }

        getHtmlArea(): HtmlArea {
            return this.htmlArea;
        }

        static on(handler: (event: HtmlAreaResizeEvent) => void) {
            Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: HtmlAreaResizeEvent) => void) {
            Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
