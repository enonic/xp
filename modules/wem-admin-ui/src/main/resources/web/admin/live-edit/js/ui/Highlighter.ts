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
            super(new api.dom.ElementProperties().
                setHelper(new api.dom.ElementHelper(wemjq(html).get(0))).
                setLoadExistingChildren(true));

            this.rectangle = this.getChildren()[0];
            this.appendChild(this.rectangle);
        }

        showOnComponent(component: ItemView): void {
            if (!component) {
                if (this.isVisible()) {
                    this.hide();
                }
                return;
            }

            this.resizeToComponent(component);

            if (!this.isVisible()) {
                this.show();
            }
        }

        private resizeToComponent(component: ItemView): void {
            var componentBoxModel = component.getElementDimensions();
            var w = Math.round(componentBoxModel.width),
                h = Math.round(componentBoxModel.height),
                top = Math.round(componentBoxModel.top),
                left = Math.round(componentBoxModel.left);

            this.getEl().setWidthPx(w).setHeightPx(h).setTopPx(top).setLeftPx(left);
            this.rectangle.getEl().setAttribute('width', w + '').setAttribute('height', h + '');

            // paint border
            var style = component.getType().getConfig().getHighlighterStyle();
            wemjq(this.getHTMLElement()).css(style);
        }

    }
}
