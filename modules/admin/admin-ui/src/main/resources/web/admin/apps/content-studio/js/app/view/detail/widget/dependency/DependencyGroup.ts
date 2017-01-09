import "../../../../../api.ts";

import ContentDependencyGroupJson = api.content.json.ContentDependencyGroupJson;
import ContentTypeName = api.schema.content.ContentTypeName;

export enum DependencyType {
    INBOUND,
    OUTBOUND
}

export class DependencyGroup implements api.Equitable {

    private itemCount: number;

    private iconUrl: string;

    private contentType: ContentTypeName;

    private type: DependencyType;

    constructor(builder: DependencyGroupBuilder) {
        this.itemCount = builder.itemCount;
        this.iconUrl = builder.iconUrl;
        this.contentType = builder.contentType;
        this.type = builder.type;
    }

    getItemCount(): number {
        return this.itemCount;
    }

    getIconUrl(): string {
        return this.iconUrl;
    }

    getContentType(): ContentTypeName {
        return this.contentType;
    }

    getName(): string {
        return this.contentType.toString();
    }

    getType(): string {
        return DependencyType[this.type];
    }

    equals(o: api.Equitable): boolean {

        if (!api.ObjectHelper.iFrameSafeInstanceOf(o, DependencyGroup)) {
            return false;
        }

        let other = <DependencyGroup>o;

        if (!api.ObjectHelper.numberEquals(this.itemCount, other.itemCount)) {
            return false;
        }
        if (!api.ObjectHelper.equals(this.contentType, other.contentType)) {
            return false;
        }
        if (!api.ObjectHelper.stringEquals(DependencyType[this.type], DependencyType[other.type])) {
            return false;
        }

        return true;
    }

    static fromDependencyGroupJson(type: DependencyType, jsonItems: ContentDependencyGroupJson[]): DependencyGroup[] {
        let array: DependencyGroup[] = [];
        jsonItems.forEach((obj: ContentDependencyGroupJson) => {
            array.push(new DependencyGroupBuilder().fromJson(obj).setType(type).build());
        });
        return array;
    }

}

export class DependencyGroupBuilder {

    itemCount: number;

    iconUrl: string;

    contentType: api.schema.content.ContentTypeName;

    type: DependencyType;

    constructor(source?: DependencyGroup) {
        if (source) {
            this.itemCount = source.getItemCount();
            this.iconUrl = source.getIconUrl();
            this.contentType = source.getContentType();
        }
    }

    fromJson(json: ContentDependencyGroupJson): DependencyGroupBuilder {
        this.itemCount = json.count;
        this.iconUrl = json.iconUrl;
        this.contentType = new ContentTypeName(json.type);

        return this;
    }

    setType(value: DependencyType): DependencyGroupBuilder {
        this.type = value;
        return this;
    }

    build(): DependencyGroup {
        return new DependencyGroup(this);
    }
}
