module api.liveedit {

    export class PageView extends ItemView {

        constructor(element?: HTMLElement) {
            super(RegionItemType.get(), element);
        }

        static fromHTMLElement(element: HTMLElement): PageView {
            return new PageView(element);
        }
    }
}