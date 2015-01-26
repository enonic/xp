module api.liveedit.text {

    import LayoutItemType = api.liveedit.layout.LayoutItemType;
    import PageItemType = api.liveedit.PageItemType;

    export class TextPlaceholder extends ComponentPlaceholder {

        private textView: TextComponentView;
        private message: api.dom.DivEl;

        constructor(componentView: TextComponentView) {
            this.textView = componentView;

            super();
            this.addClass("text-placeholder");

            this.message = new api.dom.DivEl("message");
            this.message.setHtml("Click to edit");

            this.appendChild(this.message);
        }

        select() {
            this.message.show();
        }

        deselect() {
            this.message.hide();
        }

    }
}