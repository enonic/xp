module app.publish {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentPath = api.content.ContentPath;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import PublishContentRequest = api.content.PublishContentRequest;
    import CompareStatus = api.content.CompareStatus;
    import ContentPublishItemJson = api.content.json.ContentPublishItemJson;
    import ContentName = api.content.ContentName;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ContentPublishItem implements api.Equitable {

        private id: string;

        private displayName: string;

        private path: ContentPath;

        private iconUrl: string;

        private compareStatus: CompareStatus;

        private name: ContentName;

        private type: api.schema.content.ContentTypeName;

        private valid: boolean;

        constructor(builder: ContentPublishItemBuilder) {
            this.displayName = builder.displayName;
            this.path = builder.path;
            this.iconUrl = builder.iconUrl;
            this.id = builder.id;
            this.name = builder.name;
            this.type = builder.type;
            this.valid = builder.valid;
            this.compareStatus = builder.compareStatus;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getCompareStatus(): api.content.CompareStatus {
            return this.compareStatus;
        }

        hasParent(): boolean {
            return this.path.hasParentContent();
        }

        getPath(): ContentPath {
            return this.path;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        getId(): string {
            return this.id;
        }

        getName(): ContentName {
            return this.name;
        }

        getType(): ContentTypeName {
            return this.type;
        }

        isValid(): boolean {
            return this.valid;
        }

        isImage(): boolean {
            return this.type.isImage();
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ContentPublishItem)) {
                return false;
            }

            var other = <ContentPublishItem>o;

            if (!api.ObjectHelper.stringEquals(this.id, other.id)) {
                return false;
            }
            if (!api.ObjectHelper.stringEquals(this.displayName, other.displayName)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.path, other.path)) {
                return false;
            }
            if (!api.ObjectHelper.stringEquals(this.iconUrl, other.iconUrl)) {
                return false;
            }
            if (this.compareStatus != other.compareStatus) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.name, other.name)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.type, other.type)) {
                return false;
            }
            if (!api.ObjectHelper.booleanEquals(this.valid, other.valid)) {
                return false;
            }

            return true;
        }

        /**
         * Builds array of ContentPublishItem[] from NewContentPublishItems.
         */
        static fromNewContentPublishItems(jsonItems: ContentPublishItemJson[]): ContentPublishItem[] {
            var array: ContentPublishItem[] = [];
            jsonItems.forEach((obj: ContentPublishItemJson) => {
                array.push(new ContentPublishItemBuilder().fromJson(obj).build());
            });
            return array;
        }

        /**
         * Builds array of ContentPublishItem[] from content summaries.
         */
        static buildPublishItemsFromContentSummaries(items: ContentSummary[]): ContentPublishItem[] {
            var array: ContentPublishItem[] = [];
            items.forEach((obj: ContentSummary) => {
                array.push(new ContentPublishItemBuilder().fromContentSummary(obj).build());
            });
            return array;
        }

        static buildPublishItemFromContentSummary(item: ContentSummary): ContentPublishItem {
            return new ContentPublishItemBuilder().fromContentSummary(item).build();
        }

    }

    export class ContentPublishItemBuilder {

        id: string;

        displayName: string;

        path: ContentPath;

        iconUrl: string;

        compareStatus: api.content.CompareStatus;

        name: ContentName;

        type: ContentTypeName;

        valid: boolean;

        constructor(source?: ContentPublishItem) {
            if (source) {
                this.id = source.getId();
                this.displayName = source.getDisplayName();
                this.path = source.getPath();
                this.iconUrl = source.getIconUrl();
                this.compareStatus = source.getCompareStatus();
                this.name = source.getName();
                this.type = source.getType();
                this.valid = source.isValid();
            }
        }

        fromJson(json: ContentPublishItemJson): ContentPublishItemBuilder {
            this.id = json.id;
            this.path = ContentPath.fromString(json.path);
            this.compareStatus = <CompareStatus>CompareStatus[json.compareStatus];

            this.displayName = json.displayName;
            this.iconUrl = json.iconUrl;

            this.name = ContentName.fromString(json.name);
            this.type = new ContentTypeName(json.type);
            this.valid = json.valid;

            return this;
        }

        fromContentSummary(contentSummary: ContentSummary): ContentPublishItemBuilder {
            this.id = contentSummary.getId();
            this.path = contentSummary.getPath();
            this.compareStatus = contentSummary.getContentState().isDefault() ? CompareStatus.NEW : CompareStatus.PENDING_DELETE;

            this.displayName = contentSummary.getDisplayName();
            this.iconUrl = contentSummary.getIconUrl();

            this.name = contentSummary.getName();
            this.type = contentSummary.getType();
            this.valid = contentSummary.isValid();

            return this;
        }

        setId(value: string): ContentPublishItemBuilder {
            this.id = value;
            return this;
        }

        setCompareStatus(value: api.content.CompareStatus): ContentPublishItemBuilder {
            this.compareStatus = value;
            return this;
        }

        setPath(path: ContentPath): ContentPublishItemBuilder {
            this.path = path;
            return this;
        }

        setDisplayName(value: string): ContentPublishItemBuilder {
            this.displayName = value;
            return this;
        }

        build(): ContentPublishItem {
            return new ContentPublishItem(this);
        }
    }
}
