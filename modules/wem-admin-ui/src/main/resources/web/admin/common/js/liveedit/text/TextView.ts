module api.liveedit.text {

    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;

    export class TextView extends PageComponentView {

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