module api_content {

    export class ContentIconUrlResolver extends api_icon.IconUrlResolver<ContentIconUrlResolver,ContentSummary> {

        public getResourcePath(): api_rest.Path {
            return api_rest.Path.fromParent(this.getRestPath(), "content", "image" );
        }

        public resolve(icon: api_icon.Icon): string {

            return this.getResourcePath().toString() + "/" + icon.getBlobKey() + "?" + this.resolveQueryParams();
        }

        static default(): string {

            return api_util.getAdminUri("common/images/default_content.png");
        }
    }
}