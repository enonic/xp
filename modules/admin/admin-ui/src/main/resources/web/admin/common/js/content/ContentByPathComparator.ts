module api.content {

    export class ContentByPathComparator implements api.Comparator<ContentSummary> {

        compare(a: ContentSummary, b: ContentSummary): number {
            if (!a) {
                return 1;
            } else {
                var firstName = a.getPath().toString();
            }
            if (!b) {
                return -1;
            } else {
                var secondName = b.getPath().toString();
            }
            return firstName.localeCompare(secondName);
        }
    }
}