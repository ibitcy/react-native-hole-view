export declare class RNIbitShareOptionContainer {
    shareOption: RNIbitShareOption;
    galleryIdRecommended: boolean;
}
export declare enum RNIbitShareOption {
    TikTok = "tiktok",
    Instagram = "instagram",
    Facebook = "facebook",
    Snapchat = "snapchat",
    Youtube = "youtube",
    Twitter = "twitter"
}
export declare class RNIbitShare {
    static getShareOptions(): Promise<RNIbitShareOptionContainer[]>;
    static shareVideo(videoURL: string | null, videoId: string | null, shareOption: RNIbitShareOption, description: string | null): Promise<boolean>;
}
