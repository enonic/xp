import MixinNames = api.schema.mixin.MixinNames;

describe('api.schema.mixin.MixinNames', () => {

    describe('constructor', () => {

        it('given duplicates then Error is thrown', () => {

            expect(() => {

                MixinNames.create().fromStrings(['myapplication:duplicate', 'myapplication:duplicate']).build();

            }).toThrow(new Error("MixinNames do not allow duplicates, found: 'myapplication:duplicate'"));
        });
    });
});
