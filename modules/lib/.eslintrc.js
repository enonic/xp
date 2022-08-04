module.exports = {
    extends: [
        '@enonic/eslint-config',
        '@enonic/eslint-config/xp',
    ],
    overrides: [
        {
            files: ['*.ts'],
            parserOptions: {
                tsconfigRootDir: __dirname,
                project: './tsconfig.json',
            },
            rules: {
                '@typescript-eslint/no-var-requires': ['off'],
            },
        },
        {
            files: ['*.d.ts'],
            parserOptions: {
                tsconfigRootDir: __dirname,
                project: './tsconfig.json',
            },
            rules: {
                '@typescript-eslint/triple-slash-reference': ['off'],
                '@typescript-eslint/no-empty-interface': ['off'],
                'spaced-comment': ['error', 'always', {'markers': ['/']}],
            },
        },
    ],
    ignorePatterns: [
        '**/out',
        '**/build',
        'jsdoc/global.js',
        'lib-*/**/*.d.ts',
        '**/src/main/resources/lib/xp/*.js',
        '**/src/test/**/*.js',
        '**/src/main/resources/lib/xp/examples/**/*.js',
    ],
};
