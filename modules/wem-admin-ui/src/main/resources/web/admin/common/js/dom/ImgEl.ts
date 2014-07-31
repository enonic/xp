module api.dom {

    export class ImgEl extends Element {

        private loaded: boolean;
        private disableCache: boolean = true;

        /* 1px x 1px gif with a 1bit palette */
        static PLACEHOLDER = "data:image/gif;base64,R0lGODlhAQABAIAAAP///////yH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";

        constructor(src?: string, className?: string) {
            super(new NewElementBuilder().
                setTagName("img").
                setHelper(ImgHelper.create()).
                setClassName(className));
            this.getEl().setSrc(src ? src : ImgEl.PLACEHOLDER);
            this.onLoaded((event: UIEvent) => {
                this.loaded = true;
            })
        }

        refresh(): void {
            this.setSrc(this.getSrc());
        }

        getSrc(): string {
            return this.getEl().getSrc();
        }

        setSrc(source: string) {
            var src;
            if (this.disableCache) {
                var params = api.util.decodeUrlParams(source);
                params['time'] = new Date().getMilliseconds().toString();
                src = api.util.getUrlLocation(source) + api.util.encodeUrlParams(params);
            } else {
                src = source;
            }
            this.getEl().setSrc(src);
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

        setCacheDisabled(disabled: boolean) {
            this.disableCache = disabled;
        }

        isCacheDisabled(): boolean {
            return this.disableCache;
        }
    }
}
