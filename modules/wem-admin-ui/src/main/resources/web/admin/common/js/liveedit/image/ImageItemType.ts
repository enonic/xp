module api.liveedit.image {

    import ItemType = api.liveedit.ItemType;
    import ItemTypeConfigJson = api.liveedit.ItemTypeConfigJson;
    import PageComponentView = api.liveedit.PageComponentView;
    import PageComponentItemType = api.liveedit.PageComponentItemType;
    import RegionView = api.liveedit.RegionView;

    export class ImageItemType extends PageComponentItemType {

        private static INSTANCE = new ImageItemType();

        static get(): ImageItemType {
            return ImageItemType.INSTANCE;
        }

        constructor() {
            super("image", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=image]',
                draggable: true,
                cursor: 'move',
                iconCls: 'live-edit-font-icon-image',
                highlighterStyle: {
                    stroke: 'rgba(68, 68, 68, 1)',
                    strokeDasharray: '5 5',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['parent', 'remove', 'clear', 'duplicate']
            });
        }

        createView(element?: HTMLElement,  dummy?: boolean): ImageView {
            return new ImageView(element, dummy);
        }

        isPageComponentType(): boolean {
            return true
        }
    }

    ImageItemType.get();
}