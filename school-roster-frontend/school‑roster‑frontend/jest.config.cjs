// jest.config.cjs
module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jest-environment-jsdom',
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json'],
  transform: {
    '^.+\\.(ts|tsx)$': 'ts-jest',
  },
  moduleNameMapper: {
    // CSS modules (files ending in .module.css)
    '\\.module\\.css$': 'identity-obj-proxy',
    // And if you import any plain CSS/SCSS elsewhere:
    '\\.(css|scss|sass)$': 'identity-obj-proxy',
  },
  testMatch: ['<rootDir>/src/**/*.test.(ts|tsx|js)'],
};
