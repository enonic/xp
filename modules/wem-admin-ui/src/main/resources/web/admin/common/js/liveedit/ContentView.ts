module api.liveedit {

    import Content = api.content.Content;
    import PartView = api.liveedit.part.PartView;

    export class ContentView extends ItemView {

        private parentPartView: PartView;

        constructor(parentPartView: PartView, element?: HTMLElement) {
            super(new ItemViewBuilder().
                setItemViewIdProducer(parentPartView.getItemViewIdProducer()).
                setType(ContentItemType.get()).
                setElement(element));
            this.parentPartView = parentPartView;
        }

        getParentPartView(): PartView {
            return this.parentPartView;
        }

        getName(): string {

            return "[No name]";
        }

        getParentItemView(): PartView {
            return this.parentPartView;
        }

        select() {

            super.select();
        }
    }
}