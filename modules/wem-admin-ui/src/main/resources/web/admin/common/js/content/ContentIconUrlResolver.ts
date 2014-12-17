module api.content {

    export class ContentIconUrlResolver extends api.icon.IconUrlResolver {

        private content: ContentSummary;

        private crop: boolean = true;

        setContent(value: ContentSummary): ContentIconUrlResolver {
            this.content = value;
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
            url = this.appendParam("crop", this.crop ? "true" : "false", url);
            return url;
        }

        static default(): string {

            return api.util.UriHelper.getAdminUri("common/images/default_content.png");
        }
    }
}