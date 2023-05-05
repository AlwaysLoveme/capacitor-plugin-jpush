import {WebPlugin} from '@capacitor/core';

import type {JPushPlugin, PermissionStatus} from './definitions';

export class JPushWeb extends WebPlugin implements JPushPlugin {
    deleteTags(): Promise<void> {
        throw this.unimplemented('Method not supported on Web.');
    }

    cleanTags(): Promise<void> {
        throw this.unimplemented('Method not supported on Web.');
    }

    setBadgeNumber(): Promise<void> {
        throw this.unimplemented('Method not supported on Web.');
    }

    async setAlias(): Promise<void> {
        throw this.unimplemented('Method not supported on Web.');
    }

    async deleteAlias(): Promise<void> {
        throw this.unimplemented('Method not supported on Web.');
    }

    async removeListeners(): Promise<void> {
        throw this.unimplemented('Method not supported on Web.');
    }

    async addTags(): Promise<void> {
        throw this.unimplemented('Method not supported on Web.');
    }

    async getRegistrationID(): Promise<{ registrationId: string }> {
        throw this.unimplemented('Method not supported on Web.');
    }

    async setDebugMode(): Promise<void> {
        throw this.unimplemented('Method not supported on Web.');
    }

    async checkPermissions(): Promise<PermissionStatus> {
        throw this.unimplemented('Method not supported on Web.');
    }

    async requestPermissions(): Promise<PermissionStatus> {
        throw this.unimplemented('Method not supported on Web.');
    }

    async openNotificationSetting(): Promise<void> {
        throw this.unimplemented('Method not supported on Web.');
    }
}
