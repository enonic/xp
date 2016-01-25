module api.liveedit {

    import Component = api.content.page.region.Component;

    export class ComponentItemType extends ItemType {

        constructor(shortName: string, config: ItemTypeConfigJson) {
            super(shortName, config);
        }

        createView(config: CreateItemViewConfig<RegionView,Component>): ComponentView<Component> {
            throw new Error("Must be implemented by inheritors");
        }

        protected getDefaultConfigJson(itemType: string): ItemTypeConfigJson {
            return <ItemTypeConfigJson>{
                cssSelector: '[data-portal-component-type=' + itemType + ']',
                draggable: true,
                cursor: 'move',
                iconCls: api.StyleHelper.COMMON_PREFIX + 'icon-' + itemType,
                highlighterStyle: {
                    stroke: 'rgba(68, 68, 68, 1)',
                    strokeDasharray: '5 5',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['parent', 'remove', 'clear', 'duplicate']
            };
        }
    }
}