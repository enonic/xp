module api.security {

    export interface GroupJson extends PrincipalJson {

        members?: string[];

    }
}