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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Calendar;

public class PerfTracker {
    private static final String TAG = PerfTracker.class.getSimpleName();
    private final Logger logger;
    private long startTimer = 0L;
    private long lastTimer = this.startTimer;

    public PerfTracker(String tag) {
        this.logger = LoggerFactory.getLogger(tag);
    }

    public PerfTracker(Logger logger) {
        this.logger = logger;
    }

    public void start(String message, Object... objects) {
        reset();
        log(this.lastTimer - this.startTimer, message, objects);
    }

    public void start() {
        start("start");
    }

    public long stop(String message, Object... objects) {
        this.lastTimer = Calendar.getInstance().getTimeInMillis();
        long ts = this.lastTimer - this.startTimer;
        log(ts, message, objects);
        reset();
        return ts;
    }

    public long stop() {
        return stop("stop");
    }

    public long track(String message, Object... objects) {
        long t = Calendar.getInstance().getTimeInMillis();
        long ts = t - this.lastTimer;
        log(ts, message, objects);
        this.lastTimer = t;
        return ts;
    }

    public long track() {
        return track("track");
    }

    public void reset() {
        this.startTimer = Calendar.getInstance().getTimeInMillis();
        this.lastTimer = this.startTimer;
    }

    private void log(long timeSpan, String message, Object[] objects) {
        String str = null;
        if (timeSpan > 0L)
            str = MessageFormat.format("[{0}] {1}ms | ", TAG,
                    Long.valueOf(timeSpan));
        else {
            str = MessageFormat.format("[{0}] ", TAG);
        }
        this.logger.info(str + message, objects);
    }
}
