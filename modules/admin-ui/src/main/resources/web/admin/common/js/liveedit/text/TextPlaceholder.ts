module api.liveedit.text {

    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import PageItemType = api.liveedit.PageItemType;

    export class TextPlaceholder extends ComponentPlaceholder {

        constructor(layoutView: TextComponentView) {
            super();
            this.addClass("text-placeholder");
            this.setHtml("Click to edit");
        }

        select() {

        }

        deselect() {

        }
    }
}