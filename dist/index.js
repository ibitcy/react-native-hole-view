"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const react_native_1 = require("react-native");
class RNIbitShareOptionContainer {
}
exports.RNIbitShareOptionContainer = RNIbitShareOptionContainer;
var RNIbitShareOption;
(function (RNIbitShareOption) {
    RNIbitShareOption["TikTok"] = "tiktok";
    RNIbitShareOption["Instagram"] = "instagram";
    RNIbitShareOption["Facebook"] = "facebook";
    RNIbitShareOption["Snapchat"] = "snapchat";
    RNIbitShareOption["Youtube"] = "youtube";
    RNIbitShareOption["Twitter"] = "twitter";
})(RNIbitShareOption = exports.RNIbitShareOption || (exports.RNIbitShareOption = {}));
class RNIbitShare {
    static getShareOptions() {
        return react_native_1.NativeModules.RNIbitShare.getShareOptions();
    }
    static shareVideo(videoURL, videoId, shareOption, description) {
        return react_native_1.NativeModules.RNIbitShare.shareVideo(videoURL, videoId, shareOption, description);
    }
}
exports.RNIbitShare = RNIbitShare;
//# sourceMappingURL=index.js.map