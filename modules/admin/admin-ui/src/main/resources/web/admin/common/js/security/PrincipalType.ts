module api.security {

    export enum PrincipalType {
        USER,
        GROUP,
        ROLE
    }

    export class PrincipalTypeUtil {
        public static typesToStrings(types: PrincipalType[]): string[] {
            return types.map((type: PrincipalType) => {
                return PrincipalType[type].toUpperCase();
            });
        }
    }
}
