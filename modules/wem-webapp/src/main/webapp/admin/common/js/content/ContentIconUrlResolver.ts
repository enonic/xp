module api_content {

    export class ContentIconUrlResolver extends api_icon.IconUrlResolver<ContentIconUrlResolver,ContentSummary> {

        public getResourcePath(): api_rest.Path {
            return api_rest.Path.fromString("content/image");
        }

        public resolve(icon: api_icon.Icon): string {

            return this.toRestUrl(this.getResourcePath()) + "/" + icon.getBlobKey() + "?" + this.resolveQueryParams();
        }

        static default(): string {

            return api_util.getAdminUri("common/images/default_content.png");
        }
    }
}