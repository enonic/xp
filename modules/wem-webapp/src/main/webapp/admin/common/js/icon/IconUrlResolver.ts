module api_icon {

    export class IconUrlResolver<T,SUBJECT> {

        private restPath: api_rest.Path;

        private size: number;

        private thumbnail: boolean;

        constructor() {
            this.size = null;
            this.thumbnail = false;
            this.restPath = api_rest.Path.fromString(api_util.getRestUri(""));
        }

        public setSize(value: number): IconUrlResolver<T,SUBJECT> {
            this.size = value;
            return <IconUrlResolver<T,SUBJECT>>this;
        }

        public setThumbnail(value: boolean): IconUrlResolver<T,SUBJECT> {
            this.thumbnail = value;
            return <IconUrlResolver<T,SUBJECT>>this;
        }

        public resolve(icon: api_icon.Icon): string {
            throw Error("Function resolve must be overridden by inheritor" + api_util.getClassName(this));
        }

        public resolveQueryParams(): string {
            var str = "";
            if (this.size != null) {
                str += "size=" + this.size;
            }

            if (this.thumbnail != null) {
                str += "thumbnail=" + this.thumbnail;
            }
            return str;
        }

        public getRestPath(): api_rest.Path {
            return this.restPath;
        }

        public getResourcePath(): api_rest.Path {
            throw Error("Function getResourcePath must be overridden by inheritor" + api_util.getClassName(this));
        }
    }

}
