module api {

    export class StyleHelper {

        static COMMON_PREFIX = "xp-admin-common-";

        static ADMIN_PREFIX = "xp-admin-";

        static PAGE_EDITOR_PREFIX = "xp-page-editor-";

        static ICON_PREFIX = "icon-";

        static currentPrefix = "";

        static setCurrentPrefix(prefix: string) {
            api.StyleHelper.currentPrefix = prefix;
        }

        static getCurrentPrefix(): string {
            return api.StyleHelper.currentPrefix;
        }

        static getCls(cls: string, prefix: string = api.StyleHelper.currentPrefix): string {
            if (!prefix) {
                return cls;
            }
            var clsArr = cls.trim().split(" ");
            clsArr.forEach((clsEl: string, index: number, arr: string[]) => {
                if (!api.StyleHelper.isPrefixed(clsEl, prefix)) {
                    arr[index] = prefix + clsEl;
                }
            });
            return clsArr.join(" ");
        }

        static getIconCls(iconCls: string): string {
            return api.StyleHelper.getCls(StyleHelper.ICON_PREFIX + iconCls);
        }

        static getCommonIconCls(iconCls: string): string {
            return api.StyleHelper.getCls(StyleHelper.ICON_PREFIX + iconCls, StyleHelper.COMMON_PREFIX);
        }

        private static isPrefixed(cls: string, prefix: string): boolean {
            return cls.indexOf(prefix) == 0;
        }
    }
}