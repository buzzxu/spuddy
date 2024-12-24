/**
 * Copyright (C) 2013 Phoenix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.buzzxu.spuddy.util;

import com.google.common.base.Throwables;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 保证只执行一次
 * @author xux
 *
 */
public class OnlyOnceCondition {

    public static OnlyOnceCondition create(String message) {
        return new OnlyOnceCondition(message);
    }

    private final String message;

    private OnlyOnceCondition(String message) {
        this.message = message;
    }

    private final AtomicBoolean hasChecked = new AtomicBoolean(false);

    public void check() {
        if (!hasChecked.compareAndSet(false, true))
            Throwables.throwIfUnchecked(new RuntimeException(message));
    }
}
