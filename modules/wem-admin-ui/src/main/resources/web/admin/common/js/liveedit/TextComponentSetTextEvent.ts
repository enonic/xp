module api.liveedit {

    export class TextComponentSetTextEvent extends api.event.Event2 {

        private pageComponentId: PageComponentId;

        private text: string;

        constructor(pageComponent: PageComponentId, text: string) {
            super();
            this.pageComponentId = pageComponent;
            this.text = text;
        }

        getPageComponentId(): PageComponentId {
            return this.pageComponentId;
        }

        getText(): string {
            return this.text;
        }

        static on(handler: (event: TextComponentSetTextEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: TextComponentSetTextEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}