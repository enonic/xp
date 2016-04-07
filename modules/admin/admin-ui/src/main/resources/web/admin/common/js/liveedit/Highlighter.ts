module api.liveedit {

    export class Highlighter extends api.dom.Element {

        private rectangle: api.dom.Element;

        private static INSTANCE: Highlighter;

        private lastHighlightedItemView: ItemView;

        constructor() {
            // Needs to be a SVG element as the css has pointer-events:none
            // CSS pointer-events only works for SVG in IE
            var svgCls = api.StyleHelper.getCls("highlight-border");
            var html = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="' + svgCls + '" style="top:-5000px;left:-5000px">' +
                       '    <rect width="150" height="150"/>' +
                       '</svg>';
            super(new api.dom.ElementFromHelperBuilder().
                setHelper(api.dom.Element.fromString(html).getEl()).
                setLoadExistingChildren(true));

            this.rectangle = this.getChildren()[0];
            this.appendChild(this.rectangle);

            api.dom.Body.get().appendChild(this);
        }

        public static get(): Highlighter {
            if (!Highlighter.INSTANCE) {
                Highlighter.INSTANCE = new Highlighter();
            }
            return Highlighter.INSTANCE;
        }

        highlightItemView(itemView: ItemView): void {
            if (!itemView) {
                this.hide();
                return;
            }
            var dimensions = itemView.getEl().getDimensionsTopRelativeToParent();
            var style = itemView.getType().getConfig().getHighlighterStyle();

            this.resize(dimensions, this.preProcessStyle(style));
            this.show();

            this.lastHighlightedItemView = itemView;
        }

        highlightElement(dimensions: ElementDimensions, style: HighlighterStyle): void {
            this.resize(dimensions, style);
            this.show();
        }

        updateLastHighlightedItemView() {
            this.highlightItemView(this.lastHighlightedItemView);
        }

        protected preProcessStyle(style: HighlighterStyle): HighlighterStyle {
            return style;
        }

        private resize(dimensions: ElementDimensions, style: HighlighterStyle): void {
            var w = Math.round(dimensions.width),
                h = Math.round(dimensions.height),
                top = Math.round(dimensions.top),
                left = Math.round(dimensions.left);

            this.getEl().setWidthPx(w).setHeightPx(h).setTopPx(top).setLeftPx(left);

            this.rectangle.getEl()
                .setAttribute('width', w + '')
                .setAttribute('height', h + '')
                .setStroke(style.stroke)
                .setStrokeDasharray(style.strokeDasharray)
                .setFill(style.fill);
        }

    }
}
