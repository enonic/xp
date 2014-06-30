module api.liveedit.text {

    import PageComponentView = api.liveedit.PageComponentView;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.text.TextComponent;

    export class TextComponentViewBuilder extends PageComponentViewBuilder<TextComponent> {

        constructor() {
            super();
            this.setType(TextItemType.get());
        }
    }

    export class TextComponentView extends PageComponentView<TextComponent> {

        private textComponent: TextComponent;

        constructor(builder: TextComponentViewBuilder) {
            super(builder.setContextMenuActions(this.createTextContextMenuActions()));
            this.textComponent = builder.pageComponent;
        }

        duplicate(duplicate: TextComponent): TextComponentView {

            var duplicatedView = new TextComponentView(new TextComponentViewBuilder().
                setParentRegionView(this.getParentItemView()).
                setParentElement(this.getParentElement()).
                setPageComponent(duplicate));
            duplicatedView.insertAfterEl(this);
            return duplicatedView;
        }

        public static fromJQuery(element: JQuery): TextComponentView {
            return new TextComponentView(new TextComponentViewBuilder().setElement(api.dom.Element.fromHtmlElement(<HTMLElement>element.get(0))));
        }

        getTooltipViewer(): TextComponentViewer {
            return new TextComponentViewer();
        }

        private createTextContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                // TODO
            }));
            return actions;
        }
    }
}