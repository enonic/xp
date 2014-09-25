describe("api.util.ClassHelper", function () {

    function namedFunction() {};
    var anonymousFunction = function () {};

    describe("tests for api.util.getFunctionName() function", function () {

        it("returns function name as string", function () {
            expect(api.util.getFunctionName(namedFunction)).toBe('namedFunction');
        });

        it("returns empty string for anonymous function", function () {
            expect(api.util.getFunctionName(anonymousFunction)).toBe('');
        });

    });

    describe("tests for api.util.getClassName() function", function () {

        it("returns class name for object", function () {
            var instance = new api.dom.ElementHelper(document.body);
            expect(api.util.getClassName(instance)).toBe("ElementHelper");
        });

        it("should return instance['constructor']['name']", function () {
            expect(api.util.getClassName(new function Some(){})).toBe("Some");
            expect(api.util.getClassName({})).toBe("Object");
        });

    });

    describe("tests for api.util.getModuleName() function", function () {

        it("returns full module path for class which given object is instance of", function () {
            var instance = new api.dom.ElementHelper(document.body);
            expect(api.util.getModuleName(instance)).toBe("api.dom");
        });

        it("returns full module path for given typescript class", function () {
            expect(api.util.getModuleName(api.dom.ElementHelper)).toBe("api.dom");
        });

        it("returns full module path for given exported function", function() {
            expect(api.util.getModuleName(api.util.getModuleName)).toBe('api.util');
        });

    });

    describe("tests for api.util.getFullName() function", function () {

        it("returns full name for class which given object is instance of", function () {
            var instance = new api.dom.ElementHelper(document.body);
            expect(api.util.getFullName(instance)).toBe("api.dom.ElementHelper");
        });

        it("returns full name for given typescript class", function () {
            expect(api.util.getFullName(api.dom.ElementHelper)).toBe("api.dom.ElementHelper");
        });

        it("returns full name for given exported function", function () {
            expect(api.util.getFullName(api.util.getFullName)).toBe('api.util.getFullName');
        });

        it("correctly resolves classes with equal names", function () {
            api.$package1 = { Class1: function Class1() {} };
            api.$package2 = { Class1: function Class1() {} };
            expect(api.util.getFullName(api.$package1.Class1)).toBe('api.$package1.Class1');
            expect(api.util.getFullName(api.$package2.Class1)).toBe('api.$package2.Class1');
        });

        it("returns empty string if function wasn't found", function () {
            expect(api.util.getFullName(function Class1() {})).toBe('');
        });

    });

});