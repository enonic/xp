module api.dom {

    export class ImgEl extends Element {

        private loaded: boolean;

        /* 1px x 1px gif with a 1bit palette */
        public static PLACEHOLDER = "data:image/gif;base64,R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";

        constructor(src?: string, className?: string) {
            super(new NewElementBuilder().
                setTagName("img").
                setHelper(ImgHelper.create()).
                setClassName(className));
            this.getEl().setSrc(src ? src : ImgEl.PLACEHOLDER);
            this.onLoaded((event: UIEvent) => {
                this.loaded = true;
            });
        }

        refresh(): void {
            this.setSrc(this.getSrc());
        }

        getSrc(): string {
            return this.getEl().getSrc();
        }

        setSrc(source: string) {
            this.loaded = false;
            this.getEl().setSrc(source);
        }

        getEl(): ImgHelper {
            return <ImgHelper>super.getEl();
        }

        onLoaded(listener: (event: UIEvent) => void) {
            this.getEl().addEventListener("load", listener);
        }

        unLoaded(listener: (event: UIEvent) => void) {
            this.getEl().removeEventListener("load", listener);
        }

        isLoaded(): boolean {
            return this.loaded;
        }
    }
}
