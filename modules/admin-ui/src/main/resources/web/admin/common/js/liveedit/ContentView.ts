module api.liveedit {

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

    // TODO:
    export class ContentView extends ItemView {

        private parentPartComponentView: PartComponentView;

        constructor(builder: ContentViewBuilder) {


            super(new ItemViewBuilder().
                setItemViewIdProducer(builder.parentPartComponentView.getItemViewIdProducer()).
                setType(ContentItemType.get()).
                setElement(builder.element).
                setParentElement(builder.parentElement).
                setParentView(builder.parentPartComponentView).
                setContextMenuActions(this.createContentContextMenuActions()).
                setContextMenuTitle(new ContentViewContextMenuTitle(this)));
            this.parentPartComponentView = builder.parentPartComponentView;
        }

        private createContentContextMenuActions(): api.ui.Action[] {
            var actions: api.ui.Action[] = [];
            actions.push(new api.ui.Action('Parent').onExecuted(() => {
                var parentView: ItemView = this.getParentItemView();
                if (parentView) {
                    this.deselect();
                    parentView.select();
                }
            }));
            actions.push(new api.ui.Action('Insert').onExecuted(() => {
                // TODO
            }));
            actions.push(new api.ui.Action('View').onExecuted(() => {
                // TODO
            }));
            actions.push(new api.ui.Action('Edit').onExecuted(() => {
                // TODO
            }));
            return actions;
        }

        isEmpty(): boolean {
            return false;
        }


        getParentPartComponentView(): PartComponentView {
            return this.parentPartComponentView;
        }

        getParentItemView(): PartComponentView {
            return this.parentPartComponentView;
        }

        select(clickPosition ?: Position) {
            super.select(clickPosition);
        }
    }
}