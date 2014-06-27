module api.liveedit {

    import Content = api.content.Content;
    import PartComponentView = api.liveedit.part.PartComponentView;

    export class ContentViewBuilder {

        parentPartComponentView: PartComponentView;

        parentElement: api.dom.Element;

        element: api.dom.Element;

        setParentPartComponentView(value: PartComponentView): ContentViewBuilder {
            this.parentPartComponentView = value;
            return this;
        }

        setParentElement(value: api.dom.Element): ContentViewBuilder {
            this.parentElement = value;
            return this;
        }

        setElement(value: api.dom.Element): ContentViewBuilder {
            this.element = value;
            return this;
        }

    }

    export class ContentView extends ItemView {

        private parentPartComponentView: PartComponentView;

        constructor(builder: ContentViewBuilder) {
            super(new ItemViewBuilder().
                setItemViewIdProducer(builder.parentPartComponentView.getItemViewIdProducer()).
                setType(ContentItemType.get()).
                setElement(builder.element).
                setParentElement(builder.parentElement).
                setParentView(builder.parentPartComponentView));
            this.parentPartComponentView = builder.parentPartComponentView;
        }

        getParentPartComponentView(): PartComponentView {
            return this.parentPartComponentView;
        }

        getName(): string {

            return "[No name]";
        }

        getParentItemView(): PartComponentView {
            return this.parentPartComponentView;
        }

        select(clickPosition?: Position) {
            super.select(clickPosition);
        }
    }
}