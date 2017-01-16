module api.liveedit.layout {

    import LayoutComponent = api.content.page.region.LayoutComponent;
    import RegionView = api.liveedit.RegionView;
    import ComponentItemType = api.liveedit.ComponentItemType;

    export class LayoutItemType extends ComponentItemType {

        private static INSTANCE: LayoutItemType = new LayoutItemType();

        static get(): LayoutItemType {
            return LayoutItemType.INSTANCE;
        }

        constructor() {
            super('layout');
        }

        isComponentType(): boolean {
            return true;
        }

        createView(config: CreateItemViewConfig<RegionView,LayoutComponent>): LayoutComponentView {
            return new LayoutComponentView(new LayoutComponentViewBuilder().
                setItemViewProducer(config.itemViewProducer).
                setParentRegionView(config.parentView).
                setParentElement(config.parentElement).
                setComponent(config.data).
                setElement(config.element).
                setPositionIndex(config.positionIndex));
        }
    }

    LayoutItemType.get();
}
