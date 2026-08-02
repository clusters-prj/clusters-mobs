package com.kaguya.custommobs.ai;

import com.kaguya.custommobs.model.AiBehaviorConfig;
import com.kaguya.custommobs.model.CustomMobInstance;

public interface AiBehavior {
    /**
     * 毎Tick(または間引きTick)呼ばれる処理。
     * @param mob 対象Mobインスタンス
     * @param config このビヘイビアに紐づくYAML設定
     * @param nowTick 現在のサーバーTickカウント(独自カウンタでOK)
     */
    void tick(CustomMobInstance mob, AiBehaviorConfig config, long nowTick);
}
