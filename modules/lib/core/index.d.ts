declare global {
    interface XpXData {
    }
}

export type UserKey = `user:${string}:${string}`;
export type GroupKey = `group:${string}:${string}`;
export type RoleKey = `role:${string}`;

export type PrincipalKey = UserKey | GroupKey | RoleKey;

export type WorkflowState = 'IN_PROGRESS' | 'PENDING_APPROVAL' | 'REJECTED' | 'READY';

export type WorkflowCheckState = 'PENDING' | 'REJECTED' | 'APPROVED';

export type ContentInheritType = 'CONTENT' | 'PARENT' | 'NAME' | 'SORT';

export interface Workflow {
    state: WorkflowState;
    checks?: Record<string, WorkflowCheckState>;
}

export interface Attachment {
    name: string;
    label?: string;
    size: number;
    mimeType: string;
}

export interface PublishInfo {
    from?: string;
    to?: string;
    first?: string;
}

export interface Component<Config extends object = object, Regions extends Record<string, Region> = Record<string, Region>> {
    config: Config;
    descriptor: string;
    path: string;
    type: 'page' | 'layout' | 'part';
    regions: Regions;
}

export interface Region<Config extends object = object> {
    name: string;
    components: Component<Config>[];
}

export interface Content<
    Data = Record<string, unknown>,
    Type extends string = string,
    Config extends object = object,
    Regions extends Record<string, Region> = Record<string, Region>,
    > {
    _id: string;
    _name: string;
    _path: string;
    _score: number;
    creator: string;
    modifier: string;
    createdTime: string;
    modifiedTime: string;
    owner: string;
    data: Data;
    type: Type;
    displayName: string;
    hasChildren: boolean;
    language: string;
    valid: boolean;
    originProject: string;
    childOrder?: string;
    _sort?: object[];
    page: Component<Config, Regions>;
    x: XpXData;
    attachments: Record<string, Attachment>;
    publish?: PublishInfo;
    workflow?: Workflow;
    inherit?: ContentInheritType[];
}
