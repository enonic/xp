module api.dom {

    export class ImgEl extends Element {

        private loaded: boolean;

        private loadedListeners: {(event: UIEvent): void}[] = [];

        public static debug = false;

        /* 1px x 1px gif with a 1bit palette */
        public static PLACEHOLDER = "data:image/gif;base64,R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";

        constructor(src?: string, className?: string) {
            super(new NewElementBuilder().
                setTagName("img").
                setHelper(ImgHelper.create()).
                setClassName(className));
            this.getEl().setSrc(src ? src : ImgEl.PLACEHOLDER);
            this.onImgElLoaded((event: UIEvent) => {
                this.loaded = true;
                if (ImgEl.debug) {
                    console.log('ImgEl.onLoaded', this.getSrc(), this.loaded.toString());
                }
                this.notifyLoaded(event);
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

        unLoaded(listener: (event: UIEvent) => void) {
            this.loadedListeners = this.loadedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyLoaded(event: UIEvent) {
            this.loadedListeners.forEach(listener => listener(event));
        }

        private onImgElLoaded(listener: (event: UIEvent) => void) {
            this.getEl().addEventListener("load", listener);
        }

        isLoaded(): boolean {
            return this.loaded;
        }

        isPlaceholder(): boolean {
            return this.getCurrentSrc() == ImgEl.PLACEHOLDER;
        }
    }
}
