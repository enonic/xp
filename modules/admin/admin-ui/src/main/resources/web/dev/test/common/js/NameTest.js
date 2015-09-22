describe("api.NameTest", function () {
    describe("ensureValidName", function () {
        it("should return valid name - 'test-name'", function () {
            expect(api.Name.ensureValidName(" Test Name ")).toBe("test-name");
        });
        it("should return empty result for empty string value", function () {
            expect(api.Name.ensureValidName("")).toBe("");
        });
        it("should return empty result for null value", function () {
            expect(api.Name.ensureValidName(null)).toBe("");
        });
        it("should return empty result for undefined value", function () {
            expect(api.Name.ensureValidName(undefined)).toBe("");
        });
    });
});