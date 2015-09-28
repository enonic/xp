module api.liveedit.text {

    import ItemTypeConfigJson = api.liveedit.ItemTypeConfigJson;
    import ComponentItemType = api.liveedit.ComponentItemType;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.region.TextComponent;

    export class TextItemType extends ComponentItemType {

        private static INSTANCE = new TextItemType();

        static get(): TextItemType {
            return TextItemType.INSTANCE;
        }

        constructor() {
            super("text", <ItemTypeConfigJson>{
                cssSelector: '[data-portal-component-type=text]',
                draggable: true,
                cursor: 'move',
                iconCls: 'live-edit-font-icon-text',
                highlighterStyle: {
                    stroke: 'rgba(152, 201, 242, 1)',
                    strokeDasharray: '5 5',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['parent', 'edit', 'remove', 'clear', 'duplicate']
            });
        }

        createView(config: CreateItemViewConfig<RegionView,TextComponent>): TextComponentView {
            return new TextComponentView(new TextComponentViewBuilder().
                setItemViewProducer(config.itemViewProducer).
                setParentRegionView(config.parentView).
                setParentElement(config.parentElement).
                setComponent(config.data).
                setElement(config.element).
                setPositionIndex(config.positionIndex));
        }

        isComponentType(): boolean {
            return true
        }
    }

    TextItemType.get();
}