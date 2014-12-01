describe("api.data.PropertyPathTest", function () {

    var PropertyPath = api.data.PropertyPath;
    var PropertyPathElement = api.data.PropertyPathElement;

    it("doesn't accept null in first constructor argument", function () {
        expect(function () {
            new PropertyPath([null]);
        }).toThrow();
    });

    it("doesn't accept PropertyPathElement's with empty name in first constructor argument", function () {
        expect(function () {
            new PropertyPath([new PropertyPathElement('', 0)]);
        }).toThrow();
    });

    it("is absolute if second constructor argument isn't set", function () {
        expect(new PropertyPath([]).isAbsolute()).toBeTruthy();
    });

    describe("tests PropertyPath.toString() method", function () {

        it("returns '.' when path has no elements and is absolute", function () {
            expect(new PropertyPath([]).toString()).toBe('.');
        });

        it("returns '' when path has no elements and isn't absolute", function () {
            expect(new PropertyPath([], false).toString()).toBe('');
        });

        it("joins elements and prefixes with '.' when path has elements and is absolute", function () {
            expect(new PropertyPath([new PropertyPathElement('first', 0),
                new PropertyPathElement('second', 0)]).toString()).toBe('.first.second');
        });

        it("doesn't append '.' when path has elements and isn't absolute", function () {
            expect(new PropertyPath([new PropertyPathElement('element', 0)], false).toString()).toBe('element');
        });

    });

    describe("test for PropertyPath.fromString() function", function () {

        it("moduleKey", function () {
            var path = PropertyPath.fromString('moduleKey');

            expect(path.elementCount()).toBe(1);
            expect(path.toString()).toBe('moduleKey');
        });

        it("checks if path is absolute and splits elements", function () {
            var path = PropertyPath.fromString('.first.second');

            expect(path.isAbsolute()).toBeTruthy();
            expect(path.elementCount()).toBe(2);
            expect(path.toString()).toBe('.first.second');

            path = PropertyPath.fromString('first');
            expect(path.isAbsolute()).toBeFalsy();
        });

        it("removes empty elements", function () {
            var path = PropertyPath.fromString('..first...second..');

            expect(path.elementCount()).toBe(2);
            expect(path.toString()).toBe('.first.second');
        });

        it("sets index to 0 if not specified", function () {
            var path = PropertyPath.fromString('first.second');
            expect(path.getElement(0).getIndex()).toBe(0);
            expect(path.getElement(1).getIndex()).toBe(0);
        });

    });

    it(".fromParent() function", function () {
        var parent = PropertyPath.fromString('.first'),
            path = PropertyPath.fromParent(parent, PropertyPathElement.fromString('second'));

        expect(path.elementCount()).toBe(parent.elementCount() + 1);
        expect(path.isAbsolute()).toBe(parent.isAbsolute());
        expect(path.toString()).toBe('.first.second');
    });

    it(".fromPathElement() function", function () {
        var path = PropertyPath.fromPathElement(PropertyPathElement.fromString('element'));

        expect(path.isAbsolute()).toBeTruthy();
        expect(path.elementCount()).toBe(1);
        expect(path.toString()).toBe('.element');
    });

    it(".removeFirstPathElement() method", function () {
        var original = PropertyPath.fromString('.first.second'),
            newPath = original.removeFirstPathElement();

        expect(newPath.elementCount()).toBe(original.elementCount() - 1);
        expect(newPath.toString()).toBe('.second');
    });

    describe('tests for PropertyPath.getParentPath() method', function () {

        it("returns null for root path", function () {
            var path = PropertyPath.fromString('.');

            expect(path.getParentPath()).toBeNull();
        });

        it("returns path without last element", function () {
            var path = PropertyPath.fromString('.first.second');

            expect(path.getParentPath().elementCount()).toBe(path.elementCount() - 1);
            expect(path.getParentPath().toString()).toBe('.first');
        });

    });

    describe("tests for PropertyPath.removeFirstPathElement() method", function () {

        it("returns new path without first element", function () {
            var path = PropertyPath.fromString('.first.second'),
                newPath = path.removeFirstPathElement();

            expect(newPath.isAbsolute()).toBe(path.isAbsolute());
            expect(newPath.elementCount()).toBe(path.elementCount() - 1);
            expect(newPath.toString()).toBe('.second');
        });

        it("throws an exception if path contains one or no elements", function () {
            var path = PropertyPath.fromString('.first');

            expect(function () {
                path.removeFirstPathElement();
            }).toThrow();
        });

        it("returns relative path if parent is relative", function () {
            var path = PropertyPath.fromString('first.second'),
                newPath = path.removeFirstPathElement();

            expect(newPath.isAbsolute()).toBe(path.isAbsolute());
            expect(newPath.isAbsolute()).toBeFalsy();
        });

    });

});

describe("api.data.PropertyPathElementTest", function () {

    var PropertyPathElement = api.data.PropertyPathElement;

    it('.toString() method returns element name followed by index inside brackets', function () {
        expect(new PropertyPathElement('element', 1).toString()).toBe('element[1]');
    });

    it('.toString() method returns element name followed without index inside brackets when index is zero', function () {
        expect(new PropertyPathElement('element', 0).toString()).toBe('element');
    });

    describe("tests for PropertyPathElement.fromString() function", function () {

        it("splits string to name and index", function () {
            var element = PropertyPathElement.fromString('element[1]');
            expect(element.getName()).toBe('element');
            expect(element.getIndex()).toBe(1);
        });

        it("sets index to 0 if not specified", function () {
            expect(PropertyPathElement.fromString('element').getIndex()).toBe(0);
        });

    })
});