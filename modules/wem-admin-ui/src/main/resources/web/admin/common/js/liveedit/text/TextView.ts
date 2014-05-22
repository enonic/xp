module api.liveedit.text {

    import PageComponentView = api.liveedit.PageComponentView;

    export class TextView extends PageComponentView {

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