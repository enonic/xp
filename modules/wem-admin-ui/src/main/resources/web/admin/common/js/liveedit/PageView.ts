module api.liveedit {

    import Content = api.content.Content;

    export class PageView extends ItemView {

        constructor(element?: HTMLElement) {
            super(PageItemType.get(), element);
        }

        getComponentName(): string {

            var content = PageItemType.get().getContent();
            return content ? content.getDisplayName() : "[No name]";
        }


        static fromHTMLElement(element: HTMLElement): PageView {
            return new PageView(element);
        }

        public static fromJQuery(element: JQuery): PageView {
            return new PageView(<HTMLElement>element.get(0));
        }
    }
}