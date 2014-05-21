module api.liveedit.text {

    export class TextView extends ItemView {

        constructor(element?: HTMLElement) {
            super(TextItemType.get(), element);
        }

        static fromHTMLElement(element: HTMLElement): TextView {
            return new TextView(element);
        }

        public static fromJQuery(element: JQuery): TextView {
            return new TextView(<HTMLElement>element.get(0));
        }
    }
}