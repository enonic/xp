module api.liveedit {

    import Content = api.content.Content;

    export class PageView extends ItemView {

        constructor(element?: HTMLElement) {
            super(PageItemType.get(), element);
        }

        getName(): string {

            var content = PageItemType.get().getContent();
            return content ? content.getDisplayName() : "[No name]";
        }

        select() {
            new PageSelectEvent(this).fire();
            super.select();
        }

        static fromHTMLElement(element: HTMLElement): PageView {
            return new PageView(element);
        }

        public static fromJQuery(element: JQuery): PageView {
            return new PageView(<HTMLElement>element.get(0));
        }
    }
}