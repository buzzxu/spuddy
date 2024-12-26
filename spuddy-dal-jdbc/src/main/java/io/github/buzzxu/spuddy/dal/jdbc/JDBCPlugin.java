package io.github.buzzxu.spuddy.dal.jdbc;



import io.github.buzzxu.spuddy.Plugin;
import io.github.buzzxu.spuddy.annotations.Named;

import java.util.Set;

@Named("jdbc")
public class JDBCPlugin implements Plugin {

    @Override
    public Set<Class<?>> classes() {
        return Set.of(JDBCConfigure.class);
    }
}
