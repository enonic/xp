import PropertyPathElement = api.data.PropertyPathElement;

describe("api.data.PropertyPath", () => {

    it("doesn't accept null in first constructor argument", () => {
        expect(() => {
            // tslint:disable-next-line:no-unused-new
            new PropertyPath([null]);
        }).toThrow();
    });

    it("doesn't accept PropertyPathElement's with empty name in first constructor argument", () => {
        expect(() => {
            // tslint:disable-next-line:no-unused-new
            new PropertyPath([new PropertyPathElement('', 0)]);
        }).toThrow();
    });

    it("is absolute if second constructor argument isn't set", () => {
        expect(new PropertyPath([]).isAbsolute()).toBeTruthy();
    });

    describe("tests PropertyPath.toString() method", () => {

        it("returns '.' when path has no elements and is absolute", () => {
            expect(new PropertyPath([]).toString()).toBe('.');
        });

        it("returns '' when path has no elements and isn't absolute", () => {
            expect(new PropertyPath([], false).toString()).toBe('');
        });

        it("joins elements and prefixes with '.' when path has elements and is absolute", () => {
            expect(new PropertyPath([new PropertyPathElement('first', 0),
                new PropertyPathElement('second', 0)]).toString()).toBe('.first.second');
        });

        it("doesn't append '.' when path has elements and isn't absolute", () => {
            expect(new PropertyPath([new PropertyPathElement('element', 0)], false).toString()).toBe('element');
        });

    });

    describe("test for PropertyPath.fromString() function", () => {

        it("applicationKey", () => {
            let path = PropertyPath.fromString('applicationKey');

            expect(path.elementCount()).toBe(1);
            expect(path.toString()).toBe('applicationKey');
        });

        it("checks if path is absolute and splits elements", () => {
            let path = PropertyPath.fromString('.first.second');

            expect(path.isAbsolute()).toBeTruthy();
            expect(path.elementCount()).toBe(2);
            expect(path.toString()).toBe('.first.second');

            path = PropertyPath.fromString('first');
            expect(path.isAbsolute()).toBeFalsy();
        });

        it("removes empty elements", () => {
            let path = PropertyPath.fromString('..first...second..');

            expect(path.elementCount()).toBe(2);
            expect(path.toString()).toBe('.first.second');
        });

        it("sets index to 0 if not specified", () => {
            let path = PropertyPath.fromString('first.second');
            expect(path.getElement(0).getIndex()).toBe(0);
            expect(path.getElement(1).getIndex()).toBe(0);
        });

    });

    it(".fromParent() function", () => {
        let parent = PropertyPath.fromString('.first');
        let path = PropertyPath.fromParent(parent, PropertyPathElement.fromString('second'));

        expect(path.elementCount()).toBe(parent.elementCount() + 1);
        expect(path.isAbsolute()).toBe(parent.isAbsolute());
        expect(path.toString()).toBe('.first.second');
    });

    it(".fromPathElement() function", () => {
        let path = PropertyPath.fromPathElement(PropertyPathElement.fromString('element'));

        expect(path.isAbsolute()).toBeTruthy();
        expect(path.elementCount()).toBe(1);
        expect(path.toString()).toBe('.element');
    });

    it(".removeFirstPathElement() method", () => {
        let original = PropertyPath.fromString('.first.second');
        let newPath = original.removeFirstPathElement();

        expect(newPath.elementCount()).toBe(original.elementCount() - 1);
        expect(newPath.toString()).toBe('.second');
    });

    describe('tests for PropertyPath.getParentPath() method', () => {

        it("returns null for root path", () => {
            let path = PropertyPath.fromString('.');

            expect(path.getParentPath()).toBeNull();
        });

        it("returns path without last element", () => {
            let path = PropertyPath.fromString('.first.second');

            expect(path.getParentPath().elementCount()).toBe(path.elementCount() - 1);
            expect(path.getParentPath().toString()).toBe('.first');
        });

    });

    describe("tests for PropertyPath.removeFirstPathElement() method", () => {

        it("returns new path without first element", () => {
            let path = PropertyPath.fromString('.first.second');
            let newPath = path.removeFirstPathElement();

            expect(newPath.isAbsolute()).toBe(path.isAbsolute());
            expect(newPath.elementCount()).toBe(path.elementCount() - 1);
            expect(newPath.toString()).toBe('.second');
        });

        it("throws an exception if path contains one or no elements", () => {
            let path = PropertyPath.fromString('.first');

            expect(path.removeFirstPathElement).toThrow();
        });

        it("returns relative path if parent is relative", () => {
            let path = PropertyPath.fromString('first.second');
            let newPath = path.removeFirstPathElement();

            expect(newPath.isAbsolute()).toBe(path.isAbsolute());
            expect(newPath.isAbsolute()).toBeFalsy();
        });

    });

});

describe("api.data.PropertyPathElement", () => {

    it('.toString() method returns element name followed by index inside brackets', () => {
        expect(new PropertyPathElement('element', 1).toString()).toBe('element[1]');
    });

    it('.toString() method returns element name followed without index inside brackets when index is zero', () => {
        expect(new PropertyPathElement('element', 0).toString()).toBe('element');
    });

    describe("tests for PropertyPathElement.fromString() function", () => {

        it("splits string to name and index", () => {
            let element = PropertyPathElement.fromString('element[1]');
            expect(element.getName()).toBe('element');
            expect(element.getIndex()).toBe(1);
        });

        it("sets index to 0 if not specified", () => {
            expect(PropertyPathElement.fromString('element').getIndex()).toBe(0);
        });

    });
});
