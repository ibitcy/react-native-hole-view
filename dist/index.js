"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.RNHoleView = void 0;
const react_native_1 = require("react-native");
const React = require("react");
const RNHoleViewManager = react_native_1.requireNativeComponent('RNHoleViewManager');
exports.RNHoleView = props => {
    return React.createElement(RNHoleViewManager, Object.assign({}, this.props));
};
//# sourceMappingURL=index.js.map