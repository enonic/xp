module api.liveedit.image {

    import ImageComponent = api.content.page.region.ImageComponent;
    import ItemType = api.liveedit.ItemType;
    import ItemTypeConfigJson = api.liveedit.ItemTypeConfigJson;
    import ComponentItemType = api.liveedit.ComponentItemType;
    import RegionView = api.liveedit.RegionView;

    export class ImageItemType extends ComponentItemType {

        private static INSTANCE: ImageItemType = new ImageItemType();

        static get(): ImageItemType {
            return ImageItemType.INSTANCE;
        }

        constructor() {
            super('image');
        }

        createView(config: CreateItemViewConfig<RegionView,ImageComponent>): ImageComponentView {
            return new ImageComponentView(<ImageComponentViewBuilder>new ImageComponentViewBuilder().
                setItemViewProducer(config.itemViewProducer).
                setParentRegionView(config.parentView).
                setParentElement(config.parentElement).
                setElement(config.element).
                setComponent(config.data).
                setPositionIndex(config.positionIndex));
        }

        isComponentType(): boolean {
            return true;
        }
    }

    ImageItemType.get();
}
