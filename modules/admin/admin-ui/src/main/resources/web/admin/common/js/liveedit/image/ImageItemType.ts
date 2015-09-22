module api.liveedit.image {

    import ImageComponent = api.content.page.region.ImageComponent;
    import ItemType = api.liveedit.ItemType;
    import ItemTypeConfigJson = api.liveedit.ItemTypeConfigJson;
    import ComponentItemType = api.liveedit.ComponentItemType;
    import RegionView = api.liveedit.RegionView;

    export class ImageItemType extends ComponentItemType {

        private static INSTANCE = new ImageItemType();

        static get(): ImageItemType {
            return ImageItemType.INSTANCE;
        }

        constructor() {
            super("image", <ItemTypeConfigJson>{
                cssSelector: '[data-portal-component-type=image]',
                draggable: true,
                cursor: 'move',
                iconCls: 'live-edit-font-icon-image',
                highlighterStyle: {
                    stroke: 'rgba(68, 68, 68, 1)',
                    strokeDasharray: '5 5',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['parent', 'remove', 'clear', 'duplicate']
            });
        }

        createView(config: CreateItemViewConfig<RegionView,ImageComponent>): ImageComponentView {
            return new ImageComponentView(new ImageComponentViewBuilder().
                setItemViewProducer(config.itemViewProducer).
                setParentRegionView(config.parentView).
                setParentElement(config.parentElement).
                setElement(config.element).
                setComponent(config.data).
                setPositionIndex(config.positionIndex));
        }

        isComponentType(): boolean {
            return true
        }
    }

    ImageItemType.get();
}