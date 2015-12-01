module api.liveedit.text {

    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import PageItemType = api.liveedit.PageItemType;

    export class TextPlaceholder extends ItemViewPlaceholder {

        constructor() {
            super();
            this.addClassEx("text-placeholder");
        }

    }
}