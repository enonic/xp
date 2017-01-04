module api.content.page {

    export class PageTemplateByDisplayNameComparator implements api.Comparator<PageTemplate> {

        compare(a: PageTemplate, b: PageTemplate): number {
            let firstName: string;
            let secondName: string;
            if (!a) {
                return 1;
            } else {
                firstName = a.getDisplayName() || '';
            }
            if (!b) {
                return -1;
            } else {
                secondName = b.getDisplayName() || '';
            }
            return firstName.localeCompare(secondName);
        }
    }
}