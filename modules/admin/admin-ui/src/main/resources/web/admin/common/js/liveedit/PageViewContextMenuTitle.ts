module api.liveedit {

    export class PageViewContextMenuTitle extends ItemViewContextMenuTitle {

        constructor(content: api.content.Content) {
            let name = !!content.getDisplayName() ? content.getDisplayName() : api.content.ContentUnnamed.prettifyUnnamed();
            super(name, PageItemType.get().getConfig().getIconCls());
        }

    }

}
