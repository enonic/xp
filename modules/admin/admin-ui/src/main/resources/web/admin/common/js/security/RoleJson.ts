module api.security {

    export interface RoleJson extends PrincipalJson {

        members?: string[];

    }
}