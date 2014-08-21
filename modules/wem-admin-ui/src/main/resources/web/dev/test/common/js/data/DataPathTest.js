describe("Tests for DataPath", function () {

    var DataPath = api.data.DataPath;
    var DataPathElement = api.data.DataPathElement;

    it("doesn't accept null in first constructor argument", function () {
        expect(function () {
            new DataPath([null]);
        }).toThrow();
    });

    it("doesn't accept DataPathElement's with empty name in first constructor argument", function () {
        expect(function () {
            new DataPath([new DataPathElement('', 0)]);
        }).toThrow();
    });

    it("is absolute if second constructor argument isn't set", function () {
        expect(new DataPath([]).isAbsolute()).toBeTruthy();
    });

    describe("tests DataPath.toString() method", function () {

        it("returns '.' when path has no elements and is absolute", function () {
            expect(new DataPath([]).toString()).toBe('.');
        });

        it("returns '' when path has no elements and isn't absolute", function () {
            expect(new DataPath([], false).toString()).toBe('');
        });

        it("joins elements and prefixes with '.' when path has elements and is absolute", function () {
            expect(new DataPath([new DataPathElement('first', 0), new DataPathElement('second', 0) ]).toString()).toBe('.first[0].second[0]');
        });

        it("doesn't append '.' when path has elements and isn't absolute", function () {
            expect(new DataPath([new DataPathElement('element', 0)], false).toString()).toBe('element[0]');
        });

    });

    describe("test for DataPath.fromString() function", function () {

        it("checks if path is absolute and splits elements", function () {
            var path = DataPath.fromString('.first.second');

            expect(path.isAbsolute()).toBeTruthy();
            expect(path.elementCount()).toBe(2);
            expect(path.toString()).toBe('.first[0].second[0]');

            path = DataPath.fromString('first');
            expect(path.isAbsolute()).toBeFalsy();
        });

        it("removes empty elements", function () {
            var path = DataPath.fromString('..first...second..');

            expect(path.elementCount()).toBe(2);
            expect(path.toString()).toBe('.first[0].second[0]');
        });

    });

    it(".fromParent() function", function () {
        var parent = DataPath.fromString('.first'),
            path = DataPath.fromParent(parent, DataPathElement.fromString('second'));

        expect(path.elementCount()).toBe(parent.elementCount() + 1);
        expect(path.isAbsolute()).toBe(parent.isAbsolute());
        expect(path.toString()).toBe('.first[0].second[0]');
    });

    it(".fromPathElement() function", function () {
        var path = DataPath.fromPathElement(DataPathElement.fromString('element'));

        expect(path.isAbsolute()).toBeTruthy();
        expect(path.elementCount()).toBe(1);
        expect(path.toString()).toBe('.element[0]');
    });

    xit(".newWithoutFirstElement() method", function () {
        var original = DataPath.fromString('.first.second'),
            newPath = original.newWithoutFirstElement();

        expect(newPath.elementCount()).toBe(original.elementCount() - 1);
        expect(newPath.toString()).toBe('.second[0]');
    });

    describe('tests for DataPath.getParentPath() method', function () {

        it("returns null for root path", function () {
            var path = DataPath.fromString('.');

            expect(path.getParentPath()).toBeNull();
        });

        it("returns path without last element", function () {
            var path = DataPath.fromString('.first.second');

            expect(path.getParentPath().elementCount()).toBe(path.elementCount() - 1);
            expect(path.getParentPath().toString()).toBe('.first[0]');
        });

    });

    describe("tests for DataPath.asNewWithoutFirstPathElement() method", function () {

        it("returns new path without first element", function () {
            var path = DataPath.fromString('.first.second'),
                newPath = path.asNewWithoutFirstPathElement();

            expect(newPath.isAbsolute()).toBe(path.isAbsolute());
            expect(newPath.elementCount()).toBe(path.elementCount() - 1);
            expect(newPath.toString()).toBe('.second[0]');
        });

        it("throws an exception if path contains one or no elements", function () {
            var path = DataPath.fromString('.first');

            expect(function() {
                path.asNewWithoutFirstPathElement();
            }).toThrow();
        });

        it("returns relative path if parent is relative", function () {
            var path = DataPath.fromString('first.second'),
                newPath = path.asNewWithoutFirstPathElement();

            expect(newPath.isAbsolute()).toBe(path.isAbsolute());
            expect(newPath.isAbsolute()).toBeFalsy();
        });

    });

});

describe("Tests for DataPathElement", function () {

    var DataPathElement = api.data.DataPathElement;

    it('.toString() method returns element name followed by index inside brackets', function () {
        expect(new DataPathElement('element', 1).toString()).toBe('element[1]');
    });

    describe("tests for DataPathElement.fromString() function", function () {

        it("splits string to name and index", function () {
            var element = DataPathElement.fromString('element[1]');
            expect(element.getName()).toBe('element');
            expect(element.getIndex()).toBe(1);
        });

        it("sets index to 0 if not specified", function () {
            expect(DataPathElement.fromString('element').getIndex()).toBe(0);
        });

    })
});