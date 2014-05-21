module api.liveedit.image {

    import ItemType = api.liveedit.ItemType;

    export class ImageItemType extends ItemType {

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

        isPageComponentType(): boolean {
            return true
        }
    }

    ImageItemType.get();
}