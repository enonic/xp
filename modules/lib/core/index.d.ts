declare global {
    interface XpXData {
        [key: string]: Record<string, Record<string, unknown>>;
    }
}

export type UserKey<
	IdProvider extends string = string,
	Login extends string = string
> = `user:${IdProvider}:${Login}`;

export type GroupKey<
	IdProvider extends string = string,
	Name extends string = string
> = `group:${IdProvider}:${Name}`;

export type RoleKey<T extends string = string> = `role:${T}`;

export namespace Roles {
    // NOTE: These exist, but we don't want to "expose" them
    // export namespace Cms {
    //   export type Admin = RoleKey<'cms.admin'>;
    //   export namespace Cm {
    //       export type App = RoleKey<'cms.app'>;
    //   }
    //   export type Expert = RoleKey<'cms.expert'>;
    // }
    export namespace System {
        export type Admin = RoleKey<'system.admin'>;
        export namespace Admin {
            export type Login = RoleKey<'system.admin.login'>;
        }
        export type Auditlog = RoleKey<'system.auditlog'>;
        export type Authenticated = RoleKey<'system.authenticated'>;
        export type Everyone = RoleKey<'system.everyone'>;
        export namespace User {
            export type Admin = RoleKey<'system.user.admin'>;
            export type App = RoleKey<'system.user.app'>;
        }
    }
}

export namespace Users {
    export namespace System {
        export type Su = UserKey<'system','su'>;
    }
}

export type PrincipalKey =
    | UserKey
    | GroupKey
    | RoleKey
    // NOTE: These exist, but we don't want to "expose" them
    // | Roles.Cms.Admin
    // | Roles.Cms.Cm.App
    // | Roles.Cms.Expert
    | Roles.System.Admin
    | Roles.System.Admin.Login
    | Roles.System.Auditlog
    | Roles.System.Authenticated
    | Roles.System.Everyone
    | Roles.System.User.Admin
    | Roles.System.User.App
    | Users.System.Su
;

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
