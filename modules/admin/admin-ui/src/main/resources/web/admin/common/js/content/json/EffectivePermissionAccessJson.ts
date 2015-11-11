module api.content.json {

    export interface EffectivePermissionAccessJson {

        count: number;

        users: EffectivePermissionMemberJson[];
    }
}