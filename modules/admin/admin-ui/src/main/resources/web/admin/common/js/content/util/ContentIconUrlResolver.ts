import UriHelper = api.util.UriHelper;

module api.content.util {

    export class ContentIconUrlResolver extends api.icon.IconUrlResolver {

        private content: ContentSummary;

        private crop: boolean;

        private size: number;

        setContent(value: ContentSummary): ContentIconUrlResolver {
            this.content = value;
            return this;
        }

        setSize(value: number): ContentIconUrlResolver {
            this.size = value;
            return this;
        }

        setCrop(value: boolean): ContentIconUrlResolver {
            this.crop = value;
            return this;
        }

        resolve(): string {

            var url = this.content.getIconUrl();
            if (!url) {
                return null;
            }
            // CMS-4677: using crop=false for images only by default
            if (this.crop == undefined) {
                this.crop = !this.content.isImage();
            }

            // parse existing params from url in case there are any
            let params = UriHelper.decodeUrlParams(url);

            if (this.crop != undefined) {
                params['crop'] = String(this.crop);
            }
            if (this.size != undefined) {
                params['size'] = String(this.size);
            }

            return `${UriHelper.trimUrlParams(url)}?${UriHelper.encodeUrlParams(params)}`;
        }

        static default(): string {

            return api.util.UriHelper.getAdminUri("common/images/default_content.png");
        }
    }
}