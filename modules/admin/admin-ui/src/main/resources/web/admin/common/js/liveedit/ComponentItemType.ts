module api.liveedit {

    import Component = api.content.page.region.Component;

    export class ComponentItemType extends ItemType {

        createView(config: CreateItemViewConfig<RegionView,Component>): ComponentView<Component> {
            throw new Error("Must be implemented by inheritors");
        }
        
        protected getItemTypeConfig(itemType: string): ItemTypeConfig {
            return new ItemTypeConfig(<ItemTypeConfigJson>{
                cssSelector: '[data-portal-component-type=' + itemType + ']',
                draggable: true,
                cursor: 'move',
                iconCls: api.StyleHelper.COMMON_PREFIX + 'icon-' + itemType,
                highlighterStyle: {
                    stroke: 'rgba(68, 68, 68, 1)', // not used
                    strokeDasharray: '',
                    fill: 'rgba(255, 255, 255, 0)' // not used
                },
                contextMenuConfig: ['parent', 'remove', 'clear', 'duplicate']
            });
        }
    }
}