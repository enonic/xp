module api.liveedit.text {

    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import PageItemType = api.liveedit.PageItemType;

    export class TextPlaceholder extends ItemViewPlaceholder {

        private textView: TextComponentView;

        constructor(componentView: TextComponentView) {
            this.textView = componentView;

            super();
            this.addClass("text-placeholder");
        }

    }
}