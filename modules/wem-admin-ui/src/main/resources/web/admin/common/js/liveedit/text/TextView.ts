module api.liveedit.text {

    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.text.TextComponent;

    export class TextView extends PageComponentView<TextComponent> {

        constructor(element?: HTMLElement) {
            super(TextItemType.get(), element);
        }

        duplicate(): TextView {

            var duplicatedView = new TextView();
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }

        public static fromJQuery(element: JQuery): TextView {
            return new TextView(<HTMLElement>element.get(0));
        }
    }
}