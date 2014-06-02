module api.liveedit {

    export class PageComponentItemType extends ItemType {

        constructor(shortName: string, config: ItemTypeConfigJson) {
            super(shortName, config);
        }

        createView(element?: HTMLElement, dummy?: boolean): PageComponentView {
            throw new Error("Must be implemented by inheritors");
        }
    }
}