module api.security {

    export interface FindPrincipalsResultJson {

        principals: api.security.PrincipalJson[];

        totalSize: number;
    }
}
