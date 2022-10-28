declare global {
    interface XpXData {
        [key: string]: Record<string, Record<string, unknown>>;
    }
}

export type UserKey = `user:${string}:${string}`;
export type GroupKey = `group:${string}:${string}`;
export type RoleKey = `role:${string}`;

export type PrincipalKey = UserKey | GroupKey | RoleKey;

export interface User {
    type: 'user';
    key: UserKey;
    displayName: string;
    modifiedTime: string;
    disabled?: boolean;
    email?: string;
    login: string;
    idProvider: string;
}

export interface Group {
    type: 'group';
    key: GroupKey;
    displayName: string;
    modifiedTime: string;
    description?: string;
}

export interface Role {
    type: 'role';
    key: RoleKey;
    displayName: string;
    modifiedTime: string;
    description?: string;
}

export type Principal = User | Group | Role;

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
    Page extends Component = Component,
    > {
    _id: string;
    _name: string;
    _path: string;
    _score: number;
    creator: UserKey;
    modifier: UserKey;
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
    page: Page;
    x: XpXData;
    attachments: Record<string, Attachment>;
    publish?: PublishInfo;
    workflow?: {
        state: 'IN_PROGRESS' | 'PENDING_APPROVAL' | 'REJECTED' | 'READY';
        checks?: Record<string, 'PENDING' | 'REJECTED' | 'APPROVED'>;
    };
    inherit?: ('CONTENT' | 'PARENT' | 'NAME' | 'SORT')[];
}

export interface Resource {
    getSize(): number;

    getTimestamp(): number;

    getStream(): object;

    exists(): boolean;
}
