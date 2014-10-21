module api.liveedit {

    export class TextComponentSetTextEvent extends api.event.Event {

        private text: string;

        constructor(text: string) {
            super();
            this.text = text;
        }

        getText(): string {
            return this.text;
        }

        static on(handler: (event: TextComponentSetTextEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: TextComponentSetTextEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}