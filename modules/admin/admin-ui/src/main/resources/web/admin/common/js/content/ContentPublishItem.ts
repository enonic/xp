module api.content {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentPath = api.content.ContentPath;
    import ContentSummary = api.content.ContentSummary;
    import ContentState = api.schema.content.ContentState;
    import ContentStateEnum = api.schema.content.ContentStateEnum;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import DialogButton = api.ui.dialog.DialogButton;
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

        static fromJson(json: ContentPublishItemJson): ContentPublishItem {
            return new ContentPublishItemBuilder().fromJson(json).build();
        }

        toContentSummaryAndCompareStatus(): ContentSummaryAndCompareStatus {
            let summary = new ContentSummaryBuilder()
                .setId(this.id)
                .setContentId(new ContentId(this.id))
                .setPath(this.path)
                .setDisplayName(this.displayName)
                .setContentState(new ContentState(this.compareStatus == CompareStatus.PENDING_DELETE
                    ? ContentStateEnum.PENDING_DELETE
                    : ContentStateEnum.DEFAULT))
                .setIconUrl(this.iconUrl)
                .setName(this.name)
                .setType(this.type)
                .setValid(this.valid)
                .build();

            return new ContentSummaryAndCompareStatus().setCompareStatus(this.compareStatus).setContentSummary(summary);
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
            this.displayName = json.displayName;
            this.path = ContentPath.fromString(json.path);
            this.iconUrl = json.iconUrl;
            this.compareStatus = <CompareStatus>CompareStatus[json.compareStatus];
            this.name = ContentName.fromString(json.name);
            this.type = new ContentTypeName(json.type);
            this.valid = json.valid;
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
