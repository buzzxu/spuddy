package io.github.buzzxu.spuddy.dal.jdbi;

import com.google.common.collect.Sets;
import io.github.buzzxu.spuddy.Plugin;
import io.github.buzzxu.spuddy.annotations.Named;

import java.util.Set;

/**
 * Created by xux on 2017/4/11.
 */
@Named("dal-jdbi")
public class JDBIPlugin implements Plugin {

    @Override
    public Set<Class<?>> classes() {
        return Sets.newHashSet(JDBIConfigure.class);
    }
}
