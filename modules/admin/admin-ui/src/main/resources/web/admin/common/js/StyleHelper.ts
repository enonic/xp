module api {

    export class StyleHelper {

        static COMMON_PREFIX = "xp-admin-";

        static PAGE_EDITOR_PREFIX = "xp-page-editor-";

        static ICON_PREFIX = "icon-";

        static getCls(cls: string, prefix: string = clsPrefix): string {
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

        static getCommonCls(cls: string): string {
            return api.StyleHelper.getCls(cls, StyleHelper.COMMON_PREFIX);
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