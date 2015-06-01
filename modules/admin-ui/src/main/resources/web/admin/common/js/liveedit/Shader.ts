module api.liveedit {

    import Element = api.dom.Element;
    import DivEl = api.dom.DivEl;
    import Body = api.dom.Body;

    export class Shader {

        private static CLS_NAME: string = 'shader';

        private target: Element;
        private scrollEnabled: boolean = true;

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

        private static debug: boolean = true;

        constructor() {
            this.pageShader = new DivEl(Shader.CLS_NAME + " page");
            this.northShader = new DivEl(Shader.CLS_NAME + " north");
            this.eastShader = new DivEl(Shader.CLS_NAME + " east");
            this.southShader = new DivEl(Shader.CLS_NAME + " south");
            this.westShader = new DivEl(Shader.CLS_NAME + " west");

            this.shaders = [this.pageShader, this.northShader, this.eastShader, this.southShader, this.westShader];

            var body = Body.get();
            body.appendChildren.apply(body, this.shaders);
            body.onMouseWheel((event: MouseEvent) => {
                if (this.target && this.isVisible()) {
                    if (Shader.debug) {
                        console.log('Shader.onMouseWheel, scroll enabled = ' + this.scrollEnabled);
                    }
                    if (!this.scrollEnabled) {
                        // swallow event to prevent scrolling
                        event.preventDefault();
                        event.stopPropagation();
                    } else {
                        // give the page some time to reflect scroll
                        setTimeout(() => this.resizeToElement(this.target), 5);
                    }
                }
            });

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

        public setScrollEnabled(enabled: boolean): Shader {
            this.scrollEnabled = enabled;
            return this;
        }

        shade(element: Element): void {
            if (!element) {
                this.hide();
                return;
            }

            if (api.ObjectHelper.iFrameSafeInstanceOf(element, PageView)) {
                this.resizeToPage();
            } else {
                this.resizeToElement(element);
            }
        }

        hide(): void {
            this.target = undefined;
            this.shaders.forEach((shader: Element) => shader.hide());
        }

        isVisible(): boolean {
            return this.shaders.some((shader: Element) => shader.isVisible());
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

        private showShaderIfNecessary(shader: Element) {
            // show only shaders having both width and height
            var shaderEl = shader.getEl();
            shader.setVisible(shaderEl.getWidth() > 0 && shaderEl.getHeight() > 0);
        }

        private resizeToPage(): void {
            if (Shader.debug) {
                console.log('Shader.resizeToPage');
            }
            this.target = undefined;
            this.pageShader.getEl().setTopPx(0).setRightPx(0).setBottomPx(0).setLeftPx(0);
            this.pageShader.show();
        }

        private resizeToElement(element: Element): void {

            this.target = element;

            var win = api.dom.WindowDOM.get(),
                bodyEl = api.dom.Body.get().getEl(),
            // check if body is bigger than window to account for scroll
                documentWidth = Math.max(win.getWidth(), bodyEl.getWidth()),
                documentHeight = Math.max(win.getHeight(), bodyEl.getHeight());

            var dimensions = element.getEl().getDimensions(),
                x1 = Math.max(0, dimensions.left),
                y1 = Math.max(0, dimensions.top),
                x2 = Math.min(documentWidth, dimensions.left + dimensions.width),
                y2 = Math.min(documentHeight, dimensions.top + dimensions.height);

            if (Shader.debug) {
                console.log('Shader.resizeToElement(' + x1 + ', ' + y1 + ', ' + x2 + ', ' + y2 + ')', element);
            }

            this.northShader.getEl().
                setTopPx(0).
                setLeftPx(0).
                setWidthPx(documentWidth).
                setHeightPx(y1);
            this.showShaderIfNecessary(this.northShader);

            this.eastShader.getEl().
                setTopPx(y1).
                setLeftPx(x2).
                setWidthPx(documentWidth - x2).
                setHeightPx(y2 - y1);
            this.showShaderIfNecessary(this.eastShader);

            this.southShader.getEl().
                setTopPx(y2).
                setLeftPx(0).
                setWidthPx(documentWidth).
                setHeightPx(documentHeight - y2);
            this.showShaderIfNecessary(this.southShader);

            this.westShader.getEl().
                setTopPx(y1).
                setLeftPx(0).
                setWidthPx(x1).
                setHeightPx(y2 - y1);
            this.showShaderIfNecessary(this.westShader);
        }
    }
}