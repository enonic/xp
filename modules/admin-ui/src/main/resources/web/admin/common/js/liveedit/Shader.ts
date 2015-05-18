module api.liveedit {

    import Element = api.dom.Element;
    import DivEl = api.dom.DivEl;
    import Body = api.dom.Body;

    export class Shader {

        private static CLS_NAME: string = 'shader';

        private pageShader: Element;
        private northShader: Element;
        private eastShader: Element;
        private southShader: Element;
        private westShader: Element;

        private shaders: Element[];

        private clickListeners: {(event: MouseEvent): void}[] = [];
        private unlockClickedListeners: {(event: MouseEvent): void}[] = [];
        private mouseEnterListeners: {(event: MouseEvent): void}[] = [];
        private mouseLeaveListeners: {(event: MouseEvent): void}[] = [];

        private static INSTANCE: Shader;

        constructor() {
            this.pageShader = new DivEl(Shader.CLS_NAME + " page");
            this.northShader = new DivEl(Shader.CLS_NAME + " north");
            this.eastShader = new DivEl(Shader.CLS_NAME + " east");
            this.southShader = new DivEl(Shader.CLS_NAME + " south");
            this.westShader = new DivEl(Shader.CLS_NAME + " west");

            this.shaders = [this.pageShader, this.northShader, this.eastShader, this.southShader, this.westShader];

            var body = Body.get();
            body.appendChildren.apply(body, this.shaders);

            this.shaders.forEach((shader: Element) => {
                shader.onClicked((event: MouseEvent) => this.handleClick(event));
                shader.onContextMenu((event: MouseEvent) => this.handleClick(event));
                shader.onMouseEnter((event: MouseEvent) => this.notifyMouseEntered(event));
                shader.onMouseLeave((event: MouseEvent) => this.notifyMouseLeft(event));
            });
        }

        public static get(): Shader {
            if (!Shader.INSTANCE) {
                Shader.INSTANCE = new Shader();
            }
            return Shader.INSTANCE;
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

        hide(): void {
            this.shaders.forEach((shader: Element) => shader.hide());
        }

        onUnlockClicked(listener: (event: MouseEvent) => void) {
            this.unlockClickedListeners.push(listener);
        }

        unUnlockClicked(listener: (event: MouseEvent) => void) {
            this.unlockClickedListeners = this.unlockClickedListeners.filter((curr) => {
                return listener != curr;
            })
        }

        private notifyUnlockClicked(event: MouseEvent) {
            this.unlockClickedListeners.forEach((listener) => {
                listener(event);
            })
        }

        onMouseEnter(listener: (event: MouseEvent) => void) {
            this.mouseEnterListeners.push(listener);
        }

        unMouseEnter(listener: (event: MouseEvent) => void) {
            this.mouseEnterListeners = this.mouseEnterListeners.filter((curr) => {
                return listener != curr;
            })
        }

        private notifyMouseEntered(event: MouseEvent) {
            this.mouseEnterListeners.forEach((listener) => {
                listener(event);
            })
        }

        onMouseLeave(listener: (event: MouseEvent) => void) {
            this.mouseLeaveListeners.push(listener);
        }

        unMouseLeave(listener: (event: MouseEvent) => void) {
            this.mouseLeaveListeners = this.mouseLeaveListeners.filter((curr) => {
                return listener != curr;
            })
        }

        private notifyMouseLeft(event: MouseEvent) {
            this.mouseLeaveListeners.forEach((listener) => {
                listener(event);
            })
        }

        onClicked(listener: (event: MouseEvent) => void) {
            this.clickListeners.push(listener);
        }

        unClicked(listener: (event: MouseEvent) => void) {
            this.clickListeners = this.clickListeners.filter((curr) => {
                return listener != curr;
            })
        }

        private notifyClicked(event: MouseEvent) {
            this.clickListeners.forEach((listener) => {
                listener(event);
            })
        }

        private handleUnlockClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            this.notifyUnlockClicked(event);
        }

        private handleClick(event: MouseEvent) {
            event.stopPropagation();
            event.preventDefault();

            this.notifyClicked(event);
        }

        private resizeToPage(): void {
            this.pageShader.getEl().setTopPx(0).setRightPx(0).setBottomPx(0).setLeftPx(0);
            this.pageShader.show();
        }

        private resizeToItemView(itemView: ItemView): void {
            var win = api.dom.WindowDOM.get(),
                bodyEl = api.dom.Body.get().getEl(),    // check if body is bigger than window to account for scroll
                documentWidth = Math.max(win.getWidth(), bodyEl.getWidth()),
                documentHeight = Math.max(win.getHeight(), bodyEl.getHeight());

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
    }
}