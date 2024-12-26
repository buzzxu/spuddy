package io.github.buzzxu.spuddy.security.services;

import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.security.OrganizationService;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

/**
 * @author xux
 * @date 2024年12月26日 12:34:57
 */
public class StandardOrganization extends AbstractStandard implements OrganizationService {
    @Override
    public boolean exists(int orgId) {
        try {
            return qr.query("SELECT EXISTS (SELECT * FROM t_org WHERE id = ?)", new ScalarHandler<Long>(1), orgId) > 0;
        } catch (SQLException ex) {
            throw ApplicationException.raise(ex);
        }
    }
}
