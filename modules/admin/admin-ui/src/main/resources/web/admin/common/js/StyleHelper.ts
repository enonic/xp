module api {

    export class StyleHelper {

        static COMMON_PREFIX = "xp-admin-";

        static PAGE_EDITOR_PREFIX = "xp-page-editor-";

        static getCls(cls: string): string {
            if (!clsPrefix) {
                return cls;
            }
            var clsArr = cls.trim().split(" ");
            clsArr.forEach((clsEl: string, index: number, arr: string[]) => {
                if (!api.StyleHelper.isPrefixed(clsEl, clsPrefix)) {
                    arr[index] = clsPrefix + clsEl;
                }
            });
            return clsArr.join(" ");
        }

        private static isPrefixed(cls: string, prefix: string): boolean {
            return cls.indexOf(prefix) == 0;
        }
    }
}