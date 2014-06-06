module api.liveedit {

    import PageComponent = api.content.page.PageComponent;

    export class PageComponentItemType extends ItemType {

        constructor(shortName: string, config: ItemTypeConfigJson) {
            super(shortName, config);
        }

        createView(parentRegion: RegionView, pageComponent: PageComponent, element?: HTMLElement,
                   dummy?: boolean): PageComponentView<PageComponent> {
            throw new Error("Must be implemented by inheritors");
        }
    }
}