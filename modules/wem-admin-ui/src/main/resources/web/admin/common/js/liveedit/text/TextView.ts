module api.liveedit.text {

    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.text.TextComponent;

    export class TextView extends PageComponentView<TextComponent> {

        constructor(parentRegionView: RegionView, textComponent: TextComponent, element?: HTMLElement) {
            super(TextItemType.get(), parentRegionView, textComponent, element);
        }

        duplicate(duplicate: TextComponent): TextView {

            var duplicatedView = new TextView(this.getParentRegionView(), duplicate);
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }

        public static fromJQuery(element: JQuery): TextView {
            return new TextView(null, null, <HTMLElement>element.get(0));
        }

        getTooltipViewer(): TextComponentViewer {
            return new TextComponentViewer();
        }
    }
}