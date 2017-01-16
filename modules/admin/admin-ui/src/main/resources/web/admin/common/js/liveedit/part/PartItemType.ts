module api.liveedit.part {

    import ItemType = api.liveedit.ItemType;
    import ComponentItemType = api.liveedit.ComponentItemType;
    import RegionView = api.liveedit.RegionView;
    import PartComponent = api.content.page.region.PartComponent;

    export class PartItemType extends ComponentItemType {

        private static INSTANCE: PartItemType = new PartItemType();

        static get(): PartItemType {
            return PartItemType.INSTANCE;
        }

        constructor() {
            super("part");
        }

        isComponentType(): boolean {
            return true;
        }

        createView(config: CreateItemViewConfig<RegionView,PartComponent>): PartComponentView {

            return new PartComponentView(new PartComponentViewBuilder().
                setItemViewProducer(config.itemViewProducer).
                setParentRegionView(config.parentView).
                setParentElement(config.parentElement).
                setComponent(config.data).
                setElement(config.element).
                setPositionIndex(config.positionIndex));
        }
    }

    PartItemType.get();
}
