module LiveEdit.ui {

    import Element = api.dom.Element;
    import DivEl = api.dom.DivEl;
    import ItemView = api.liveedit.ItemView;

    export class Shader {

        private static CLS_NAME: string = 'live-edit-shader';

        private pageShader: Element;
        private northShader: Element;
        private eastShader: Element;
        private southShader: Element;
        private westShader: Element;

        private shaders: Element[];

        constructor() {
            this.pageShader = new DivEl(Shader.CLS_NAME + " page");
            this.northShader = new DivEl(Shader.CLS_NAME + " north");
            this.eastShader = new DivEl(Shader.CLS_NAME + " east");
            this.southShader = new DivEl(Shader.CLS_NAME + " south");
            this.westShader = new DivEl(Shader.CLS_NAME + " west");

            this.shaders = [this.pageShader, this.northShader, this.eastShader, this.southShader, this.westShader];

            var body = api.dom.Body.get();
            body.appendChildren.apply(body, this.shaders);

            this.shaders.forEach((shader: Element) => {
                shader.onClicked((event: MouseEvent) => this.handleClick(event));
                shader.onContextMenu((event: MouseEvent) => this.handleClick(event));
            });
        }

        shadeItemView(itemView: ItemView): void {

            if (!itemView) {
                this.hide();
                return;
            }

            if (itemView.getType().equals(api.liveedit.PageItemType.get())) {
                this.resizeToPage();
            } else {
                this.resizeToItemView(itemView);
            }
        }

        private handleClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            new ShaderClickedEvent().fire();
        }

        private resizeToPage(): void {
            this.pageShader.getEl().setTopPx(0).setRightPx(0).setBottomPx(0).setLeftPx(0);
            this.pageShader.show();
        }

        private resizeToItemView(itemView: ItemView): void {
            var documentSize = LiveEdit.DomHelper.getDocumentSize(),
                documentWidth = documentSize.width,
                documentHeight = documentSize.height;

            var dimensions = itemView.getElementDimensions(),
                x = dimensions.left,
                y = dimensions.top,
                w = dimensions.width,
                h = dimensions.height;

            this.northShader.getEl().
                setTopPx(0).
                setLeftPx(0).
                setWidthPx(documentWidth).
                setHeightPx(y);
            this.northShader.show();

            this.eastShader.getEl().
                setTopPx(y).
                setLeftPx(x + w).
                setWidthPx(documentWidth - (x + w)).
                setHeightPx(h);
            this.eastShader.show();

            this.southShader.getEl().
                setTopPx(y + h).
                setLeftPx(0).
                setWidthPx(documentWidth).
                setHeightPx(documentHeight - (y + h));
            this.southShader.show();

            this.westShader.getEl().
                setTopPx(y).
                setLeftPx(0).
                setWidthPx(x).
                setHeightPx(h);
            this.westShader.show();
        }

        hide(): void {
            this.shaders.forEach((shader: Element) => shader.hide());
        }
    }
}