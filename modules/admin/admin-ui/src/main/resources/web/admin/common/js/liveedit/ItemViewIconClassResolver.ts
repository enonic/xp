module api.liveedit {

    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import PartItemType = api.liveedit.part.PartItemType;
    import ImageItemType = api.liveedit.image.ImageItemType;
    import TextItemType = api.liveedit.text.TextItemType;

    export class ItemViewIconClassResolver {

        public static resolve(itemView: ItemView): string {
            if (!itemView) {
                return '';
            }

            var iconClass = '',
                itemType = itemView.getType();

            if (RegionItemType.get().equals(itemType)) {
                iconClass = 'live-edit-font-icon-region';
            } else if (LayoutItemType.get().equals(itemType)) {
                iconClass = 'live-edit-font-icon-layout';
            } else if (PartItemType.get().equals(itemType)) {
                iconClass = 'live-edit-font-icon-part';
            } else if (ImageItemType.get().equals(itemType)) {
                iconClass = 'live-edit-font-icon-image';
            } else if (TextItemType.get().equals(itemType)) {
                iconClass = 'live-edit-font-icon-text';
            }
            return iconClass;
        }
    }

}