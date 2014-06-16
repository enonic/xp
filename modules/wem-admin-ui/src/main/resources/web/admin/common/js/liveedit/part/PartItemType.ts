module api.liveedit.part {

    import ItemType = api.liveedit.ItemType;
    import PageComponentView = api.liveedit.PageComponentView;
    import PageComponentItemType = api.liveedit.PageComponentItemType;
    import RegionView = api.liveedit.RegionView;
    import PartComponent = api.content.page.part.PartComponent;

    export class PartItemType extends PageComponentItemType {

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

        isPageComponentType(): boolean {
            return true
        }

        createView(config: CreateItemViewConfig<RegionView,PartComponent>): PartComponentView {
            return new PartComponentView(new PartComponentViewBuilder().
                setItemViewProducer(config.itemViewProducer).
                setParentRegionView(config.parentView).
                setParentElement(config.parentElement).
                setPageComponent(config.data).
                setElement(config.element).
                setPositionIndex(config.positionIndex));
        }
    }

    PartItemType.get();
}