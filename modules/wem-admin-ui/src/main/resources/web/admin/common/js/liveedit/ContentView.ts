module api.liveedit {

    import Content = api.content.Content;
    import PartComponentView = api.liveedit.part.PartComponentView;

    export class ContentView extends ItemView {

        private parentPartComponentView: PartComponentView;

        constructor(parentPartComponentView: PartComponentView, element?: HTMLElement) {
            super(new ItemViewBuilder().
                setItemViewIdProducer(parentPartComponentView.getItemViewIdProducer()).
                setType(ContentItemType.get()).
                setElement(element));
            this.parentPartComponentView = parentPartComponentView;
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

        select() {

            super.select();
        }
    }
}