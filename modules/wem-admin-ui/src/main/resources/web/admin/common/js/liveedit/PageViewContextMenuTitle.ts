module api.liveedit {

    export class PageViewContextMenuTitle extends ItemViewContextMenuTitle {

        constructor(content: api.content.Content) {
            super(content.getDisplayName(), PageItemType.get().getConfig().getIconCls());
        }

    }

}