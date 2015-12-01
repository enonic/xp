module api.liveedit.layout {

    import LayoutComponent = api.content.page.region.LayoutComponent;
    import ItemType = api.liveedit.ItemType;
    import ItemTypeConfigJson = api.liveedit.ItemTypeConfigJson;
    import RegionView = api.liveedit.RegionView;
    import ComponentItemType = api.liveedit.ComponentItemType;

    export class LayoutItemType extends ComponentItemType {

        private static INSTANCE = new LayoutItemType();

        static get(): LayoutItemType {
            return LayoutItemType.INSTANCE;
        }

        constructor() {
            super("layout", <ItemTypeConfigJson>{
                cssSelector: '[data-portal-component-type=layout]',
                draggable: true,
                cursor: 'move',
                iconCls: api.StyleHelper.COMMON_PREFIX + 'icon-layout',
                highlighterStyle: {
                    stroke: 'rgba(255, 165, 0, 1)',
                    strokeDasharray: '5 5',
                    fill: 'rgba(100, 12, 36, 0)'
                },
                contextMenuConfig: ['parent', 'remove', 'clear', 'duplicate']
            });
        }

        isComponentType(): boolean {
            return true
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