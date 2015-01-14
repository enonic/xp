module api.liveedit.part {

    import ItemType = api.liveedit.ItemType;
    import ComponentItemType = api.liveedit.ComponentItemType;
    import RegionView = api.liveedit.RegionView;
    import PartComponent = api.content.page.region.PartComponent;

    export class PartItemType extends ComponentItemType {

        private static INSTANCE = new PartItemType();

        static get(): PartItemType {
            return PartItemType.INSTANCE;
        }

        constructor() {
            super("part", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=part]',
                draggable: true,
                cursor: 'move',
                iconCls: 'live-edit-font-icon-part',
                highlighterStyle: {
                    stroke: 'rgba(68, 68, 68, 1)',
                    strokeDasharray: '5 5',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['parent', 'remove', 'clear', 'duplicate']
            });
        }

        isComponentType(): boolean {
            return true
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