module api.dom {

    export class ImgEl
        extends Element {

        private loaded: boolean;

        private loadedListeners: { (event: UIEvent): void }[] = [];

        private errorListeners: { (event: UIEvent): void }[] = [];

        public static debug: boolean = false;

        /* 1px x 1px gif with a 1bit palette */
        public static PLACEHOLDER: string = 'data:image/gif;base64,R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==';

        constructor(src?: string, className?: string, usePlaceholder: boolean = false) {
            super(new NewElementBuilder().setTagName('img').setHelper(ImgHelper.create()).setClassName(className));

            if (src || usePlaceholder) {
                this.getEl().setSrc(src ? src : ImgEl.PLACEHOLDER);
            }
            this.onImgElLoaded((event: UIEvent) => {
                this.loaded = true;
                if (ImgEl.debug) {
                    console.log('ImgEl.onLoaded', this.getSrc(), this.loaded.toString());
                }
                this.notifyLoaded(event);
            });
            this.onImgElError((event: UIEvent) => {
                this.loaded = false;
                this.notifyError(event);
            });
        }

        refresh(): void {
            this.setSrc(this.getSrc());
        }

        getSrc(): string {
            return this.getEl().getSrc();
        }

        getCurrentSrc(): string {
            return this.getEl().getCurrentSrc();
        }

        setSrc(source: string) {
            this.loaded = false;
            if (ImgEl.debug) {
                console.log('ImgEl.setSrc', this.getSrc(), source, this.loaded.toString());
            }
            this.getEl().setSrc(source);
        }

        getEl(): ImgHelper {
            return <ImgHelper>super.getEl();
        }

        onLoaded(listener: (event: UIEvent) => void) {
            this.loadedListeners.push(listener);
        }

        onError(listener: (event: UIEvent) => void) {
            this.errorListeners.push(listener);
        }

        unLoaded(listener: (event: UIEvent) => void) {
            this.loadedListeners = this.loadedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        unError(listener: (event: UIEvent) => void) {
            this.errorListeners = this.errorListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyLoaded(event: UIEvent) {
            this.loadedListeners.forEach(listener => listener(event));
        }

        private notifyError(event: UIEvent) {
            this.errorListeners.forEach(listener => listener(event));
        }

        private onImgElLoaded(listener: (event: UIEvent) => void) {
            this.getEl().addEventListener('load', listener);
        }

        private onImgElError(listener: (event: UIEvent) => void) {
            this.getEl().addEventListener('error', listener);
        }

        isLoaded(): boolean {
            return this.loaded;
        }

        isPlaceholder(): boolean {
            return this.getCurrentSrc() === ImgEl.PLACEHOLDER;
        }

        getHTMLElement(): HTMLImageElement {
            return <HTMLImageElement> super.getHTMLElement();
        }
    }
}
