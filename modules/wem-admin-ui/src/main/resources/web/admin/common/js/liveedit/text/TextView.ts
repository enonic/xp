module api.liveedit.text {

    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.text.TextComponent;

    export class TextViewBuilder extends PageComponentViewBuilder<TextComponent> {

        constructor() {
            super();
            this.setType(TextItemType.get());
        }
    }

    export class TextView extends PageComponentView<TextComponent> {

        constructor(builder: TextViewBuilder) {
            super(builder);
        }

        duplicate(duplicate: TextComponent): TextView {

            var duplicatedView = new TextView(new TextViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setPageComponent(duplicate));
            this.getEl().insertAfterThisEl(duplicatedView.getEl());
            return duplicatedView;
        }

        public static fromJQuery(element: JQuery): TextView {
            return new TextView(new TextViewBuilder().setElement(<HTMLElement>element.get(0)));
        }

        getTooltipViewer(): TextComponentViewer {
            return new TextComponentViewer();
        }
    }
}