const tsParser = require('@typescript-eslint/parser');
const tsPlugin = require('@typescript-eslint/eslint-plugin');
const reactPlugin = require('eslint-plugin-react');
const rnPlugin = require('eslint-plugin-react-native');

module.exports = [
  {
    files: ['src/**/*.{ts,tsx}'],
    languageOptions: {
      parser: tsParser,
      parserOptions: {
        ecmaVersion: 2020,
        sourceType: 'module',
        project: './tsconfig.json',
      },
    },
    plugins: {
      '@typescript-eslint': tsPlugin,
      'react': reactPlugin,
      'react-native': rnPlugin,
    },
    rules: {
      '@typescript-eslint/no-unused-vars': 'warn',
      'react-native/no-inline-styles': 'warn',
    },
  },
  {
    files: ['**/*.{js,jsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      sourceType: 'module',
    },
    plugins: {
      'react': reactPlugin,
      'react-native': rnPlugin,
    },
    rules: {
      'react-native/no-inline-styles': 'warn',
    },
  },
];
