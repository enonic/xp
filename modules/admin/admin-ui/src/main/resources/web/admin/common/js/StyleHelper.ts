module api {

    export class StyleHelper {

        private static ADMIN_PREFIX = "xp-admin-";

        private static PAGE_EDITOR_PREFIX = "xp-page-editor-";

        static getAdminCls(cls: string, prefix: string = api.StyleHelper.ADMIN_PREFIX): string {
            return api.StyleHelper.getCls(cls, prefix);
        }

        static getPageEditorCls(cls: string, prefix: string = api.StyleHelper.PAGE_EDITOR_PREFIX): string {
            return api.StyleHelper.getCls(cls, prefix);
        }

        private static getCls(cls: string, prefix: string): string {
            if (api.StyleHelper.isPrefixed(cls, prefix)) {
                return cls;
            }
            return prefix + cls;
        }

        private static isPrefixed(cls: string, prefix: string): boolean {
            return cls.indexOf(prefix) == 0;
        }
    }
}