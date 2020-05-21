"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.RNHoleView = void 0;
const react_native_1 = require("react-native");
const React = require("react");
const RNHoleViewManager = react_native_1.requireNativeComponent('RNHoleView');
exports.RNHoleView = props => {
    return React.createElement(RNHoleViewManager, Object.assign({}, props));
};
//# sourceMappingURL=index.js.map