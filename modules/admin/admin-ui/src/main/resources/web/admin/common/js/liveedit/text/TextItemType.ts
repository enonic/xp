module api.liveedit.text {

    import ItemTypeConfigJson = api.liveedit.ItemTypeConfigJson;
    import ComponentItemType = api.liveedit.ComponentItemType;
    import RegionView = api.liveedit.RegionView;
    import TextComponent = api.content.page.region.TextComponent;

    export class TextItemType extends ComponentItemType {

        private static INSTANCE: TextItemType = new TextItemType();

        static get(): TextItemType {
            return TextItemType.INSTANCE;
        }

        constructor() {
            super("text");
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
            return true;
        }

        protected getItemTypeConfig(itemType: string): ItemTypeConfig {
            let config = super.getItemTypeConfig(itemType);

            config.getContextMenuConfig().push("edit");

            return config;

        }
    }

    TextItemType.get();
}