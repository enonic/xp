import '../../api.ts';
import {IssueSummaryJson} from './IssueSummaryJson';

export class IssueSummary {

    private id: string;

    private title: string;

    private creator: string;

    private modifiedTime: Date;

    constructor(builder: IssueSummaryBuilder) {
        this.id = builder.id;
        this.title = builder.title;
        this.creator = builder.creator;
        this.modifiedTime = builder.modifiedTime;
    }

    static fromJson(json: IssueSummaryJson): IssueSummary {
        return new IssueSummaryBuilder().fromIssueSummaryJson(json).build();
    }

    static create(): IssueSummaryBuilder {
        return new IssueSummaryBuilder();
    }

    getId(): string {
        return this.id;
    }

    getTitle(): string {
        return this.title;
    }

    getCreator(): string {
        return this.creator;
    }

    getModifiedTime(): Date {
        return this.modifiedTime;
    }

}

export class IssueSummaryBuilder {

    id: string;

    title: string;

    creator: string;

    modifiedTime: Date;

    fromIssueSummaryJson(json: IssueSummaryJson): IssueSummaryBuilder {
        this.id = json.id;
        this.title = json.title;
        this.creator = json.creator;
        this.modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;

        return this;
    }

    setId(id: string): IssueSummaryBuilder {
        this.id = id;
        return this;
    }

    setTitle(title: string): IssueSummaryBuilder {
        this.title = title;
        return this;
    }

    setCreator(creator: string): IssueSummaryBuilder {
        this.creator = creator;
        return this;
    }

    setModifiedTime(modifiedTime: Date): IssueSummaryBuilder {
        this.modifiedTime = modifiedTime;
        return this;
    }

    build(): IssueSummary {
        return new IssueSummary(this);
    }
}
