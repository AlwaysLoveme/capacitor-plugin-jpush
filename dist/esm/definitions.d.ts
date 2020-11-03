declare module '@capacitor/core' {
    interface PluginRegistry {
        Jpush: JpushPlugin;
    }
}
export interface JpushPlugin {
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
    startPushSDK(): Promise<any>;
}
