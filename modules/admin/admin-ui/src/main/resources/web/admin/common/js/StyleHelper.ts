module api {

    export class StyleHelper {

        static COMMON_PREFIX = "xp-admin-";

        static PAGE_EDITOR_PREFIX = "xp-page-editor-";

        static getCls(cls: string): string {
            if (!clsPrefix || api.StyleHelper.isPrefixed(cls, clsPrefix)) {
                return cls;
            }

            return clsPrefix + cls;
        }

        private static isPrefixed(cls: string, prefix: string): boolean {
            return cls.indexOf(prefix) == 0;
        }
    }
}