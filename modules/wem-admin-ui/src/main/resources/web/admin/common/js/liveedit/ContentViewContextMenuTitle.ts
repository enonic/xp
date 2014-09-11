module api.liveedit {

    export class ContentViewContextMenuTitle extends ItemViewContextMenuTitle {

        constructor(contentView: ContentView) {
            super(contentView.getName(), ContentItemType.get().getConfig().getIconCls());
        }

    }

}