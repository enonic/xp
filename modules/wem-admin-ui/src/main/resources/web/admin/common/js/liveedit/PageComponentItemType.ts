module api.liveedit {

    import PageComponent = api.content.page.PageComponent;

    export class PageComponentItemType extends ItemType {

        constructor(shortName: string, config: ItemTypeConfigJson) {
            super(shortName, config);
        }

        createView(config: CreateItemViewConfig<RegionView,PageComponent>): PageComponentView<PageComponent> {
            throw new Error("Must be implemented by inheritors");
        }
    }
}