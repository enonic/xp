module api.liveedit {

    import Component = api.content.page.Component;

    export class ComponentItemType extends ItemType {

        constructor(shortName: string, config: ItemTypeConfigJson) {
            super(shortName, config);
        }

        createView(config: CreateItemViewConfig<RegionView,Component>): PageComponentView<Component> {
            throw new Error("Must be implemented by inheritors");
        }
    }
}