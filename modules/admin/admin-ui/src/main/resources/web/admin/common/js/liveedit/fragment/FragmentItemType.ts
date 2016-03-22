module api.liveedit.fragment {

    import FragmentComponent = api.content.page.region.FragmentComponent;
    import ItemType = api.liveedit.ItemType;
    import ItemTypeConfigJson = api.liveedit.ItemTypeConfigJson;
    import ComponentItemType = api.liveedit.ComponentItemType;
    import RegionView = api.liveedit.RegionView;

    export class FragmentItemType extends ComponentItemType {

        private static INSTANCE = new FragmentItemType();

        static get(): FragmentItemType {
            return FragmentItemType.INSTANCE;
        }

        constructor() {
            super("fragment", this.getDefaultConfigJson("fragment"));
        }

        createView(config: CreateItemViewConfig<RegionView,FragmentComponent>): FragmentComponentView {
            return new FragmentComponentView(new FragmentComponentViewBuilder().setItemViewProducer(
                config.itemViewProducer).setParentRegionView(config.parentView).setParentElement(config.parentElement).setElement(
                config.element).setComponent(config.data).setPositionIndex(config.positionIndex));
        }

        isComponentType(): boolean {
            return true
        }
    }

    FragmentItemType.get();
}