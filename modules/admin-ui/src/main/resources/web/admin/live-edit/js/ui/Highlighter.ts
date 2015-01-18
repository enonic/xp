module LiveEdit.ui {

    import ItemView = api.liveedit.ItemView;

    export class Highlighter extends BaseComponent {

        private rectangle: api.dom.Element;

        constructor() {
            // Needs to be a SVG element as the css has pointer-events:none
            // CSS pointer-events only works for SVG in IE
            var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlight-border" style="top:-5000px;left:-5000px">' +
                       '    <rect width="150" height="150"/>' +
                       '</svg>';
            super(new api.dom.ElementFromHelperBuilder().
                setHelper(api.dom.Element.fromString(html).getEl()).
                setLoadExistingChildren(true));

            this.rectangle = this.getChildren()[0];
            this.appendChild(this.rectangle);
        }

        highlightItemView(itemView: ItemView): void {

            if (!itemView) {
                this.hide();
                return;
            }

            this.resizeToComponent(itemView);

            this.show();
        }

        private resizeToComponent(itemView: ItemView): void {
            var itemDimensions = itemView.getElementDimensions();
            var w = Math.round(itemDimensions.width),
                h = Math.round(itemDimensions.height),
                top = Math.round(itemDimensions.top),
                left = Math.round(itemDimensions.left);

            this.getEl().setWidthPx(w).setHeightPx(h).setTopPx(top).setLeftPx(left);
            this.rectangle.getEl().setAttribute('width', w + '').setAttribute('height', h + '');

            // paint border
            var style = itemView.getType().getConfig().getHighlighterStyle();
            this.getEl().setStroke(style.stroke).setStrokeDasharray(style.strokeDasharray).setFill(style.fill);
        }

    }
}
