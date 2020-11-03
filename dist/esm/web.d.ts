import { WebPlugin } from '@capacitor/core';
import { JpushPlugin } from './definitions';
export declare class JpushWeb extends WebPlugin implements JpushPlugin {
    constructor();
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
declare const Jpush: JpushWeb;
export { Jpush };
